package org.egov.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.web.models.BankAccount;
import org.egov.web.models.BankAccountDetails;
import org.egov.web.models.BankBranchIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class BankAccountRowMapper implements ResultSetExtractor<List<BankAccount>> {

    private final ObjectMapper objectMapper;

    @Autowired
    public BankAccountRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<BankAccount> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, BankAccount> bankAccountMap = new LinkedHashMap<>();
        Map<String, BankAccountDetails> detailsMap = new LinkedHashMap<>();

        while (rs.next()) {
            String bankAccountId = rs.getString("ba_id");
            String detailId = rs.getString("bad_id");

            BankAccount bankAccount = bankAccountMap.get(bankAccountId);
            if (bankAccount == null) {
                bankAccount = buildBankAccount(rs);
                bankAccountMap.put(bankAccountId, bankAccount);
            }

            if (detailId != null) {
                BankAccountDetails detail = detailsMap.get(detailId);
                if (detail == null) {
                    detail = buildBankAccountDetails(rs);
                    detailsMap.put(detailId, detail);
                    
                    if (bankAccount.getBankAccountDetails() == null) {
                        bankAccount.setBankAccountDetails(new ArrayList<>());
                    }
                    bankAccount.getBankAccountDetails().add(detail);
                }

                // Set bank branch identifier
                if (rs.getString("bbi_id") != null) {
                    BankBranchIdentifier identifier = buildBankBranchIdentifier(rs);
                    detail.setBankBranchIdentifier(identifier);
                }
            }
        }

        return new ArrayList<>(bankAccountMap.values());
    }

    private BankAccount buildBankAccount(ResultSet rs) throws SQLException {
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(rs.getString("ba_created_by"))
                .lastModifiedBy(rs.getString("ba_last_modified_by"))
                .createdTime(rs.getLong("ba_created_time"))
                .lastModifiedTime(rs.getLong("ba_last_modified_time"))
                .build();

        return BankAccount.builder()
                .id(rs.getString("ba_id"))
                .tenantId(rs.getString("ba_tenant_id"))
                .serviceCode(rs.getString("service_code"))
                .referenceId(rs.getString("reference_id"))
                .additionalFields(getAdditionalDetail(rs.getString("ba_additional_details")))
                .auditDetails(auditDetails)
                .build();
    }

    private BankAccountDetails buildBankAccountDetails(ResultSet rs) throws SQLException {
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(rs.getString("bad_created_by"))
                .lastModifiedBy(rs.getString("bad_last_modified_by"))
                .createdTime(rs.getLong("bad_created_time"))
                .lastModifiedTime(rs.getLong("bad_last_modified_time"))
                .build();

        return BankAccountDetails.builder()
                .id(rs.getString("bad_id"))
                .tenantId(rs.getString("bad_tenant_id"))
                .accountHolderName(rs.getString("account_holder_name"))
                .accountNumber(rs.getString("account_number"))
                .accountType(rs.getString("account_type"))
                .isPrimary(rs.getBoolean("is_primary"))
                .isActive(rs.getBoolean("is_active"))
                .additionalFields(getAdditionalDetail(rs.getString("bad_additional_details")))
                .auditDetails(auditDetails)
                .build();
    }

    private BankBranchIdentifier buildBankBranchIdentifier(ResultSet rs) throws SQLException {
        return BankBranchIdentifier.builder()
                .type(rs.getString("bbi_type"))
                .code(rs.getString("bbi_code"))
                .additionalDetails(getAdditionalDetail(rs.getString("bbi_additional_details")))
                .build();
    }

    private Object getAdditionalDetail(String additionalDetailsString) {
        if (additionalDetailsString == null || additionalDetailsString.isEmpty()) {
            return null;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(additionalDetailsString);
            return objectMapper.convertValue(jsonNode, Object.class);
        } catch (Exception e) {
            log.warn("Error parsing additional details: {}", additionalDetailsString, e);
            return null;
        }
    }
}