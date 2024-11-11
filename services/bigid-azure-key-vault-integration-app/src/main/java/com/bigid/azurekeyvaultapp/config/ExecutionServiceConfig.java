package com.bigid.azurekeyvaultapp.config;

import com.bigid.azurekeyvaultapp.service.ExecutionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ExecutionServiceConfig {
    @Bean
    public Map<String, ExecutionService> executionServices(List<ExecutionService> executionServices) {
        return executionServices.stream()
                .collect(Collectors.toMap(ExecutionService::getActionName, executionService -> executionService));
    }
}
