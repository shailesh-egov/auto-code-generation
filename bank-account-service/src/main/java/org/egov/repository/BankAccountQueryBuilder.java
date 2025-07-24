package org.egov.repository;

import org.egov.web.models.BankAccountSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BankAccountQueryBuilder {

    /**
     * NOTE: All INSERT/UPDATE queries have been removed as they are now handled
     * by the DIGIT persister service via Kafka messaging.
     * This class now only contains search query building functionality.
     */

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