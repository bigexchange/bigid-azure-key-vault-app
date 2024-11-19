package com.bigid.azurekeyvaultapp.service.impl;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class TestConnectionExecutionService implements ExecutionService {

    private static final String CONNECTION_SUCCESSFUL = "Connection to Azure Key Vault successful!";
    private static final String CONNECTION_FAILED = "Connection to Azure Key Vault Failed: %s";

    @Autowired
    private KeyVaultTokenService keyVaultTokenService;

    @Override
    public ActionResponseDto performAction(ExecutionContext executionContext) {
        log.info("Attempting to fetch an access token...");
        keyVaultTokenService.fetchAccessToken(executionContext);
        log.info(CONNECTION_SUCCESSFUL);

        return new ActionResponseDto(CONNECTION_SUCCESSFUL, new HashMap<>());
    }

    @Override
    public String getActionName() {
        return "test_connection";
    }
}