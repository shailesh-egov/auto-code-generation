package org.egov.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.config.BankAccountServiceConfiguration;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@Slf4j
public class IdgenUtil {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final BankAccountServiceConfiguration configs;

    @Autowired
    public IdgenUtil(ObjectMapper mapper, RestTemplate restTemplate, BankAccountServiceConfiguration configs) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.configs = configs;
    }

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
        List<String> list = new ArrayList<>();
        
        Map<String, Object> idGenRequest = new HashMap<>();
        idGenRequest.put("requestInfo", requestInfo);
        
        List<Map<String, Object>> idRequests = new ArrayList<>();
        Map<String, Object> idRequest = new HashMap<>();
        idRequest.put("tenantId", tenantId);
        idRequest.put("idName", idName);
        idRequest.put("format", idformat);
        idRequest.put("count", count);
        
        idRequests.add(idRequest);
        idGenRequest.put("idRequests", idRequests);
        
        String response = null;
        try {
            response = restTemplate.postForObject(configs.getIdGenHost() + configs.getIdGenPath(), idGenRequest, String.class);
            
            Map<String, Object> responseMap = mapper.readValue(response, Map.class);
            List<Map<String, Object>> idResponses = (List<Map<String, Object>>) responseMap.get("idResponses");
            
            if (idResponses != null && !idResponses.isEmpty()) {
                for (Map<String, Object> idResponse : idResponses) {
                    list.add((String) idResponse.get("id"));
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching from idgen: ", e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("IDGEN_ERROR", "Error in ID generation Service");
            throw new CustomException(errorMap);
        }
        
        return list;
    }

    public String generateBankAccountId(RequestInfo requestInfo, String tenantId) {
        List<String> ids = getIdList(requestInfo, tenantId, "bank.account.id", "BA-[city]-[SEQ_BANK_ACCOUNT_ID]", 1);
        return ids.isEmpty() ? UUID.randomUUID().toString() : ids.get(0);
    }
}