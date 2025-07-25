package org.egov.certificate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan(basePackages = {"org.egov.certificate", "org.egov.works.commons"})
@EnableAspectJAutoProxy
public class CertificateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertificateApplication.class, args);
    }
}