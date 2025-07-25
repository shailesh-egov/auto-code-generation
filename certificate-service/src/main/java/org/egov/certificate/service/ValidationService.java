package org.egov.certificate.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.certificate.repository.CertificateRepository;
import org.egov.certificate.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ValidationService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateConfiguration configuration;

    private static final List<String> VALID_STATUSES = Arrays.asList(
            CertificateConfiguration.CERTIFICATE_STATUS_ACTIVE,
            CertificateConfiguration.CERTIFICATE_STATUS_REVOKED,
            CertificateConfiguration.CERTIFICATE_STATUS_EXPIRED
    );

    private static final List<String> VALID_ISSUER_TYPES = Arrays.asList(
            CertificateConfiguration.ISSUER_TYPE_DEPARTMENT,
            CertificateConfiguration.ISSUER_TYPE_AGENCY,
            CertificateConfiguration.ISSUER_TYPE_RELIGIOUS_INSTITUTION
    );

    public void validateCertificateRequest(CertificateRequest request) {
        log.info("Validating certificate creation request");

        if (request.getRequestInfo() == null) {
            throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory");
        }

        if (CollectionUtils.isEmpty(request.getCertificates())) {
            throw new CustomException("INVALID_REQUEST", "Certificates list cannot be empty");
        }

        if (request.getCertificates().size() > 100) {
            throw new CustomException("INVALID_REQUEST", "Cannot create more than 100 certificates in a single request");
        }

        for (Certificate certificate : request.getCertificates()) {
            validateCertificateForCreate(certificate);
        }

        log.info("Certificate creation request validation completed successfully");
    }

    public void validateCertificateUpdateRequest(CertificateRequest request) {
        log.info("Validating certificate update request");

        if (request.getRequestInfo() == null) {
            throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory");
        }

        if (CollectionUtils.isEmpty(request.getCertificates())) {
            throw new CustomException("INVALID_REQUEST", "Certificates list cannot be empty");
        }

        for (Certificate certificate : request.getCertificates()) {
            validateCertificateForUpdate(certificate);
        }

        log.info("Certificate update request validation completed successfully");
    }

    public void validateSearchRequest(CertificateSearchRequest request) {
        log.info("Validating certificate search request");

        if (request.getRequestInfo() == null) {
            throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory");
        }

        if (request.getSearchCriteria() == null) {
            throw new CustomException("INVALID_REQUEST", "Search criteria is mandatory");
        }

        CertificateSearchCriteria criteria = request.getSearchCriteria();

        if (!StringUtils.hasText(criteria.getTenantId())) {
            throw new CustomException("INVALID_REQUEST", "TenantId is mandatory");
        }

        // Validate limit
        if (criteria.getLimit() != null && criteria.getLimit() > configuration.getMaxSearchLimit()) {
            throw new CustomException("INVALID_REQUEST", 
                    "Search limit cannot exceed " + configuration.getMaxSearchLimit());
        }

        // Validate status if provided
        if (StringUtils.hasText(criteria.getStatus()) && !VALID_STATUSES.contains(criteria.getStatus())) {
            throw new CustomException("INVALID_REQUEST", 
                    "Invalid status. Valid values are: " + String.join(", ", VALID_STATUSES));
        }

        // Validate date range
        if (criteria.getFromDate() != null && criteria.getToDate() != null && 
            criteria.getFromDate() > criteria.getToDate()) {
            throw new CustomException("INVALID_REQUEST", "FromDate cannot be greater than ToDate");
        }

        log.info("Certificate search request validation completed successfully");
    }

    public void validateRevocationRequest(CertificateRevocationRequest request) {
        log.info("Validating certificate revocation request");

        if (request.getRequestInfo() == null) {
            throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory");
        }

        if (CollectionUtils.isEmpty(request.getRevocationDetails())) {
            throw new CustomException("INVALID_REQUEST", "Revocation details list cannot be empty");
        }

        for (RevocationDetail detail : request.getRevocationDetails()) {
            validateRevocationDetail(detail);
        }

        log.info("Certificate revocation request validation completed successfully");
    }

    private void validateCertificateForCreate(Certificate certificate) {
        Map<String, String> errorMap = new HashMap<>();

        // Validate mandatory fields
        if (!StringUtils.hasText(certificate.getTenantId())) {
            errorMap.put("TENANT_ID_REQUIRED", "TenantId is mandatory");
        }

        if (!StringUtils.hasText(certificate.getContext())) {
            errorMap.put("CONTEXT_REQUIRED", "Context is mandatory");
        }

        if (!StringUtils.hasText(certificate.getType())) {
            errorMap.put("TYPE_REQUIRED", "Certificate type is mandatory");
        }

        if (certificate.getIssuer() == null) {
            errorMap.put("ISSUER_REQUIRED", "Issuer is mandatory");
        } else {
            validateIssuer(certificate.getIssuer(), errorMap);
        }

        if (certificate.getCredentialSubject() == null || certificate.getCredentialSubject().isEmpty()) {
            errorMap.put("CREDENTIAL_SUBJECT_REQUIRED", "Credential subject is mandatory");
        }

        // Validate business rules
        validateBusinessRules(certificate, errorMap);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateCertificateForUpdate(Certificate certificate) {
        Map<String, String> errorMap = new HashMap<>();

        // Certificate ID must be provided for updates
        if (!StringUtils.hasText(certificate.getId())) {
            errorMap.put("CERTIFICATE_ID_REQUIRED", "Certificate ID is mandatory for updates");
        }

        if (!StringUtils.hasText(certificate.getTenantId())) {
            errorMap.put("TENANT_ID_REQUIRED", "TenantId is mandatory");
        }

        // Check if certificate exists
        if (StringUtils.hasText(certificate.getId()) && StringUtils.hasText(certificate.getTenantId())) {
            if (!certificateRepository.existsCertificate(certificate.getTenantId(), certificate.getId())) {
                errorMap.put("CERTIFICATE_NOT_FOUND", "Certificate not found");
            }
        }

        // Validate other fields similar to create
        validateCertificateForCreate(certificate);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateRevocationDetail(RevocationDetail detail) {
        Map<String, String> errorMap = new HashMap<>();

        if (!StringUtils.hasText(detail.getCertificateId())) {
            errorMap.put("CERTIFICATE_ID_REQUIRED", "Certificate ID is mandatory");
        }

        if (!StringUtils.hasText(detail.getTenantId())) {
            errorMap.put("TENANT_ID_REQUIRED", "TenantId is mandatory");
        }

        if (!StringUtils.hasText(detail.getReason())) {
            errorMap.put("REASON_REQUIRED", "Revocation reason is mandatory");
        }

        // Check if certificate exists and is not already revoked
        if (StringUtils.hasText(detail.getCertificateId()) && StringUtils.hasText(detail.getTenantId())) {
            Certificate certificate = certificateRepository.getCertificateById(detail.getTenantId(), detail.getCertificateId());
            if (certificate == null) {
                errorMap.put("CERTIFICATE_NOT_FOUND", "Certificate not found: " + detail.getCertificateId());
            } else if (CertificateConfiguration.CERTIFICATE_STATUS_REVOKED.equals(certificate.getStatus())) {
                errorMap.put("CERTIFICATE_ALREADY_REVOKED", "Certificate is already revoked: " + detail.getCertificateId());
            }
        }

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }
    }

    private void validateIssuer(Issuer issuer, Map<String, String> errorMap) {
        if (!StringUtils.hasText(issuer.getId())) {
            errorMap.put("ISSUER_ID_REQUIRED", "Issuer ID is mandatory");
        }

        if (!StringUtils.hasText(issuer.getName())) {
            errorMap.put("ISSUER_NAME_REQUIRED", "Issuer name is mandatory");
        }

        if (!StringUtils.hasText(issuer.getType())) {
            errorMap.put("ISSUER_TYPE_REQUIRED", "Issuer type is mandatory");
        } else if (!VALID_ISSUER_TYPES.contains(issuer.getType())) {
            errorMap.put("INVALID_ISSUER_TYPE", 
                    "Invalid issuer type. Valid values are: " + String.join(", ", VALID_ISSUER_TYPES));
        }
    }

    private void validateBusinessRules(Certificate certificate, Map<String, String> errorMap) {
        // Check for duplicate active certificates for the same subject and type
        if (certificate.getCredentialSubject() != null && 
            certificate.getCredentialSubject().containsKey("id") &&
            StringUtils.hasText(certificate.getType())) {
            String subjectId = (String) certificate.getCredentialSubject().get("id");
            String certificateType = certificate.getType();
            if (certificateRepository.existsCertificateBySubjectId(
                    certificate.getTenantId(), subjectId, certificateType)) {
                errorMap.put("DUPLICATE_CERTIFICATE", 
                        "Active certificate already exists for subject " + subjectId + " and type " + certificateType);
            }
        }

        // Validate expiration date if provided
        if (certificate.getExpirationDate() != null && certificate.getExpirationDate() <= System.currentTimeMillis()) {
            errorMap.put("INVALID_EXPIRATION_DATE", "Expiration date cannot be in the past");
        }
    }
}