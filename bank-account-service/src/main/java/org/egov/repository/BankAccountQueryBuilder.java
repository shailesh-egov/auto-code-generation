package org.egov.repository;

import org.egov.common.contract.models.Document;
import org.egov.web.models.BankAccount;
import org.egov.web.models.BankAccountDetails;
import org.egov.web.models.BankAccountSearchCriteria;
import org.egov.web.models.BankBranchIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BankAccountQueryBuilder {

    // Base queries for insert operations
    private static final String INSERT_BANK_ACCOUNT_QUERY = 
        "INSERT INTO eg_bank_account (id, tenant_id, service_code, reference_id, additional_details, " +
        "created_by, last_modified_by, created_time, last_modified_time) " +
        "VALUES (?, ?, ?, ?, ?::JSONB, ?, ?, ?, ?)";

    private static final String INSERT_BANK_ACCOUNT_DETAIL_QUERY = 
        "INSERT INTO eg_bank_account_detail (id, tenant_id, bank_account_id, account_holder_name, " +
        "account_number, account_type, is_primary, is_active, additional_details, " +
        "created_by, last_modified_by, created_time, last_modified_time) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::JSONB, ?, ?, ?, ?)";

    private static final String INSERT_BANK_BRANCH_IDENTIFIER_QUERY = 
        "INSERT INTO eg_bank_branch_identifier (id, bank_account_detail_id, type, code, additional_details) " +
        "VALUES (?, ?, ?, ?, ?::JSONB)";

    private static final String INSERT_BANK_ACCOUNT_DOCUMENT_QUERY = 
        "INSERT INTO eg_bank_accounts_doc (id, bank_account_detail_id, document_type, file_store, " +
        "document_uid, additional_details) " +
        "VALUES (?, ?, ?, ?, ?, ?::JSONB)";

    // Base queries for update operations
    private static final String UPDATE_BANK_ACCOUNT_QUERY = 
        "UPDATE eg_bank_account SET service_code = ?, reference_id = ?, additional_details = ?::JSONB, " +
        "last_modified_by = ?, last_modified_time = ? WHERE id = ?";

    private static final String UPDATE_BANK_ACCOUNT_DETAIL_QUERY = 
        "UPDATE eg_bank_account_detail SET account_holder_name = ?, account_number = ?, " +
        "account_type = ?, is_primary = ?, is_active = ?, additional_details = ?::JSONB, " +
        "last_modified_by = ?, last_modified_time = ? WHERE id = ?";

    private static final String UPDATE_BANK_BRANCH_IDENTIFIER_QUERY = 
        "UPDATE eg_bank_branch_identifier SET type = ?, code = ?, additional_details = ?::JSONB " +
        "WHERE bank_account_detail_id = ?";

    // Base search query
    private static final String BASE_SEARCH_QUERY = 
        "SELECT ba.id as ba_id, ba.tenant_id as ba_tenant_id, ba.service_code, ba.reference_id, " +
        "ba.additional_details as ba_additional_details, ba.created_by as ba_created_by, " +
        "ba.last_modified_by as ba_last_modified_by, ba.created_time as ba_created_time, " +
        "ba.last_modified_time as ba_last_modified_time, " +
        "bad.id as bad_id, bad.tenant_id as bad_tenant_id, bad.account_holder_name, " +
        "bad.account_number, bad.account_type, bad.is_primary, bad.is_active, " +
        "bad.additional_details as bad_additional_details, bad.created_by as bad_created_by, " +
        "bad.last_modified_by as bad_last_modified_by, bad.created_time as bad_created_time, " +
        "bad.last_modified_time as bad_last_modified_time, " +
        "bbi.id as bbi_id, bbi.type as bbi_type, bbi.code as bbi_code, " +
        "bbi.additional_details as bbi_additional_details " +
        "FROM eg_bank_account ba " +
        "LEFT JOIN eg_bank_account_detail bad ON ba.id = bad.bank_account_id " +
        "LEFT JOIN eg_bank_branch_identifier bbi ON bad.id = bbi.bank_account_detail_id ";

    public String getInsertBankAccountQuery() {
        return INSERT_BANK_ACCOUNT_QUERY;
    }

    public String getInsertBankAccountDetailQuery() {
        return INSERT_BANK_ACCOUNT_DETAIL_QUERY;
    }

    public String getInsertBankBranchIdentifierQuery() {
        return INSERT_BANK_BRANCH_IDENTIFIER_QUERY;
    }

    public String getInsertBankAccountDocumentQuery() {
        return INSERT_BANK_ACCOUNT_DOCUMENT_QUERY;
    }

    public String getUpdateBankAccountQuery() {
        return UPDATE_BANK_ACCOUNT_QUERY;
    }

    public String getUpdateBankAccountDetailQuery() {
        return UPDATE_BANK_ACCOUNT_DETAIL_QUERY;
    }

    public String getUpdateBankBranchIdentifierQuery() {
        return UPDATE_BANK_BRANCH_IDENTIFIER_QUERY;
    }

    public List<Object> getBankAccountInsertParams(BankAccount bankAccount) {
        List<Object> params = new ArrayList<>();
        params.add(bankAccount.getId());
        params.add(bankAccount.getTenantId());
        params.add(bankAccount.getServiceCode());
        params.add(bankAccount.getReferenceId());
        params.add(bankAccount.getAdditionalFields());
        params.add(bankAccount.getAuditDetails().getCreatedBy());
        params.add(bankAccount.getAuditDetails().getLastModifiedBy());
        params.add(bankAccount.getAuditDetails().getCreatedTime());
        params.add(bankAccount.getAuditDetails().getLastModifiedTime());
        return params;
    }

    public List<Object> getBankAccountDetailInsertParams(BankAccountDetails detail, String bankAccountId) {
        List<Object> params = new ArrayList<>();
        params.add(detail.getId());
        params.add(detail.getTenantId());
        params.add(bankAccountId);
        params.add(detail.getAccountHolderName());
        params.add(detail.getAccountNumber());
        params.add(detail.getAccountType());
        params.add(detail.getIsPrimary());
        params.add(detail.getIsActive());
        params.add(detail.getAdditionalFields());
        params.add(detail.getAuditDetails().getCreatedBy());
        params.add(detail.getAuditDetails().getLastModifiedBy());
        params.add(detail.getAuditDetails().getCreatedTime());
        params.add(detail.getAuditDetails().getLastModifiedTime());
        return params;
    }

    public List<Object> getBankBranchIdentifierInsertParams(BankBranchIdentifier identifier, String detailId) {
        List<Object> params = new ArrayList<>();
        params.add(java.util.UUID.randomUUID().toString());
        params.add(detailId);
        params.add(identifier.getType());
        params.add(identifier.getCode());
        params.add(identifier.getAdditionalDetails());
        return params;
    }

    public List<Object> getBankAccountDocumentInsertParams(Document document, String detailId) {
        List<Object> params = new ArrayList<>();
        params.add(java.util.UUID.randomUUID().toString());
        params.add(detailId);
        params.add(document.getDocumentType());
        params.add(document.getFileStoreId());
        params.add(document.getDocumentUid());
        params.add(document.getAdditionalDetails());
        return params;
    }

    public List<Object> getBankAccountUpdateParams(BankAccount bankAccount) {
        List<Object> params = new ArrayList<>();
        params.add(bankAccount.getServiceCode());
        params.add(bankAccount.getReferenceId());
        params.add(bankAccount.getAdditionalFields());
        params.add(bankAccount.getAuditDetails().getLastModifiedBy());
        params.add(bankAccount.getAuditDetails().getLastModifiedTime());
        params.add(bankAccount.getId());
        return params;
    }

    public List<Object> getBankAccountDetailUpdateParams(BankAccountDetails detail) {
        List<Object> params = new ArrayList<>();
        params.add(detail.getAccountHolderName());
        params.add(detail.getAccountNumber());
        params.add(detail.getAccountType());
        params.add(detail.getIsPrimary());
        params.add(detail.getIsActive());
        params.add(detail.getAdditionalFields());
        params.add(detail.getAuditDetails().getLastModifiedBy());
        params.add(detail.getAuditDetails().getLastModifiedTime());
        params.add(detail.getId());
        return params;
    }

    public List<Object> getBankBranchIdentifierUpdateParams(BankBranchIdentifier identifier, String detailId) {
        List<Object> params = new ArrayList<>();
        params.add(identifier.getType());
        params.add(identifier.getCode());
        params.add(identifier.getAdditionalDetails());
        params.add(detailId);
        return params;
    }

    public String getBankAccountSearchQuery(BankAccountSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        
        addWhereClause(query, criteria, preparedStmtList);
        addOrderByClause(query);
        addPaginationClause(query, criteria, preparedStmtList);
        
        return query.toString();
    }

    private void addWhereClause(StringBuilder query, BankAccountSearchCriteria criteria, List<Object> preparedStmtList) {
        boolean isWhereAdded = false;
        
        if (StringUtils.hasText(criteria.getTenantId())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("ba.tenant_id = ?");
            preparedStmtList.add(criteria.getTenantId());
            isWhereAdded = true;
        }
        
        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("ba.id IN (").append(createQuery(criteria.getIds())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
            isWhereAdded = true;
        }
        
        if (StringUtils.hasText(criteria.getServiceCode())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("ba.service_code = ?");
            preparedStmtList.add(criteria.getServiceCode());
            isWhereAdded = true;
        }
        
        if (!CollectionUtils.isEmpty(criteria.getReferenceId())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("ba.reference_id IN (").append(createQuery(criteria.getReferenceId())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getReferenceId());
            isWhereAdded = true;
        }
        
        if (StringUtils.hasText(criteria.getAccountHolderName())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("bad.account_holder_name ILIKE ?");
            preparedStmtList.add("%" + criteria.getAccountHolderName() + "%");
            isWhereAdded = true;
        }
        
        if (!CollectionUtils.isEmpty(criteria.getAccountNumber())) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("bad.account_number IN (").append(createQuery(criteria.getAccountNumber())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getAccountNumber());
            isWhereAdded = true;
        }
        
        if (criteria.getIsActive() != null) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("bad.is_active = ?");
            preparedStmtList.add(criteria.getIsActive());
            isWhereAdded = true;
        }
        
        if (criteria.getIsPrimary() != null) {
            query.append(isWhereAdded ? " AND " : " WHERE ");
            query.append("bad.is_primary = ?");
            preparedStmtList.add(criteria.getIsPrimary());
        }
    }

    private void addOrderByClause(StringBuilder query) {
        query.append(" ORDER BY ba.created_time DESC");
    }

    private void addPaginationClause(StringBuilder query, BankAccountSearchCriteria criteria, List<Object> preparedStmtList) {
        query.append(" LIMIT ?");
        preparedStmtList.add(criteria.getLimit());
        
        query.append(" OFFSET ?");
        preparedStmtList.add(criteria.getOffset());
    }

    private String createQuery(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> list) {
        preparedStmtList.addAll(list);
    }
}