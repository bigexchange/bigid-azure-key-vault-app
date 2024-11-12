package com.bigid.azurekeyvaultapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bigid.appinfrastructure", "com.bigid.azurekeyvaultapp"})
public class AzureKeyVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureKeyVaultApplication.class, args);
    }

}