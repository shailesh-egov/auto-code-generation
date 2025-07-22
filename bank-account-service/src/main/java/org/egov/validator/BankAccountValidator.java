package org.egov.validator;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.config.BankAccountServiceConfiguration;
import org.egov.tracer.model.CustomException;
import org.egov.util.IndividualUtil;
import org.egov.util.OrganisationUtil;
import org.egov.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BankAccountValidator {

    private final BankAccountServiceConfiguration configuration;
    private final IndividualUtil individualUtil;
    private final OrganisationUtil organisationUtil;

    @Autowired
    public BankAccountValidator(BankAccountServiceConfiguration configuration,
                               IndividualUtil individualUtil,
                               OrganisationUtil organisationUtil) {
        this.configuration = configuration;
        this.individualUtil = individualUtil;
        this.organisationUtil = organisationUtil;
    }

    /**
     * Validates bank account create request
     */
    public void validateCreateRequest(BankAccountRequest request) {
        log.info("Validating bank account create request");

        Map<String, String> errorMap = new HashMap<>();

        // Validate request info
        validateRequestInfo(request.getRequestInfo(), errorMap);

        // Validate bank accounts
        validateBankAccounts(request.getBankAccounts(), errorMap, false);

        // Validate reference IDs with external services
        validateReferenceIds(request.getBankAccounts(), request.getRequestInfo(), errorMap);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

        log.info("Bank account create request validation completed successfully");
    }

    /**
     * Validates bank account update request
     */
    public void validateUpdateRequest(BankAccountRequest request) {
        log.info("Validating bank account update request");

        Map<String, String> errorMap = new HashMap<>();

        // Validate request info
        validateRequestInfo(request.getRequestInfo(), errorMap);

        // Validate bank accounts (for update, IDs must be present)
        validateBankAccounts(request.getBankAccounts(), errorMap, true);

        // Validate reference IDs with external services
        validateReferenceIds(request.getBankAccounts(), request.getRequestInfo(), errorMap);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

        log.info("Bank account update request validation completed successfully");
    }

    /**
     * Validates bank account search request
     */
    public void validateSearchRequest(RequestInfoWrapper requestInfoWrapper, 
                                     BankAccountSearchCriteria criteria) {
        log.info("Validating bank account search request");

        Map<String, String> errorMap = new HashMap<>();

        // Validate request info
        if (requestInfoWrapper.getRequestInfo() == null) {
            errorMap.put("REQUEST_INFO", "RequestInfo is mandatory for search");
        }

        // Validate search criteria
        validateSearchCriteria(criteria, errorMap);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

        log.info("Bank account search request validation completed successfully");
    }

    private void validateRequestInfo(RequestInfo requestInfo, Map<String, String> errorMap) {
        if (requestInfo == null) {
            errorMap.put("REQUEST_INFO", "RequestInfo is mandatory");
            return;
        }

        if (requestInfo.getUserInfo() == null) {
            errorMap.put("USERINFO", "UserInfo is mandatory");
            return;
        }

        if (!StringUtils.hasText(requestInfo.getUserInfo().getUuid())) {
            errorMap.put("USERINFO_UUID", "User UUID is mandatory");
        }
    }

    private void validateBankAccounts(List<BankAccount> bankAccounts, 
                                     Map<String, String> errorMap, 
                                     boolean isUpdate) {
        if (CollectionUtils.isEmpty(bankAccounts)) {
            errorMap.put("BANK_ACCOUNTS", "Bank accounts list cannot be empty");
            return;
        }

        for (int i = 0; i < bankAccounts.size(); i++) {
            BankAccount bankAccount = bankAccounts.get(i);
            String prefix = "BANK_ACCOUNT[" + i + "]";

            validateBankAccount(bankAccount, errorMap, prefix, isUpdate);
        }
    }

    private void validateBankAccount(BankAccount bankAccount, 
                                    Map<String, String> errorMap, 
                                    String prefix, 
                                    boolean isUpdate) {
        if (bankAccount == null) {
            errorMap.put(prefix, "Bank account cannot be null");
            return;
        }

        // Validate ID for update operations
        if (isUpdate && !StringUtils.hasText(bankAccount.getId())) {
            errorMap.put(prefix + ".ID", "Bank account ID is mandatory for update");
        }

        // Validate mandatory fields
        if (!StringUtils.hasText(bankAccount.getTenantId())) {
            errorMap.put(prefix + ".TENANT_ID", "Tenant ID is mandatory");
        }

        if (!StringUtils.hasText(bankAccount.getServiceCode())) {
            errorMap.put(prefix + ".SERVICE_CODE", "Service code is mandatory");
        }

        if (!StringUtils.hasText(bankAccount.getReferenceId())) {
            errorMap.put(prefix + ".REFERENCE_ID", "Reference ID is mandatory");
        }

        // Validate bank account details
        if (CollectionUtils.isEmpty(bankAccount.getBankAccountDetails())) {
            errorMap.put(prefix + ".BANK_ACCOUNT_DETAILS", "Bank account details cannot be empty");
        } else {
            validateBankAccountDetails(bankAccount.getBankAccountDetails(), errorMap, prefix, isUpdate);
        }
    }

    private void validateBankAccountDetails(List<BankAccountDetails> details, 
                                          Map<String, String> errorMap, 
                                          String prefix, 
                                          boolean isUpdate) {
        for (int i = 0; i < details.size(); i++) {
            BankAccountDetails detail = details.get(i);
            String detailPrefix = prefix + ".DETAIL[" + i + "]";

            if (detail == null) {
                errorMap.put(detailPrefix, "Bank account detail cannot be null");
                continue;
            }

            // Validate ID for update operations
            if (isUpdate && !StringUtils.hasText(detail.getId())) {
                errorMap.put(detailPrefix + ".ID", "Bank account detail ID is mandatory for update");
            }

            // Validate mandatory fields
            if (!StringUtils.hasText(detail.getTenantId())) {
                errorMap.put(detailPrefix + ".TENANT_ID", "Tenant ID is mandatory");
            }

            if (!StringUtils.hasText(detail.getAccountNumber())) {
                errorMap.put(detailPrefix + ".ACCOUNT_NUMBER", "Account number is mandatory");
            }

            // Validate bank branch identifier
            if (detail.getBankBranchIdentifier() == null) {
                errorMap.put(detailPrefix + ".BRANCH_IDENTIFIER", "Bank branch identifier is mandatory");
            } else {
                validateBankBranchIdentifier(detail.getBankBranchIdentifier(), errorMap, detailPrefix);
            }

            // Validate account type if provided
            if (StringUtils.hasText(detail.getAccountType())) {
                validateAccountType(detail.getAccountType(), errorMap, detailPrefix);
            }
        }
    }

    private void validateBankBranchIdentifier(BankBranchIdentifier identifier, 
                                            Map<String, String> errorMap, 
                                            String prefix) {
        if (!StringUtils.hasText(identifier.getType())) {
            errorMap.put(prefix + ".BRANCH_IDENTIFIER.TYPE", "Branch identifier type is mandatory");
        }

        if (!StringUtils.hasText(identifier.getCode())) {
            errorMap.put(prefix + ".BRANCH_IDENTIFIER.CODE", "Branch identifier code is mandatory");
        }

        // Validate identifier type values
        if (StringUtils.hasText(identifier.getType())) {
            if (!isValidBranchIdentifierType(identifier.getType())) {
                errorMap.put(prefix + ".BRANCH_IDENTIFIER.TYPE", 
                           "Invalid branch identifier type. Allowed values: IFSC, SWIFT");
            }
        }
    }

    private void validateSearchCriteria(BankAccountSearchCriteria criteria, Map<String, String> errorMap) {
        if (criteria == null) {
            errorMap.put("BANK_ACCOUNTS_SEARCH_CRITERIA_REQUEST", "Search criteria is mandatory");
            return;
        }

        if (!StringUtils.hasText(criteria.getTenantId())) {
            errorMap.put("TENANT_ID", "Tenant ID is mandatory for search");
        }

        // Validate pagination
        if (criteria.getLimit() != null && criteria.getLimit() > configuration.getMaxLimit()) {
            errorMap.put("SEARCH.LIMIT", "Search limit cannot exceed " + configuration.getMaxLimit());
        }
    }

    private void validateReferenceIds(List<BankAccount> bankAccounts, 
                                     RequestInfo requestInfo, 
                                     Map<String, String> errorMap) {
        for (BankAccount bankAccount : bankAccounts) {
            if ("IND".equals(bankAccount.getServiceCode())) {
                // Validate individual reference ID
                if (!individualUtil.validateIndividualId(bankAccount.getReferenceId(), 
                                                        bankAccount.getTenantId(), 
                                                        requestInfo)) {
                    errorMap.put("REFERENCE_ID.INDIVIDUAL", 
                               "Invalid individual reference ID: " + bankAccount.getReferenceId());
                }
            } else if ("ORG".equals(bankAccount.getServiceCode())) {
                // Validate organization reference ID
                if (!organisationUtil.validateOrganisationId(bankAccount.getReferenceId(), 
                                                            bankAccount.getTenantId(), 
                                                            requestInfo)) {
                    errorMap.put("REFERENCE_ID.ORGANIZATION", 
                               "Invalid organization reference ID: " + bankAccount.getReferenceId());
                }
            }
        }
    }

    private boolean isValidBranchIdentifierType(String type) {
        return "IFSC".equals(type) || "SWIFT".equals(type);
    }

    private void validateAccountType(String accountType, Map<String, String> errorMap, String prefix) {
        if (!isValidAccountType(accountType)) {
            errorMap.put(prefix + ".ACCOUNT_TYPE", 
                       "Invalid account type. Allowed values: SAVINGS, CURRENT, UPI, WALLET");
        }
    }

    private boolean isValidAccountType(String accountType) {
        return "SAVINGS".equals(accountType) || "CURRENT".equals(accountType) || 
               "UPI".equals(accountType) || "WALLET".equals(accountType);
    }
}