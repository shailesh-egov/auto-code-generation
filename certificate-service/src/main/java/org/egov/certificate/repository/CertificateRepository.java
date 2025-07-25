package org.egov.certificate.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.web.models.Certificate;
import org.egov.certificate.web.models.CertificateSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class CertificateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CertificateQueryBuilder queryBuilder;

    @Autowired
    private CertificateRowMapper rowMapper;

    public List<Certificate> getCertificates(CertificateSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getCertificateSearchQuery(searchCriteria, preparedStmtList);
        
        log.debug("Executing certificate search query: {}", query);
        log.debug("With parameters: {}", preparedStmtList);
        
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    public Integer getCertificateCount(CertificateSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getCertificateCountQuery(searchCriteria, preparedStmtList);
        
        log.debug("Executing certificate count query: {}", query);
        log.debug("With parameters: {}", preparedStmtList);
        
        Integer count = jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count != null ? count : 0;
    }

    public Certificate getCertificateById(String tenantId, String certificateId) {
        CertificateSearchCriteria criteria = CertificateSearchCriteria.builder()
                .tenantId(tenantId)
                .ids(List.of(certificateId))
                .limit(1)
                .offset(0)
                .build();

        List<Certificate> certificates = getCertificates(criteria);
        return certificates.isEmpty() ? null : certificates.get(0);
    }

    public List<Certificate> getCertificatesByIds(String tenantId, List<String> certificateIds) {
        if (certificateIds == null || certificateIds.isEmpty()) {
            return new ArrayList<>();
        }

        CertificateSearchCriteria criteria = CertificateSearchCriteria.builder()
                .tenantId(tenantId)
                .ids(certificateIds)
                .limit(certificateIds.size())
                .offset(0)
                .build();

        return getCertificates(criteria);
    }

    public boolean existsCertificate(String tenantId, String certificateId) {
        String query = "SELECT COUNT(1) FROM eg_certificate WHERE tenant_id = ? AND id = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{tenantId, certificateId}, Integer.class);
        return count != null && count > 0;
    }

    public boolean existsCertificateBySubjectId(String tenantId, String subjectId, String certificateType) {
        String query = "SELECT COUNT(1) FROM eg_certificate WHERE tenant_id = ? AND subject_id = ? AND certificate_type = ? AND status = 'ACTIVE'";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{tenantId, subjectId, certificateType}, Integer.class);
        return count != null && count > 0;
    }
}