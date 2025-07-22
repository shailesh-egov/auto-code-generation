package org.egov.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.config.BankAccountServiceConfiguration;
import org.egov.producer.BankAccountProducer;
import org.egov.repository.BankAccountRepository;
import org.egov.validator.BankAccountValidator;
import org.egov.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BankAccountService {

    private final BankAccountServiceConfiguration configuration;
    private final BankAccountValidator validator;
    private final EnrichmentService enrichmentService;
    private final EncryptionService encryptionService;
    private final BankAccountRepository repository;
    private final BankAccountProducer producer;

    @Autowired
    public BankAccountService(BankAccountServiceConfiguration configuration,
                             BankAccountValidator validator,
                             EnrichmentService enrichmentService,
                             EncryptionService encryptionService,
                             BankAccountRepository repository,
                             BankAccountProducer producer) {
        this.configuration = configuration;
        this.validator = validator;
        this.enrichmentService = enrichmentService;
        this.encryptionService = encryptionService;
        this.repository = repository;
        this.producer = producer;
    }

    /**
     * Creates bank accounts for the given request (Async via Kafka)
     * 
     * @param request BankAccountRequest containing the bank accounts to create
     * @return List of enriched bank accounts
     */
    public List<BankAccount> createBankAccount(BankAccountRequest request) {
        log.info("Processing bank account creation for tenant: {}", 
                request.getBankAccounts().get(0).getTenantId());

        // Validate the request
        validator.validateCreateRequest(request);

        // Enrich the request with IDs, audit details, and default values
        enrichmentService.enrichCreateRequest(request);
        enrichmentService.validateAndEnrichPrimaryAccount(request.getBankAccounts());

        // Make a copy for response (before encryption)
        List<BankAccount> responseAccounts = copyBankAccounts(request.getBankAccounts());

        // Encrypt PII data for persistence
        encryptionService.encryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Publish to Kafka for async persistence via persister service
        producer.push(configuration.getSaveBankAccountTopic(), request);

        log.info("Bank account creation request published to Kafka for {} accounts", 
                request.getBankAccounts().size());
        
        return responseAccounts;
    }

    /**
     * Searches bank accounts based on the given criteria
     * 
     * @param requestInfoWrapper RequestInfo wrapper
     * @param searchCriteria Search criteria for bank accounts
     * @return List of matching bank accounts
     */
    public List<BankAccount> searchBankAccount(RequestInfoWrapper requestInfoWrapper, 
                                               BankAccountSearchCriteria searchCriteria) {
        log.info("Searching bank accounts for tenant: {}", searchCriteria.getTenantId());

        // Validate search request
        validator.validateSearchRequest(requestInfoWrapper, searchCriteria);

        // Apply default pagination if not provided
        enrichmentService.enrichSearchRequest(searchCriteria);

        // Encrypt search criteria if needed
        encryptionService.encryptSearchCriteria(searchCriteria, requestInfoWrapper.getRequestInfo());

        // Search from database
        List<BankAccount> bankAccounts = repository.getBankAccounts(searchCriteria);

        // Decrypt the response data
        if (!bankAccounts.isEmpty()) {
            encryptionService.decryptBankAccountData(bankAccounts, requestInfoWrapper.getRequestInfo());
        }

        log.info("Found {} bank accounts matching search criteria", bankAccounts.size());
        
        return bankAccounts;
    }

    /**
     * Updates bank accounts for the given request (Async via Kafka)
     * 
     * @param request BankAccountRequest containing the bank accounts to update
     * @return List of enriched bank accounts
     */
    public List<BankAccount> updateBankAccount(BankAccountRequest request) {
        log.info("Processing bank account update for tenant: {}", 
                request.getBankAccounts().get(0).getTenantId());

        // Validate the update request
        validator.validateUpdateRequest(request);

        // Enrich the request with audit details and default values
        enrichmentService.enrichUpdateRequest(request);
        enrichmentService.validateAndEnrichPrimaryAccount(request.getBankAccounts());

        // Make a copy for response (before encryption)
        List<BankAccount> responseAccounts = copyBankAccounts(request.getBankAccounts());

        // Encrypt PII data for persistence
        encryptionService.encryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Publish to Kafka for async persistence via persister service
        producer.push(configuration.getUpdateBankAccountTopic(), request);

        log.info("Bank account update request published to Kafka for {} accounts", 
                request.getBankAccounts().size());
        
        return responseAccounts;
    }

    /**
     * Creates a deep copy of bank accounts for response
     * This prevents issues with encryption modifying the response data
     */
    private List<BankAccount> copyBankAccounts(List<BankAccount> original) {
        // For now, return the original list
        // In production, implement proper deep copy to avoid encryption affecting response
        return original;
    }
}