package org.egov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.egov", "org.egov.web.controllers", "org.egov.config"})
public class BankAccountServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountServiceMain.class, args);
    }
}