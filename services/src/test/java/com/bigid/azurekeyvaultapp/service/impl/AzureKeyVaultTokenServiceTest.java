package com.bigid.azurekeyvaultapp.service.impl;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
import com.bigid.azurekeyvaultapp.service.provider.ClientCredentialsProvider;
import com.bigid.azurekeyvaultapp.service.provider.ManagedIdentityProvider;
import com.bigid.azurekeyvaultapp.validator.ClientCredentialsParamsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AzureKeyVaultTokenServiceTest {

    private AzureKeyVaultTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new AzureKeyVaultTokenService(List.of(
                new ClientCredentialsProvider(new ClientCredentialsParamsValidator()),
                new ManagedIdentityProvider()
        ));
    }

    @Test
    void fetchAccessToken_WithMissingParams_ShouldThrowIllegalArgumentException() {
        // Create an ExecutionContext with incomplete globalParams
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setGlobalParams(List.of(
                createParamDetails("authentication_method", "client_secret")
                // Missing other required params
        ));

        assertThrows(IllegalArgumentException.class, () -> tokenService.fetchAccessToken(executionContext));
    }

    @Test
    void fetchAccessToken_WithUnsupportedAuthMethod_ShouldThrowIllegalArgumentException() {
        // Create an ExecutionContext with incomplete globalParams
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setGlobalParams(List.of(
                createParamDetails("authentication_method", "unsupported_method")
                // Missing other required params
        ));

        assertThrows(IllegalArgumentException.class, () -> tokenService.fetchAccessToken(executionContext));
    }

    @Test
    void fetchAccessToken_WithClientCredentials_MissingClientId_ShouldThrowIllegalArgumentException() {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setGlobalParams(List.of(
                createParamDetails("authentication_method", "Client Credentials"),
                createParamDetails("tenant_id", "some-tenant-id"),
                createParamDetails("client_secret", "some-secret")
        ));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> tokenService.fetchAccessToken(executionContext));
        assert ex.getMessage().contains("client_id");
    }

    @Test
    void fetchAccessToken_WithClientCredentials_MissingTenantId_ShouldThrowIllegalArgumentException() {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setGlobalParams(List.of(
                createParamDetails("authentication_method", "Client Credentials"),
                createParamDetails("client_id", "some-client-id"),
                createParamDetails("client_secret", "some-secret")
        ));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> tokenService.fetchAccessToken(executionContext));
        assert ex.getMessage().contains("tenant_id");
    }

    @Test
    void fetchAccessToken_WithClientCredentials_MissingClientSecret_ShouldThrowIllegalArgumentException() {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setGlobalParams(List.of(
                createParamDetails("authentication_method", "Client Credentials"),
                createParamDetails("client_id", "some-client-id"),
                createParamDetails("tenant_id", "some-tenant-id")
        ));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> tokenService.fetchAccessToken(executionContext));
        assert ex.getMessage().contains("client_secret");
    }

    // Helper method to create ParamDetails
    private ParamDetails createParamDetails(String paramName, String paramValue) {
        ParamDetails paramDetails = new ParamDetails();
        paramDetails.setParamName(paramName);
        paramDetails.setParamValue(paramValue);
        return paramDetails;
    }
}
