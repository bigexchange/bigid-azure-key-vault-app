package com.bigid.azurekeyvaultapp.service.impl;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
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

    // Helper method to create ParamDetails
    private ParamDetails createParamDetails(String paramName, String paramValue) {
        ParamDetails paramDetails = new ParamDetails();
        paramDetails.setParamName(paramName);
        paramDetails.setParamValue(paramValue);
        return paramDetails;
    }
}
