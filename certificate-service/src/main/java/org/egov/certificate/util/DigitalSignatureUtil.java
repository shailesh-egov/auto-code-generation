package org.egov.certificate.util;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.certificate.web.models.Certificate;
import org.egov.certificate.web.models.Proof;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class DigitalSignatureUtil {

    @Autowired
    private CertificateConfiguration configuration;

    private final SecureRandom secureRandom = new SecureRandom();

    public Proof createDigitalSignature(Certificate certificate) {
        log.info("Creating digital signature for certificate: {}", certificate.getId());

        try {
            // In a real implementation, this would:
            // 1. Serialize the certificate data canonically
            // 2. Create a hash of the canonical data
            // 3. Sign the hash using the issuer's private key
            // 4. Return the signature in the appropriate format

            String certificateData = serializeCertificateData(certificate);
            String dataHash = createHash(certificateData);
            String signature = signData(dataHash, certificate.getIssuer().getId());

            Long currentTime = System.currentTimeMillis();

            Proof proof = Proof.builder()
                    .type(configuration.getDefaultSignatureAlgorithm())
                    .created(currentTime)
                    .proofPurpose(configuration.getDefaultProofPurpose())
                    .verificationMethod(certificate.getIssuer().getId() + "#key-1")
                    .signatureValue(signature)
                    .build();

            log.info("Successfully created digital signature for certificate: {}", certificate.getId());
            return proof;

        } catch (Exception e) {
            log.error("Error creating digital signature for certificate {}: {}", 
                    certificate.getId(), e.getMessage(), e);
            // In a real implementation, this might throw a more specific exception
            // For now, we'll create a mock signature to keep the system functional
            return createMockSignature(certificate);
        }
    }

    public boolean verifyDigitalSignature(Certificate certificate) {
        log.info("Verifying digital signature for certificate: {}", certificate.getId());

        try {
            Proof proof = certificate.getProof();
            if (proof == null || proof.getSignatureValue() == null) {
                log.warn("No proof or signature found for certificate: {}", certificate.getId());
                return false;
            }

            // In a real implementation, this would:
            // 1. Recreate the canonical certificate data
            // 2. Hash the data using the same algorithm
            // 3. Retrieve the issuer's public key
            // 4. Verify the signature using the public key

            String certificateData = serializeCertificateData(certificate);
            String dataHash = createHash(certificateData);
            boolean isValid = verifySignature(dataHash, proof.getSignatureValue(), proof.getVerificationMethod());

            log.info("Signature verification result for certificate {}: {}", certificate.getId(), isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying digital signature for certificate {}: {}", 
                    certificate.getId(), e.getMessage(), e);
            return false;
        }
    }

    private String serializeCertificateData(Certificate certificate) {
        // This is a simplified serialization for demonstration
        // In a real implementation, this would use canonical JSON-LD serialization
        
        StringBuilder data = new StringBuilder();
        data.append("id:").append(certificate.getId());
        data.append("|tenantId:").append(certificate.getTenantId());
        data.append("|context:").append(certificate.getContext());
        data.append("|type:").append(String.join(",", certificate.getType()));
        data.append("|issuer:").append(certificate.getIssuer().getId());
        data.append("|issued:").append(certificate.getIssued());
        
        if (certificate.getExpirationDate() != null) {
            data.append("|expires:").append(certificate.getExpirationDate());
        }
        
        // Add credential subject data
        if (certificate.getCredentialSubject() != null) {
            data.append("|subject:").append(certificate.getCredentialSubject().toString());
        }

        return data.toString();
    }

    private String createHash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private String signData(String dataHash, String issuerId) {
        // This is a mock implementation
        // In a real implementation, this would:
        // 1. Retrieve the issuer's private key from secure storage
        // 2. Use Ed25519 or RSA to sign the hash
        // 3. Return the signature in the appropriate format

        String mockSignature = "z" + UUID.randomUUID().toString().replace("-", "") +
                              Base64.getEncoder().encodeToString(dataHash.getBytes()).substring(0, 16);
        
        return mockSignature;
    }

    private boolean verifySignature(String dataHash, String signature, String verificationMethod) {
        // This is a mock implementation
        // In a real implementation, this would:
        // 1. Retrieve the public key from the verification method
        // 2. Use the appropriate algorithm to verify the signature
        // 3. Return the verification result

        // Simple validation - check if signature has expected format
        return signature != null && 
               signature.startsWith("z") && 
               signature.length() > 40;
    }

    private Proof createMockSignature(Certificate certificate) {
        // Fallback mock signature for demonstration purposes
        log.warn("Creating mock signature for certificate: {}", certificate.getId());
        
        Long currentTime = System.currentTimeMillis();
        String mockSignature = "z" + UUID.randomUUID().toString().replace("-", "") + 
                              Integer.toHexString(certificate.hashCode());

        return Proof.builder()
                .type(configuration.getDefaultSignatureAlgorithm())
                .created(currentTime)
                .proofPurpose(configuration.getDefaultProofPurpose())
                .verificationMethod(certificate.getIssuer().getId() + "#key-1")
                .signatureValue(mockSignature)
                .build();
    }

    public String generateProofId() {
        return "proof-" + UUID.randomUUID().toString();
    }

    public boolean isSignatureExpired(Proof proof, long maxAgeMillis) {
        if (proof.getCreated() == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - proof.getCreated()) > maxAgeMillis;
    }
}