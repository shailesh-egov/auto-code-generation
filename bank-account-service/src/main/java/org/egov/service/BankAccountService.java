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
     * Creates bank accounts for the given request
     * 
     * @param request BankAccountRequest containing the bank accounts to create
     * @return List of created bank accounts
     */
    public List<BankAccount> createBankAccount(BankAccountRequest request) {
        log.info("Creating bank accounts for tenant: {}", 
                request.getBankAccounts().get(0).getTenantId());

        // Validate the request
        validator.validateCreateRequest(request);

        // Enrich the request with IDs and audit details
        enrichmentService.enrichCreateRequest(request);

        // Encrypt PII data
        encryptionService.encryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Save to database
        repository.save(request.getBankAccounts());

        // Decrypt for response
        encryptionService.decryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Send to Kafka
        producer.push(configuration.getSaveBankAccountTopic(), request);

        log.info("Successfully created {} bank accounts", 
                request.getBankAccounts().size());
        
        return request.getBankAccounts();
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
     * Updates bank accounts for the given request
     * 
     * @param request BankAccountRequest containing the bank accounts to update
     * @return List of updated bank accounts
     */
    public List<BankAccount> updateBankAccount(BankAccountRequest request) {
        log.info("Updating bank accounts for tenant: {}", 
                request.getBankAccounts().get(0).getTenantId());

        // Validate the update request
        validator.validateUpdateRequest(request);

        // Enrich the request with audit details
        enrichmentService.enrichUpdateRequest(request);

        // Encrypt PII data
        encryptionService.encryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Update in database
        repository.update(request.getBankAccounts());

        // Decrypt for response
        encryptionService.decryptBankAccountData(request.getBankAccounts(), 
                request.getRequestInfo());

        // Send to Kafka
        producer.push(configuration.getUpdateBankAccountTopic(), request);

        log.info("Successfully updated {} bank accounts", 
                request.getBankAccounts().size());
        
        return request.getBankAccounts();
    }
}