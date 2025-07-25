package org.egov.certificate.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.web.models.Certificate;
import org.egov.certificate.web.models.Issuer;
import org.egov.certificate.web.models.Proof;
import org.egov.common.contract.models.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class CertificateRowMapper implements ResultSetExtractor<List<Certificate>> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Certificate> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Certificate> certificateMap = new LinkedHashMap<>();

        while (rs.next()) {
            String certificateId = rs.getString("id");
            Certificate certificate = certificateMap.get(certificateId);

            if (certificate == null) {
                certificate = buildCertificate(rs);
                certificateMap.put(certificateId, certificate);
            }

            // Add proof if exists
            String proofId = rs.getString("proof_id");
            if (proofId != null && certificate.getProof() == null) {
                certificate.setProof(buildProof(rs));
            }
        }

        return new ArrayList<>(certificateMap.values());
    }

    private Certificate buildCertificate(ResultSet rs) throws SQLException {
        return Certificate.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenant_id"))
                .context(rs.getString("context"))
                .type(rs.getString("certificate_type"))
                .issuer(buildIssuer(rs))
                .issued(rs.getLong("issued_at"))
                .expirationDate(rs.getObject("expires_at") != null ? rs.getLong("expires_at") : null)
                .status(rs.getString("status"))
                .credentialSubject(parseJsonb(rs, "credential_subject"))
                .additionalDetails(parseJsonb(rs, "additional_details"))
                .auditDetails(buildAuditDetails(rs))
                .build();
    }

    private Issuer buildIssuer(ResultSet rs) throws SQLException {
        return Issuer.builder()
                .id(rs.getString("issuer_id"))
                .name(rs.getString("issuer_name"))
                .type(rs.getString("issuer_type"))
                .build();
    }

    private Proof buildProof(ResultSet rs) throws SQLException {
        return Proof.builder()
                .type(rs.getString("proof_type"))
                .created(rs.getLong("proof_created_at"))
                .proofPurpose(rs.getString("proof_purpose"))
                .verificationMethod(rs.getString("verification_method"))
                .signatureValue(rs.getString("signature_value"))
                .additionalDetails(parseJsonb(rs, "proof_additional_details"))
                .build();
    }

    private AuditDetails buildAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                .createdBy(rs.getString("created_by"))
                .lastModifiedBy(rs.getString("last_modified_by"))
                .createdTime(rs.getLong("created_time"))
                .lastModifiedTime(rs.getLong("last_modified_time"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonb(ResultSet rs, String columnName) throws SQLException {
        try {
            PGobject pGobject = (PGobject) rs.getObject(columnName);
            if (pGobject == null || pGobject.getValue() == null) {
                return new HashMap<>();
            }
            
            return objectMapper.readValue(pGobject.getValue(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error parsing JSONB column {}: {}", columnName, e.getMessage());
            return new HashMap<>();
        }
    }
}