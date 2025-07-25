package org.egov.certificate.repository;

import org.egov.certificate.web.models.CertificateSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class CertificateQueryBuilder {

    private static final String BASE_QUERY = """
        SELECT 
            c.id, c.tenant_id, c.context, c.certificate_type, c.issuer_id, c.issuer_name, 
            c.issuer_type, c.subject_id, c.issued_at, c.expires_at, c.status, 
            c.credential_subject, c.additional_details, c.created_by, c.last_modified_by, 
            c.created_time, c.last_modified_time,
            p.id as proof_id, p.proof_type, p.created_at as proof_created_at, 
            p.proof_purpose, p.verification_method, p.signature_value, 
            p.additional_details as proof_additional_details
        FROM eg_certificate c
        LEFT JOIN eg_certificate_proof p ON c.id = p.certificate_id
        """;

    private static final String COUNT_QUERY = """
        SELECT COUNT(DISTINCT c.id) 
        FROM eg_certificate c
        """;

    private static final String ORDER_BY_QUERY = " ORDER BY c.{sortBy} {sortOrder}";

    public String getCertificateSearchQuery(CertificateSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        addWhereClause(criteria, preparedStmtList, query);
        addOrderByClause(criteria, query);
        addLimitAndOffset(criteria, preparedStmtList, query);
        return query.toString();
    }

    public String getCertificateCountQuery(CertificateSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(COUNT_QUERY);
        addWhereClause(criteria, preparedStmtList, query);
        return query.toString();
    }

    private void addWhereClause(CertificateSearchCriteria criteria, List<Object> preparedStmtList, StringBuilder query) {
        boolean isWhereAdded = false;

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.tenant_id = ?");
            preparedStmtList.add(criteria.getTenantId());
            isWhereAdded = true;
        }

        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.id IN (").append(createQuery(criteria.getIds())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
            isWhereAdded = true;
        }

        if (!CollectionUtils.isEmpty(criteria.getIssuerIds())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.issuer_id IN (").append(createQuery(criteria.getIssuerIds())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getIssuerIds());
            isWhereAdded = true;
        }

        if (!CollectionUtils.isEmpty(criteria.getSubjectIds())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.subject_id IN (").append(createQuery(criteria.getSubjectIds())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getSubjectIds());
            isWhereAdded = true;
        }

        if (!CollectionUtils.isEmpty(criteria.getCertificateTypes())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.certificate_type IN (").append(createQuery(criteria.getCertificateTypes())).append(")");
            addToPreparedStatement(preparedStmtList, criteria.getCertificateTypes());
            isWhereAdded = true;
        }

        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.status = ?");
            preparedStmtList.add(criteria.getStatus());
            isWhereAdded = true;
        }

        if (!ObjectUtils.isEmpty(criteria.getFromDate())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.issued_at >= ?");
            preparedStmtList.add(criteria.getFromDate());
            isWhereAdded = true;
        }

        if (!ObjectUtils.isEmpty(criteria.getToDate())) {
            addClauseIfRequired(query, isWhereAdded);
            query.append(" c.issued_at <= ?");
            preparedStmtList.add(criteria.getToDate());
            isWhereAdded = true;
        }
    }

    private void addOrderByClause(CertificateSearchCriteria criteria, StringBuilder query) {
        String sortBy = criteria.getSortBy();
        String sortOrder = criteria.getSortOrder();

        // Validate and set default values
        if (ObjectUtils.isEmpty(sortBy)) {
            sortBy = "issued_at";
        } else {
            // Map API field names to database column names
            switch (sortBy) {
                case "issued" -> sortBy = "issued_at";
                case "expirationDate" -> sortBy = "expires_at";
                case "status" -> sortBy = "status";
                case "issuer" -> sortBy = "issuer_name";
                default -> sortBy = "issued_at";
            }
        }

        if (ObjectUtils.isEmpty(sortOrder) || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "DESC";
        }

        String orderByClause = ORDER_BY_QUERY.replace("{sortBy}", sortBy).replace("{sortOrder}", sortOrder.toUpperCase());
        query.append(orderByClause);
    }

    private void addLimitAndOffset(CertificateSearchCriteria criteria, List<Object> preparedStmtList, StringBuilder query) {
        query.append(" LIMIT ?");
        preparedStmtList.add(criteria.getLimit());

        query.append(" OFFSET ?");
        preparedStmtList.add(criteria.getOffset());
    }

    private void addClauseIfRequired(StringBuilder query, boolean isWhereAdded) {
        if (isWhereAdded) {
            query.append(" AND");
        } else {
            query.append(" WHERE");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        preparedStmtList.addAll(ids);
    }
}