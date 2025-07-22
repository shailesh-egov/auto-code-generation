package org.egov.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@Slf4j
public class EncryptionDecryptionUtil {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Autowired
    public EncryptionDecryptionUtil(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public String encryptValue(String plainText, String tenantId, String type, RequestInfo requestInfo) {
        // For now, return the plain text as encryption service integration would require
        // specific configuration and service endpoints
        log.debug("Encryption requested for type: {} in tenant: {}", type, tenantId);
        return plainText; // TODO: Implement actual encryption service call
    }

    public String decryptValue(String encryptedText, String tenantId, String type, RequestInfo requestInfo) {
        // For now, return the encrypted text as decryption service integration would require
        // specific configuration and service endpoints
        log.debug("Decryption requested for type: {} in tenant: {}", type, tenantId);
        return encryptedText; // TODO: Implement actual decryption service call
    }

    public List<String> encryptValues(List<String> plainTexts, String tenantId, String type, RequestInfo requestInfo) {
        List<String> encryptedValues = new ArrayList<>();
        for (String plainText : plainTexts) {
            encryptedValues.add(encryptValue(plainText, tenantId, type, requestInfo));
        }
        return encryptedValues;
    }

    public List<String> decryptValues(List<String> encryptedTexts, String tenantId, String type, RequestInfo requestInfo) {
        List<String> decryptedValues = new ArrayList<>();
        for (String encryptedText : encryptedTexts) {
            decryptedValues.add(decryptValue(encryptedText, tenantId, type, requestInfo));
        }
        return decryptedValues;
    }
}