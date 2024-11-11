package com.bigid.azurekeyvaultapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bigid.appinfrastructure", "com.bigid.azurekeyvaultapp"})
public class BasicDemoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicDemoAppApplication.class, args);
    }

}