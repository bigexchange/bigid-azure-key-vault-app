package com.bigid.azurekeyvaultapp.config;

import com.bigid.azurekeyvaultapp.service.ExecutionService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Bean
    @Qualifier("executionServices")
    public Map<String, ExecutionService> executionServices(List<ExecutionService> executionServices) {
        return executionServices.stream()
                .collect(Collectors.toMap(ExecutionService::getActionName, executionService -> executionService));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
