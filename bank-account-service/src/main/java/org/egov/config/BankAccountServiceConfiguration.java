package org.egov.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Component
@Data
@Import({TracerConfiguration.class})
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountServiceConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    // Idgen Configuration
    @Value("${egov.idgen.host}")
    private String idGenHost;
    
    @Value("${egov.idgen.path}")
    private String idGenPath;

    // MDMS Configuration
    @Value("${egov.mdms.host}")
    private String mdmsHost;
    
    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    // User Service Configuration
    @Value("${egov.user.host}")
    private String userHost;
    
    @Value("${egov.user.context.path}")
    private String userContextPath;
    
    @Value("${egov.user.create.path}")
    private String userCreatePath;
    
    @Value("${egov.user.search.path}")
    private String userSearchPath;
    
    @Value("${egov.user.update.path}")
    private String userUpdatePath;

    // Individual Service Configuration
    @Value("${egov.individual.host}")
    private String individualHost;
    
    @Value("${egov.individual.search.endpoint}")
    private String individualSearchEndpoint;

    // Organisation Service Configuration
    @Value("${egov.organisation.host}")
    private String organisationHost;
    
    @Value("${egov.organisation.search.endpoint}")
    private String organisationSearchEndpoint;

    // Workflow Service Configuration
    @Value("${egov.workflow.host}")
    private String workflowHost;
    
    @Value("${egov.workflow.transition.path}")
    private String workflowTransitionPath;
    
    @Value("${egov.workflow.businessservice.search.path}")
    private String workflowBusinessServiceSearchPath;
    
    @Value("${egov.workflow.processinstance.search.path}")
    private String workflowProcessInstanceSearchPath;

    // URL Shortener Configuration
    @Value("${egov.url.shortner.host}")
    private String urlShortenerHost;
    
    @Value("${egov.url.shortner.endpoint}")
    private String urlShortenerEndpoint;

    // Kafka Configuration
    @Value("${bank.account.kafka.create.topic}")
    private String saveBankAccountTopic;
    
    @Value("${bank.account.kafka.update.topic}")
    private String updateBankAccountTopic;
    
    @Value("${bank.account.kafka.delete.topic}")
    private String deleteBankAccountTopic;

    // Search Configuration
    @Value("${bank.account.default.offset}")
    private Integer defaultOffset;
    
    @Value("${bank.account.default.limit}")
    private Integer defaultLimit;
    
    @Value("${bank.account.search.max.limit}")
    private Integer maxLimit;

    // Encryption Configuration
    @Value("${state.level.tenant.id}")
    private String stateLevelTenantId;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}