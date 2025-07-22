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
public class OrganisationUtil {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final BankAccountServiceConfiguration configuration;

    @Autowired
    public OrganisationUtil(ObjectMapper mapper, RestTemplate restTemplate, BankAccountServiceConfiguration configuration) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    public boolean validateOrganisationId(String organisationId, String tenantId, RequestInfo requestInfo) {
        log.info("Validating organisation ID: {} for tenant: {}", organisationId, tenantId);
        
        try {
            Map<String, Object> searchRequest = new HashMap<>();
            searchRequest.put("requestInfo", requestInfo);
            
            Map<String, Object> searchCriteria = new HashMap<>();
            searchCriteria.put("tenantId", tenantId);
            searchCriteria.put("ids", Collections.singletonList(organisationId));
            searchCriteria.put("limit", 1);
            searchCriteria.put("offset", 0);
            
            searchRequest.put("SearchCriteria", searchCriteria);
            
            String url = configuration.getOrganisationHost() + configuration.getOrganisationSearchEndpoint();
            String response = restTemplate.postForObject(url, searchRequest, String.class);
            
            if (response != null) {
                Map<String, Object> responseMap = mapper.readValue(response, Map.class);
                List<Object> organisations = (List<Object>) responseMap.get("organisations");
                
                boolean isValid = organisations != null && !organisations.isEmpty();
                log.info("Organisation validation result for ID {}: {}", organisationId, isValid);
                return isValid;
            }
        } catch (Exception e) {
            log.error("Error validating organisation ID: {}", organisationId, e);
        }
        
        return false;
    }
}