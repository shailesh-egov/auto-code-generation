package org.egov.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.config.BankAccountServiceConfiguration;
import org.egov.util.EncryptionDecryptionUtil;
import org.egov.web.models.BankAccount;
import org.egov.web.models.BankAccountDetails;
import org.egov.web.models.BankAccountSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class EncryptionService {

    private final EncryptionDecryptionUtil encryptionUtil;
    private final BankAccountServiceConfiguration configuration;

    // Encryption attribute keys as per requirements
    private static final String BANK_ACCOUNT_ENCRYPT = "BankAccountEncrypt";
    private static final String BANK_ACCOUNT_NUMBER_ENCRYPT = "BankAccountNumberEncrypt";
    private static final String BANK_ACCOUNT_HOLDER_NAME_ENCRYPT = "BankAccountHolderNameEncrypt";
    private static final String BANK_ACCOUNT_DECRYPT = "BankAccountDecrypt";

    @Autowired
    public EncryptionService(EncryptionDecryptionUtil encryptionUtil,
                           BankAccountServiceConfiguration configuration) {
        this.encryptionUtil = encryptionUtil;
        this.configuration = configuration;
    }

    /**
     * Encrypts PII data in bank accounts before storing to database
     */
    public void encryptBankAccountData(List<BankAccount> bankAccounts, RequestInfo requestInfo) {
        log.info("Starting encryption of bank account PII data");

        if (CollectionUtils.isEmpty(bankAccounts)) {
            log.warn("No bank accounts provided for encryption");
            return;
        }

        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getBankAccountDetails() != null) {
                for (BankAccountDetails detail : bankAccount.getBankAccountDetails()) {
                    encryptBankAccountDetail(detail, bankAccount.getTenantId(), requestInfo);
                }
            }
        }

        log.info("Bank account PII data encryption completed for {} accounts", bankAccounts.size());
    }

    /**
     * Decrypts PII data in bank accounts after retrieving from database
     */
    public void decryptBankAccountData(List<BankAccount> bankAccounts, RequestInfo requestInfo) {
        log.info("Starting decryption of bank account PII data");

        if (CollectionUtils.isEmpty(bankAccounts)) {
            log.warn("No bank accounts provided for decryption");
            return;
        }

        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getBankAccountDetails() != null) {
                for (BankAccountDetails detail : bankAccount.getBankAccountDetails()) {
                    decryptBankAccountDetail(detail, bankAccount.getTenantId(), requestInfo);
                }
            }
        }

        log.info("Bank account PII data decryption completed for {} accounts", bankAccounts.size());
    }

    /**
     * Encrypts search criteria fields that contain PII
     */
    public void encryptSearchCriteria(BankAccountSearchCriteria criteria, RequestInfo requestInfo) {
        log.info("Encrypting search criteria PII data");

        String tenantId = criteria.getTenantId();

        // Encrypt account holder name if provided
        if (StringUtils.hasText(criteria.getAccountHolderName())) {
            try {
                String encryptedName = encryptionUtil.encryptValue(
                    criteria.getAccountHolderName(),
                    tenantId,
                    BANK_ACCOUNT_HOLDER_NAME_ENCRYPT,
                    requestInfo
                );
                criteria.setAccountHolderName(encryptedName);
                log.debug("Encrypted account holder name in search criteria");
            } catch (Exception e) {
                log.error("Failed to encrypt account holder name in search criteria", e);
            }
        }

        // Encrypt account numbers if provided
        if (!CollectionUtils.isEmpty(criteria.getAccountNumber())) {
            try {
                List<String> encryptedNumbers = encryptionUtil.encryptValues(
                    criteria.getAccountNumber(),
                    tenantId,
                    BANK_ACCOUNT_NUMBER_ENCRYPT,
                    requestInfo
                );
                criteria.setAccountNumber(encryptedNumbers);
                log.debug("Encrypted account numbers in search criteria");
            } catch (Exception e) {
                log.error("Failed to encrypt account numbers in search criteria", e);
            }
        }

        log.info("Search criteria PII encryption completed");
    }

    /**
     * Encrypts individual bank account detail
     */
    private void encryptBankAccountDetail(BankAccountDetails detail, String tenantId, RequestInfo requestInfo) {
        try {
            // Encrypt account number
            if (StringUtils.hasText(detail.getAccountNumber())) {
                String encryptedAccountNumber = encryptionUtil.encryptValue(
                    detail.getAccountNumber(),
                    tenantId,
                    BANK_ACCOUNT_NUMBER_ENCRYPT,
                    requestInfo
                );
                detail.setAccountNumber(encryptedAccountNumber);
                log.debug("Encrypted account number for detail ID: {}", detail.getId());
            }

            // Encrypt account holder name
            if (StringUtils.hasText(detail.getAccountHolderName())) {
                String encryptedHolderName = encryptionUtil.encryptValue(
                    detail.getAccountHolderName(),
                    tenantId,
                    BANK_ACCOUNT_HOLDER_NAME_ENCRYPT,
                    requestInfo
                );
                detail.setAccountHolderName(encryptedHolderName);
                log.debug("Encrypted account holder name for detail ID: {}", detail.getId());
            }

        } catch (Exception e) {
            log.error("Failed to encrypt bank account detail with ID: {}", detail.getId(), e);
            throw new RuntimeException("Encryption failed for bank account detail", e);
        }
    }

    /**
     * Decrypts individual bank account detail
     */
    private void decryptBankAccountDetail(BankAccountDetails detail, String tenantId, RequestInfo requestInfo) {
        try {
            // Decrypt account number
            if (StringUtils.hasText(detail.getAccountNumber())) {
                String decryptedAccountNumber = encryptionUtil.decryptValue(
                    detail.getAccountNumber(),
                    tenantId,
                    BANK_ACCOUNT_DECRYPT,
                    requestInfo
                );
                detail.setAccountNumber(decryptedAccountNumber);
                log.debug("Decrypted account number for detail ID: {}", detail.getId());
            }

            // Decrypt account holder name
            if (StringUtils.hasText(detail.getAccountHolderName())) {
                String decryptedHolderName = encryptionUtil.decryptValue(
                    detail.getAccountHolderName(),
                    tenantId,
                    BANK_ACCOUNT_DECRYPT,
                    requestInfo
                );
                detail.setAccountHolderName(decryptedHolderName);
                log.debug("Decrypted account holder name for detail ID: {}", detail.getId());
            }

        } catch (Exception e) {
            log.error("Failed to decrypt bank account detail with ID: {}", detail.getId(), e);
            // Continue processing other records even if one fails
            log.warn("Continuing with encrypted data for detail ID: {}", detail.getId());
        }
    }

    /**
     * Validates if encryption is required based on tenant configuration
     */
    private boolean isEncryptionRequired(String tenantId) {
        // Check if encryption is enabled for the tenant
        // This could be configurable based on tenant settings
        return true; // For now, always encrypt as per requirements
    }

    /**
     * Creates encryption context for the given tenant and request
     */
    private String getEncryptionContext(String tenantId, RequestInfo requestInfo) {
        // Create context for encryption service
        return tenantId;
    }
}