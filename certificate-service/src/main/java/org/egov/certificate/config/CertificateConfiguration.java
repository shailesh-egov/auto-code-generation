package org.egov.certificate.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateConfiguration {

    // Database Configuration
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // Kafka Configuration
    @Value("${kafka.config.bootstrap_server_config}")
    private String kafkaBootstrapServers;

    @Value("${certificate.kafka.create.topic}")
    private String createTopic;

    @Value("${certificate.kafka.update.topic}")
    private String updateTopic;

    @Value("${certificate.kafka.revoke.topic}")
    private String revokeTopic;

    // External Service URLs
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    // Search Configuration
    @Value("${certificate.default.offset}")
    private Integer defaultOffset;

    @Value("${certificate.default.limit}")
    private Integer defaultLimit;

    @Value("${certificate.search.max.limit}")
    private Integer maxSearchLimit;

    // Certificate Service Specific Configuration
    @Value("${certificate.signature.algorithm}")
    private String defaultSignatureAlgorithm;

    @Value("${certificate.default.proof.purpose}")
    private String defaultProofPurpose;

    @Value("${certificate.default.context}")
    private String defaultContext;

    // ID Generation Configuration
    @Value("${certificate.idgen.name}")
    private String idGenName;

    @Value("${certificate.idgen.format}")
    private String idGenFormat;

    // Timeout configurations
    @Value("${certificate.http.timeout.connect:30000}")
    private Integer httpConnectTimeout;

    @Value("${certificate.http.timeout.read:30000}")
    private Integer httpReadTimeout;

    // Service constants
    public static final String CERTIFICATE_STATUS_ACTIVE = "ACTIVE";
    public static final String CERTIFICATE_STATUS_REVOKED = "REVOKED";
    public static final String CERTIFICATE_STATUS_EXPIRED = "EXPIRED";

    public static final String ISSUER_TYPE_DEPARTMENT = "DEPARTMENT";
    public static final String ISSUER_TYPE_AGENCY = "AGENCY";
    public static final String ISSUER_TYPE_RELIGIOUS_INSTITUTION = "RELIGIOUS_INSTITUTION";

    public static final String SORT_ORDER_ASC = "ASC";
    public static final String SORT_ORDER_DESC = "DESC";

    public static final String SORT_BY_ISSUED = "issued";
    public static final String SORT_BY_EXPIRATION_DATE = "expirationDate";
    public static final String SORT_BY_STATUS = "status";
    public static final String SORT_BY_ISSUER = "issuer";
}