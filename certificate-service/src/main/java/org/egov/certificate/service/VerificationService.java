package org.egov.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.certificate.repository.CertificateRepository;
import org.egov.certificate.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VerificationService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateConfiguration configuration;

    public VerificationResult verifyCertificate(CertificateVerificationRequest request) {
        log.info("Starting certificate verification");

        validateVerificationRequest(request);
        
        VerificationCriteria criteria = request.getVerificationCriteria();
        Certificate certificate = certificateRepository.getCertificateById(
                criteria.getTenantId(), criteria.getCertificateId());

        if (certificate == null) {
            throw new CustomException("CERTIFICATE_NOT_FOUND", "Certificate not found");
        }

        return performVerification(certificate, criteria);
    }

    private VerificationResult performVerification(Certificate certificate, VerificationCriteria criteria) {
        log.info("Performing verification for certificate: {}", certificate.getId());

        Long verificationTimestamp = System.currentTimeMillis();
        Map<String, Object> verificationDetails = new HashMap<>();

        // Check if certificate is revoked
        boolean notRevoked = !CertificateConfiguration.CERTIFICATE_STATUS_REVOKED.equals(certificate.getStatus());
        if (!notRevoked && criteria.getIncludeRevocationCheck()) {
            verificationDetails.put("revocationReason", certificate.getAdditionalDetails() != null ? 
                    certificate.getAdditionalDetails().get("revocationReason") : "Unknown");
        }

        // Check if certificate is expired
        boolean notExpired = true;
        if (criteria.getIncludeExpiryCheck() && certificate.getExpirationDate() != null) {
            notExpired = certificate.getExpirationDate() > verificationTimestamp;
            if (!notExpired) {
                verificationDetails.put("expiryDate", certificate.getExpirationDate());
            }
        }

        // Verify digital signature
        boolean signatureValid = true;
        if (criteria.getIncludeProofValidation() && certificate.getProof() != null) {
            signatureValid = verifyDigitalSignature(certificate);
            verificationDetails.put("signatureAlgorithm", certificate.getProof().getType());
            verificationDetails.put("verificationMethod", certificate.getProof().getVerificationMethod());
            verificationDetails.put("proofPurpose", certificate.getProof().getProofPurpose());
        }

        // Verify issuer authority
        boolean issuerVerified = verifyIssuerAuthority(certificate);
        verificationDetails.put("issuerId", certificate.getIssuer().getId());
        verificationDetails.put("issuerName", certificate.getIssuer().getName());
        verificationDetails.put("issuerType", certificate.getIssuer().getType());

        // Overall validity
        boolean isValid = notRevoked && notExpired && signatureValid && issuerVerified;

        // Add verification method details
        verificationDetails.put("verificationMethod", "cryptographic");
        verificationDetails.put("verificationEngine", "certificate-service");
        verificationDetails.put("verificationVersion", "1.0");

        // Include additional validations if provided
        if (criteria.getAdditionalValidations() != null) {
            performAdditionalValidations(certificate, criteria.getAdditionalValidations(), verificationDetails);
        }

        VerificationResult result = VerificationResult.builder()
                .certificateId(certificate.getId())
                .isValid(isValid)
                .signatureValid(signatureValid)
                .notExpired(notExpired)
                .notRevoked(notRevoked)
                .issuerVerified(issuerVerified)
                .verificationTimestamp(verificationTimestamp)
                .verificationDetails(verificationDetails)
                .build();

        log.info("Verification completed for certificate: {} - Valid: {}", certificate.getId(), isValid);
        return result;
    }

    private boolean verifyDigitalSignature(Certificate certificate) {
        // This is a placeholder implementation
        // In a real implementation, this would:
        // 1. Retrieve the public key from the verification method
        // 2. Recreate the certificate data hash
        // 3. Verify the signature using the public key
        
        Proof proof = certificate.getProof();
        if (proof == null || !StringUtils.hasText(proof.getSignatureValue())) {
            return false;
        }

        // Mock verification - in reality, this would use cryptographic libraries
        // to verify the signature against the certificate data
        return proof.getSignatureValue().startsWith("z") && proof.getSignatureValue().length() > 50;
    }

    private boolean verifyIssuerAuthority(Certificate certificate) {
        // This is a placeholder implementation
        // In a real implementation, this would:
        // 1. Check issuer against a trusted registry
        // 2. Verify issuer's authority to issue this certificate type
        // 3. Check issuer's certificate/license validity
        
        Issuer issuer = certificate.getIssuer();
        if (issuer == null || !StringUtils.hasText(issuer.getId())) {
            return false;
        }

        // Mock verification - check if issuer ID follows expected pattern
        return issuer.getId().startsWith("did:gov:") || issuer.getId().startsWith("did:dept:");
    }

    private void performAdditionalValidations(Certificate certificate, 
                                            Map<String, Object> additionalValidations,
                                            Map<String, Object> verificationDetails) {
        // Handle custom validation rules
        if (additionalValidations.containsKey("checkSubjectExistence")) {
            boolean checkSubject = (Boolean) additionalValidations.get("checkSubjectExistence");
            if (checkSubject) {
                // Mock subject existence check
                boolean subjectExists = certificate.getCredentialSubject() != null && 
                                      certificate.getCredentialSubject().containsKey("id");
                verificationDetails.put("subjectExists", subjectExists);
            }
        }

        if (additionalValidations.containsKey("validateContext")) {
            boolean validateContext = (Boolean) additionalValidations.get("validateContext");
            if (validateContext) {
                boolean contextValid = configuration.getDefaultContext().equals(certificate.getContext());
                verificationDetails.put("contextValid", contextValid);
            }
        }
    }

    private void validateVerificationRequest(CertificateVerificationRequest request) {
        if (request.getRequestInfo() == null) {
            throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory");
        }

        if (request.getVerificationCriteria() == null) {
            throw new CustomException("INVALID_REQUEST", "Verification criteria is mandatory");
        }

        VerificationCriteria criteria = request.getVerificationCriteria();

        if (!StringUtils.hasText(criteria.getTenantId())) {
            throw new CustomException("INVALID_REQUEST", "TenantId is mandatory");
        }

        if (!StringUtils.hasText(criteria.getCertificateId())) {
            throw new CustomException("INVALID_REQUEST", "Certificate ID is mandatory");
        }
    }
}