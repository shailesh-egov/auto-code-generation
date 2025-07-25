package org.egov.certificate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.config.CertificateConfiguration;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IdgenUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CertificateConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, 
                                  String idFormat, Integer count) {
        log.info("Generating {} IDs for tenant: {}, idName: {}", count, tenantId, idName);

        try {
            String url = configuration.getIdGenHost() + configuration.getIdGenPath();
            
            Map<String, Object> idGenerationRequest = createIdGenerationRequest(
                    requestInfo, tenantId, idName, idFormat, count);

            log.debug("Calling ID generation service at: {}", url);
            log.debug("Request payload: {}", objectMapper.writeValueAsString(idGenerationRequest));

            Map<String, Object> response = restTemplate.postForObject(
                    url, idGenerationRequest, Map.class);

            if (response == null) {
                throw new CustomException("IDGEN_ERROR", "No response received from ID generation service");
            }

            List<String> idList = extractIdListFromResponse(response);
            
            if (idList.size() != count) {
                throw new CustomException("IDGEN_ERROR", 
                        "Expected " + count + " IDs but received " + idList.size());
            }

            log.info("Successfully generated {} IDs", idList.size());
            return idList;

        } catch (Exception e) {
            log.error("Error occurred while generating IDs: {}", e.getMessage(), e);
            throw new CustomException("IDGEN_ERROR", 
                    "Failed to generate IDs: " + e.getMessage());
        }
    }

    private Map<String, Object> createIdGenerationRequest(RequestInfo requestInfo, String tenantId, 
                                                         String idName, String idFormat, Integer count) {
        Map<String, Object> idRequest = new HashMap<>();
        idRequest.put("idName", idName);
        idRequest.put("tenantId", tenantId);
        idRequest.put("format", idFormat);
        idRequest.put("count", count);

        List<Map<String, Object>> idRequests = new ArrayList<>();
        idRequests.add(idRequest);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);
        request.put("idRequests", idRequests);

        return request;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractIdListFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> idResponses = (List<Map<String, Object>>) response.get("idResponses");

            if (idResponses == null || idResponses.isEmpty()) {
                throw new CustomException("IDGEN_ERROR", "No ID responses found in service response");
            }

            Map<String, Object> idResponse = idResponses.get(0);
            Object idObj = idResponse.get("id");
            List<String> idList;

            if (idObj instanceof List) {
                idList = (List<String>) idObj;
            } else if (idObj instanceof String) {
                idList = new ArrayList<>();
                idList.add((String) idObj);
            } else {
                throw new CustomException("IDGEN_ERROR", "No IDs found in service response");
            }

            if (idList == null || idList.isEmpty()) {
                throw new CustomException("IDGEN_ERROR", "No IDs found in service response");
            }

            return idList;

        } catch (ClassCastException e) {
            log.error("Error parsing ID generation response: {}", e.getMessage(), e);
            throw new CustomException("IDGEN_ERROR", "Invalid response format from ID generation service");
        }
    }

    public String generateSingleId(RequestInfo requestInfo, String tenantId, String idName, String idFormat) {
        List<String> idList = getIdList(requestInfo, tenantId, idName, idFormat, 1);
        return idList.get(0);
    }

    public String generateCertificateId(RequestInfo requestInfo, String tenantId) {
        return generateSingleId(requestInfo, tenantId, 
                configuration.getIdGenName(), configuration.getIdGenFormat());
    }
}