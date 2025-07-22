package org.egov.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.config.BankAccountServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@Slf4j
public class IndividualUtil {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final BankAccountServiceConfiguration configuration;

    @Autowired
    public IndividualUtil(ObjectMapper mapper, RestTemplate restTemplate, BankAccountServiceConfiguration configuration) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    public boolean validateIndividualId(String individualId, String tenantId, RequestInfo requestInfo) {
        log.info("Validating individual ID: {} for tenant: {}", individualId, tenantId);
        
        try {
            Map<String, Object> searchRequest = new HashMap<>();
            searchRequest.put("requestInfo", requestInfo);
            
            Map<String, Object> searchCriteria = new HashMap<>();
            searchCriteria.put("tenantId", tenantId);
            searchCriteria.put("ids", Collections.singletonList(individualId));
            searchCriteria.put("limit", 1);
            searchCriteria.put("offset", 0);
            
            searchRequest.put("Individual", searchCriteria);
            
            String url = configuration.getIndividualHost() + configuration.getIndividualSearchEndpoint();
            String response = restTemplate.postForObject(url, searchRequest, String.class);
            
            if (response != null) {
                Map<String, Object> responseMap = mapper.readValue(response, Map.class);
                List<Object> individuals = (List<Object>) responseMap.get("Individual");
                
                boolean isValid = individuals != null && !individuals.isEmpty();
                log.info("Individual validation result for ID {}: {}", individualId, isValid);
                return isValid;
            }
        } catch (Exception e) {
            log.error("Error validating individual ID: {}", individualId, e);
        }
        
        return false;
    }
}