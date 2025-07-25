package org.egov.certificate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Autowired
    private CertificateConfiguration certificateConfiguration;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(certificateConfiguration.getHttpConnectTimeout());
        factory.setReadTimeout(certificateConfiguration.getHttpReadTimeout());
        return new RestTemplate(factory);
    }
}