package com.bigid.azurekeyvaultapp.service.impl;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.StatusEnum;
import com.bigid.appinfrastructure.externalconnections.BigIDProxy;
import com.bigid.appinfrastructure.services.AbstractExecutionService;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class TestConnectionExecutionService extends AbstractExecutionService implements ExecutionService {

    public static final String CONNECTION_SUCCESSFUL = "Connection to Azure Key Vault successful!";
    public static final String CONNECTION_FAILED = "Connection to Azure Key Vault Failed!";

    private final KeyVaultTokenService keyVaultTokenService;

    @Autowired
    public TestConnectionExecutionService(BigIDProxy bigIDProxy, KeyVaultTokenService keyVaultTokenService) {
        super(bigIDProxy);
        this.keyVaultTokenService = keyVaultTokenService;
    }

    @Override
    public Triple<Boolean, String, Map<String, String>> performAction(ExecutionContext executionContext) {
        try {
            log.info("Attempting to fetch an access token...");
            keyVaultTokenService.fetchAccessToken(executionContext);

            bigIDProxy.updateActionStatusToBigID(
                    executionContext,
                    initializeResponse(executionContext, StatusEnum.COMPLETED, 1d, CONNECTION_SUCCESSFUL)
            );
            log.info(CONNECTION_SUCCESSFUL);
            return Triple.of(true, "Connection to hashicorp vault successful!", null);

        } catch (RuntimeException e) {
            bigIDProxy.updateActionStatusToBigID(
                    executionContext,
                    initializeResponse(executionContext, StatusEnum.ERROR, 0d, CONNECTION_FAILED)
            );
            log.error(CONNECTION_FAILED + ": " + e.getMessage(), e);

            return Triple.of(false, CONNECTION_FAILED, null);
        }
    }

    @Override
    public String getActionName() {
        return "TestConnection";
    }
}