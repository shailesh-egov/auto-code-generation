package org.egov.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.certificate.util.IdgenUtil;
import org.egov.certificate.web.models.Certificate;
import org.egov.certificate.web.models.Proof;
import org.egov.certificate.web.models.RevocationDetail;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    private IdgenUtil idgenUtil;

    @Autowired
    private CertificateConfiguration configuration;

    public void enrichCertificatesForCreate(List<Certificate> certificates, RequestInfo requestInfo) {
        log.info("Enriching {} certificates for creation", certificates.size());

        for (Certificate certificate : certificates) {
            // Generate certificate ID
            if (certificate.getId() == null) {
                List<String> certificateIds = idgenUtil.getIdList(requestInfo, certificate.getTenantId(),
                        configuration.getIdGenName(), configuration.getIdGenFormat(), 1);
                certificate.setId(certificateIds.get(0));
            }

            // Set issued timestamp
            Long currentTime = System.currentTimeMillis();
            certificate.setIssued(currentTime);

            // Set default status if not provided
            if (certificate.getStatus() == null) {
                certificate.setStatus(CertificateConfiguration.CERTIFICATE_STATUS_ACTIVE);
            }

            // Set default context if not provided
            if (certificate.getContext() == null) {
                certificate.setContext(configuration.getDefaultContext());
            }

            // Create digital proof (placeholder for now)
            if (certificate.getProof() == null) {
                certificate.setProof(createDigitalProof(certificate, currentTime));
            }

            // Set audit details
            String userId = getUserId(requestInfo);
            AuditDetails auditDetails = buildAuditDetails(userId, currentTime);
            certificate.setAuditDetails(auditDetails);
        }

        log.info("Successfully enriched {} certificates for creation", certificates.size());
    }

    public void enrichCertificatesForUpdate(List<Certificate> certificates, RequestInfo requestInfo) {
        log.info("Enriching {} certificates for update", certificates.size());

        Long currentTime = System.currentTimeMillis();

        for (Certificate certificate : certificates) {
            // Update audit details
            String userId = getUserId(requestInfo);
            if (certificate.getAuditDetails() != null) {
                certificate.getAuditDetails().setLastModifiedBy(userId);
                certificate.getAuditDetails().setLastModifiedTime(currentTime);
            } else {
                // This shouldn't happen for updates, but handle gracefully
                AuditDetails auditDetails = buildAuditDetails(userId, currentTime);
                certificate.setAuditDetails(auditDetails);
            }

            // Update proof if certificate data changed
            if (certificate.getProof() != null) {
                certificate.setProof(createDigitalProof(certificate, currentTime));
            }
        }

        log.info("Successfully enriched {} certificates for update", certificates.size());
    }

    public void enrichCertificatesForRevocation(List<Certificate> certificates, 
                                               List<RevocationDetail> revocationDetails, 
                                               RequestInfo requestInfo) {
        log.info("Enriching {} certificates for revocation", certificates.size());

        Long currentTime = System.currentTimeMillis();

        for (Certificate certificate : certificates) {
            // Set status to REVOKED
            certificate.setStatus(CertificateConfiguration.CERTIFICATE_STATUS_REVOKED);

            // Update audit details
            String userId = getUserId(requestInfo);
            if (certificate.getAuditDetails() != null) {
                certificate.getAuditDetails().setLastModifiedBy(userId);
                certificate.getAuditDetails().setLastModifiedTime(currentTime);
            }

            // Add revocation reason to additional details
            RevocationDetail revocationDetail = revocationDetails.stream()
                    .filter(detail -> detail.getCertificateId().equals(certificate.getId()))
                    .findFirst()
                    .orElse(null);

            if (revocationDetail != null) {
                if (certificate.getAdditionalDetails() == null) {
                    certificate.setAdditionalDetails(new java.util.HashMap<>());
                }
                certificate.getAdditionalDetails().put("revocationReason", revocationDetail.getReason());
                certificate.getAdditionalDetails().put("revocationTimestamp", currentTime);
            }
        }

        log.info("Successfully enriched {} certificates for revocation", certificates.size());
    }

    private Proof createDigitalProof(Certificate certificate, Long currentTime) {
        // This is a placeholder implementation
        // In a real implementation, this would create actual digital signatures
        return Proof.builder()
                .id(UUID.randomUUID().toString())
                .type(configuration.getDefaultSignatureAlgorithm())
                .created(currentTime)
                .proofPurpose(configuration.getDefaultProofPurpose())
                .verificationMethod(certificate.getIssuer().getId() + "#key-1")
                .signatureValue(generateMockSignature(certificate))
                .build();
    }

    private String generateMockSignature(Certificate certificate) {
        // This is a placeholder implementation
        // In a real implementation, this would generate actual cryptographic signatures
        return "z" + UUID.randomUUID().toString().replace("-", "") + 
               Integer.toHexString(certificate.hashCode());
    }

    private AuditDetails buildAuditDetails(String userId, Long currentTime) {
        return AuditDetails.builder()
                .createdBy(userId)
                .lastModifiedBy(userId)
                .createdTime(currentTime)
                .lastModifiedTime(currentTime)
                .build();
    }

    private String getUserId(RequestInfo requestInfo) {
        if (requestInfo != null && requestInfo.getUserInfo() != null && requestInfo.getUserInfo().getUuid() != null) {
            return requestInfo.getUserInfo().getUuid();
        }
        // Fallback for system operations or when user info is not available
        return "SYSTEM";
    }
}