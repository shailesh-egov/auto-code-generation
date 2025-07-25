package org.egov.certificate.validator;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.web.models.Certificate;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CertificateValidator {

    private static final Pattern DID_PATTERN = Pattern.compile("^did:[a-z0-9]+:[a-zA-Z0-9._-]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern TENANT_ID_PATTERN = Pattern.compile("^[a-z]{2}\\.[a-z]+$");

    public void validateCertificateStructure(Certificate certificate) {
        log.info("Validating certificate structure for certificate: {}", certificate.getId());
        
        Map<String, String> errorMap = new HashMap<>();

        // Validate tenant ID format
        if (StringUtils.hasText(certificate.getTenantId()) && 
            !TENANT_ID_PATTERN.matcher(certificate.getTenantId()).matches()) {
            errorMap.put("INVALID_TENANT_ID", "TenantId must be in format: state.city (e.g., pb.mohali)");
        }

        // Validate context URL
        if (StringUtils.hasText(certificate.getContext()) && 
            !URL_PATTERN.matcher(certificate.getContext()).matches()) {
            errorMap.put("INVALID_CONTEXT", "Context must be a valid HTTPS URL");
        }

        // Validate certificate type
        if (!StringUtils.hasText(certificate.getType())) {
            errorMap.put("INVALID_TYPE", "Certificate type cannot be empty");
        }

        // Validate issuer ID format (should be DID)
        if (certificate.getIssuer() != null && StringUtils.hasText(certificate.getIssuer().getId()) &&
            !DID_PATTERN.matcher(certificate.getIssuer().getId()).matches()) {
            errorMap.put("INVALID_ISSUER_ID", "Issuer ID must be a valid DID (e.g., did:gov:pb:civil-registration)");
        }

        // Validate credential subject
        validateCredentialSubject(certificate, errorMap);

        // Validate dates
        validateDates(certificate, errorMap);

        if (!errorMap.isEmpty()) {
            throw new CustomException(errorMap);
        }

        log.info("Certificate structure validation completed successfully");
    }

    private void validateCredentialSubject(Certificate certificate, Map<String, String> errorMap) {
        if (certificate.getCredentialSubject() == null) {
            return;
        }

        // Subject ID should be a DID if present
        Object subjectId = certificate.getCredentialSubject().get("id");
        if (subjectId != null && subjectId instanceof String) {
            String id = (String) subjectId;
            if (StringUtils.hasText(id) && !DID_PATTERN.matcher(id).matches()) {
                errorMap.put("INVALID_SUBJECT_ID", "Subject ID must be a valid DID");
            }
        }

        // Validate required fields based on certificate type
        if (StringUtils.hasText(certificate.getType())) {
            validateTypeSpecificFields(certificate.getType(), certificate.getCredentialSubject(), errorMap);
        }
    }

    private void validateTypeSpecificFields(String certificateType, Map<String, Object> credentialSubject, 
                                          Map<String, String> errorMap) {
        switch (certificateType.toLowerCase()) {
            case "birthcertificate":
                validateBirthCertificateFields(credentialSubject, errorMap);
                break;
            case "deathcertificate":
                validateDeathCertificateFields(credentialSubject, errorMap);
                break;
            case "marriagecertificate":
                validateMarriageCertificateFields(credentialSubject, errorMap);
                break;
            // Add more certificate types as needed
            default:
                // Generic validation for unknown types
                if (!credentialSubject.containsKey("name") || !StringUtils.hasText((String) credentialSubject.get("name"))) {
                    errorMap.put("MISSING_SUBJECT_NAME", "Subject name is required");
                }
                break;
        }
    }

    private void validateBirthCertificateFields(Map<String, Object> credentialSubject, Map<String, String> errorMap) {
        if (!credentialSubject.containsKey("name") || !StringUtils.hasText((String) credentialSubject.get("name"))) {
            errorMap.put("MISSING_BIRTH_NAME", "Child's name is required for birth certificate");
        }

        if (!credentialSubject.containsKey("dateOfBirth") || !StringUtils.hasText((String) credentialSubject.get("dateOfBirth"))) {
            errorMap.put("MISSING_DATE_OF_BIRTH", "Date of birth is required for birth certificate");
        }

        if (!credentialSubject.containsKey("placeOfBirth") || !StringUtils.hasText((String) credentialSubject.get("placeOfBirth"))) {
            errorMap.put("MISSING_PLACE_OF_BIRTH", "Place of birth is required for birth certificate");
        }
    }

    private void validateDeathCertificateFields(Map<String, Object> credentialSubject, Map<String, String> errorMap) {
        if (!credentialSubject.containsKey("name") || !StringUtils.hasText((String) credentialSubject.get("name"))) {
            errorMap.put("MISSING_DECEASED_NAME", "Deceased person's name is required for death certificate");
        }

        if (!credentialSubject.containsKey("dateOfDeath") || !StringUtils.hasText((String) credentialSubject.get("dateOfDeath"))) {
            errorMap.put("MISSING_DATE_OF_DEATH", "Date of death is required for death certificate");
        }
    }

    private void validateMarriageCertificateFields(Map<String, Object> credentialSubject, Map<String, String> errorMap) {
        if (!credentialSubject.containsKey("brideName") || !StringUtils.hasText((String) credentialSubject.get("brideName"))) {
            errorMap.put("MISSING_BRIDE_NAME", "Bride's name is required for marriage certificate");
        }

        if (!credentialSubject.containsKey("groomName") || !StringUtils.hasText((String) credentialSubject.get("groomName"))) {
            errorMap.put("MISSING_GROOM_NAME", "Groom's name is required for marriage certificate");
        }

        if (!credentialSubject.containsKey("marriageDate") || !StringUtils.hasText((String) credentialSubject.get("marriageDate"))) {
            errorMap.put("MISSING_MARRIAGE_DATE", "Marriage date is required for marriage certificate");
        }
    }

    private void validateDates(Certificate certificate, Map<String, String> errorMap) {
        Long currentTime = System.currentTimeMillis();

        // Validate issued date (should not be in the future)
        if (certificate.getIssued() != null && certificate.getIssued() > currentTime) {
            errorMap.put("INVALID_ISSUED_DATE", "Certificate issued date cannot be in the future");
        }

        // Validate expiration date (should be after issued date if both are present)
        if (certificate.getIssued() != null && certificate.getExpirationDate() != null &&
            certificate.getExpirationDate() <= certificate.getIssued()) {
            errorMap.put("INVALID_EXPIRATION_DATE", "Expiration date must be after issued date");
        }
    }

    public void validateBulkOperation(List<Certificate> certificates) {
        if (certificates.size() > 100) {
            throw new CustomException("BULK_LIMIT_EXCEEDED", "Cannot process more than 100 certificates in a single request");
        }

        // Check for duplicate certificate IDs in the same request
        Map<String, Integer> idCount = new HashMap<>();
        for (Certificate cert : certificates) {
            if (StringUtils.hasText(cert.getId())) {
                idCount.put(cert.getId(), idCount.getOrDefault(cert.getId(), 0) + 1);
                if (idCount.get(cert.getId()) > 1) {
                    throw new CustomException("DUPLICATE_CERTIFICATE_ID", 
                            "Duplicate certificate ID found in request: " + cert.getId());
                }
            }
        }
    }
}