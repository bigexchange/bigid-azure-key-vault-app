package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ManagedIdentityCredential;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AzureKeyVaultTokenServiceTest {

    private AzureKeyVaultTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new AzureKeyVaultTokenService();
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
    void getManagedIdentityCredential_WithoutClientId_ShouldReturnSystemAssignedCredential() {
        Map<String, String> params = new HashMap<>();
        params.put(GlobalParams.CLIENT_ID.getValue(), null);

        TokenCredential credential = tokenService.getManagedIdentityCredential(params);

        assertInstanceOf(ManagedIdentityCredential.class, credential);
    }

    @Test
    void getManagedIdentityCredential_WithClientId_ShouldReturnUserAssignedCredential() {
        Map<String, String> params = new HashMap<>();
        params.put(GlobalParams.CLIENT_ID.getValue(), "some-client-id");

        TokenCredential credential = tokenService.getManagedIdentityCredential(params);

        assertInstanceOf(ManagedIdentityCredential.class, credential);
    }

    @Test
    void getManagedIdentityCredential_WithBlankClientId_ShouldReturnSystemAssignedCredential() {
        Map<String, String> params = new HashMap<>();
        params.put(GlobalParams.CLIENT_ID.getValue(), "   ");

        TokenCredential credential = tokenService.getManagedIdentityCredential(params);

        assertInstanceOf(ManagedIdentityCredential.class, credential);
    }

    // Helper method to create ParamDetails
    private ParamDetails createParamDetails(String paramName, String paramValue) {
        ParamDetails paramDetails = new ParamDetails();
        paramDetails.setParamName(paramName);
        paramDetails.setParamValue(paramValue);
        return paramDetails;
    }
}
