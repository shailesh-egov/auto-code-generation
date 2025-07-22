package org.egov.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.config.BankAccountServiceConfiguration;
import org.egov.util.IdgenUtil;
import org.egov.web.models.BankAccount;
import org.egov.web.models.BankAccountDetails;
import org.egov.web.models.BankAccountRequest;
import org.egov.web.models.BankAccountSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EnrichmentService {

    private final IdgenUtil idgenUtil;
    private final BankAccountServiceConfiguration configuration;

    @Autowired
    public EnrichmentService(IdgenUtil idgenUtil, BankAccountServiceConfiguration configuration) {
        this.idgenUtil = idgenUtil;
        this.configuration = configuration;
    }

    /**
     * Enriches bank account create request with IDs and audit details
     */
    public void enrichCreateRequest(BankAccountRequest request) {
        log.info("Enriching bank account create request");

        String userUuid = request.getRequestInfo().getUserInfo().getUuid();
        Long currentTime = System.currentTimeMillis();

        for (BankAccount bankAccount : request.getBankAccounts()) {
            // Generate ID for main bank account
            if (bankAccount.getId() == null) {
                bankAccount.setId(UUID.randomUUID().toString());
            }

            // Set audit details for main bank account
            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(userUuid)
                    .lastModifiedBy(userUuid)
                    .createdTime(currentTime)
                    .lastModifiedTime(currentTime)
                    .build();
            bankAccount.setAuditDetails(auditDetails);

            // Set persister pattern fields
            if (bankAccount.getRowVersion() == null) {
                bankAccount.setRowVersion(1);
            }
            if (bankAccount.getIsDeleted() == null) {
                bankAccount.setIsDeleted(false);
            }

            // Enrich bank account details
            if (bankAccount.getBankAccountDetails() != null) {
                for (BankAccountDetails detail : bankAccount.getBankAccountDetails()) {
                    enrichBankAccountDetail(detail, userUuid, currentTime);
                }
            }
        }

        log.info("Bank account create request enrichment completed");
    }

    /**
     * Enriches bank account update request with audit details
     */
    public void enrichUpdateRequest(BankAccountRequest request) {
        log.info("Enriching bank account update request");

        String userUuid = request.getRequestInfo().getUserInfo().getUuid();
        Long currentTime = System.currentTimeMillis();

        for (BankAccount bankAccount : request.getBankAccounts()) {
            // Update audit details for main bank account
            if (bankAccount.getAuditDetails() != null) {
                bankAccount.getAuditDetails().setLastModifiedBy(userUuid);
                bankAccount.getAuditDetails().setLastModifiedTime(currentTime);
            } else {
                // Create audit details if not present
                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(userUuid)
                        .lastModifiedBy(userUuid)
                        .createdTime(currentTime)
                        .lastModifiedTime(currentTime)
                        .build();
                bankAccount.setAuditDetails(auditDetails);
            }

            // Enrich bank account details
            if (bankAccount.getBankAccountDetails() != null) {
                for (BankAccountDetails detail : bankAccount.getBankAccountDetails()) {
                    enrichBankAccountDetailForUpdate(detail, userUuid, currentTime);
                }
            }
        }

        log.info("Bank account update request enrichment completed");
    }

    /**
     * Enriches search request with default pagination and other parameters
     */
    public void enrichSearchRequest(BankAccountSearchCriteria criteria) {
        log.info("Enriching bank account search request");

        // Set default offset if not provided
        if (criteria.getOffset() == null) {
            criteria.setOffset(configuration.getDefaultOffset());
        }

        // Set default limit if not provided
        if (criteria.getLimit() == null) {
            criteria.setLimit(configuration.getDefaultLimit());
        }

        // Ensure limit doesn't exceed maximum
        if (criteria.getLimit() > configuration.getMaxLimit()) {
            criteria.setLimit(configuration.getMaxLimit());
        }

        log.info("Search request enriched with offset: {} and limit: {}", 
                criteria.getOffset(), criteria.getLimit());
    }

    /**
     * Enriches bank account detail for create operation
     */
    private void enrichBankAccountDetail(BankAccountDetails detail, String userUuid, Long currentTime) {
        // Generate ID if not present
        if (detail.getId() == null) {
            detail.setId(UUID.randomUUID().toString());
        }

        // Set default values
        if (detail.getIsPrimary() == null) {
            detail.setIsPrimary(false);
        }

        if (detail.getIsActive() == null) {
            detail.setIsActive(true);
        }

        // Set audit details
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(userUuid)
                .lastModifiedBy(userUuid)
                .createdTime(currentTime)
                .lastModifiedTime(currentTime)
                .build();
        detail.setAuditDetails(auditDetails);

        // Set persister pattern fields
        if (detail.getRowVersion() == null) {
            detail.setRowVersion(1);
        }
        if (detail.getIsDeleted() == null) {
            detail.setIsDeleted(false);
        }

        // Enrich bank branch identifier if present
        if (detail.getBankBranchIdentifier() != null) {
            if (detail.getBankBranchIdentifier().getId() == null) {
                detail.getBankBranchIdentifier().setId(UUID.randomUUID().toString());
            }
            if (detail.getBankBranchIdentifier().getRowVersion() == null) {
                detail.getBankBranchIdentifier().setRowVersion(1);
            }
            if (detail.getBankBranchIdentifier().getIsDeleted() == null) {
                detail.getBankBranchIdentifier().setIsDeleted(false);
            }
        }
    }

    /**
     * Enriches bank account detail for update operation
     */
    private void enrichBankAccountDetailForUpdate(BankAccountDetails detail, String userUuid, Long currentTime) {
        // Generate ID if not present (for new details added during update)
        if (detail.getId() == null) {
            detail.setId(UUID.randomUUID().toString());
        }

        // Set default values if not present
        if (detail.getIsPrimary() == null) {
            detail.setIsPrimary(false);
        }

        if (detail.getIsActive() == null) {
            detail.setIsActive(true);
        }

        // Update audit details
        if (detail.getAuditDetails() != null) {
            detail.getAuditDetails().setLastModifiedBy(userUuid);
            detail.getAuditDetails().setLastModifiedTime(currentTime);
        } else {
            // Create audit details if not present (for new details)
            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(userUuid)
                    .lastModifiedBy(userUuid)
                    .createdTime(currentTime)
                    .lastModifiedTime(currentTime)
                    .build();
            detail.setAuditDetails(auditDetails);
        }

        // Update persister pattern fields
        if (detail.getRowVersion() != null) {
            detail.setRowVersion(detail.getRowVersion() + 1);
        } else {
            detail.setRowVersion(1);
        }
        if (detail.getIsDeleted() == null) {
            detail.setIsDeleted(false);
        }

        // Enrich bank branch identifier if present
        if (detail.getBankBranchIdentifier() != null) {
            if (detail.getBankBranchIdentifier().getId() == null) {
                detail.getBankBranchIdentifier().setId(UUID.randomUUID().toString());
            }
            if (detail.getBankBranchIdentifier().getRowVersion() != null) {
                detail.getBankBranchIdentifier().setRowVersion(detail.getBankBranchIdentifier().getRowVersion() + 1);
            } else {
                detail.getBankBranchIdentifier().setRowVersion(1);
            }
            if (detail.getBankBranchIdentifier().getIsDeleted() == null) {
                detail.getBankBranchIdentifier().setIsDeleted(false);
            }
        }
    }

    /**
     * Validates and ensures primary account logic
     * Only one account can be primary per bank account
     */
    public void validateAndEnrichPrimaryAccount(List<BankAccount> bankAccounts) {
        log.info("Validating and enriching primary account logic");

        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getBankAccountDetails() != null && !bankAccount.getBankAccountDetails().isEmpty()) {
                
                List<BankAccountDetails> details = bankAccount.getBankAccountDetails();
                boolean hasPrimary = details.stream().anyMatch(detail -> Boolean.TRUE.equals(detail.getIsPrimary()));
                
                // If no primary account is set, make the first active account primary
                if (!hasPrimary) {
                    for (BankAccountDetails detail : details) {
                        if (Boolean.TRUE.equals(detail.getIsActive())) {
                            detail.setIsPrimary(true);
                            log.info("Set account {} as primary for bank account {}", 
                                    detail.getId(), bankAccount.getId());
                            break;
                        }
                    }
                }
                
                // Ensure only one primary account exists
                boolean primaryFound = false;
                for (BankAccountDetails detail : details) {
                    if (Boolean.TRUE.equals(detail.getIsPrimary())) {
                        if (primaryFound) {
                            detail.setIsPrimary(false);
                            log.warn("Multiple primary accounts found. Removed primary flag from account {}", 
                                    detail.getId());
                        } else {
                            primaryFound = true;
                        }
                    }
                }
            }
        }

        log.info("Primary account validation and enrichment completed");
    }
}