package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.bigid.appinfrastructure.dto.ActionParamDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
import com.bigid.azurekeyvaultapp.constant.ActionParams;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static com.bigid.azurekeyvaultapp.service.impl.FetchCredentialsExecutionService.CREDENTIAL_FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchCredentialsExecutionServiceTest {

    @Mock
    private KeyVaultTokenService keyVaultTokenService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    @Spy
    private FetchCredentialsExecutionService fetchCredentialsExecutionService;

    @Test
    void performAction_whenSuccess_ShouldReturnSuccessResponse() {
        // Arrange: Create real ExecutionContext
        ExecutionContext executionContext = createExecutionContext(
                List.of(
                        createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{\"secret_key\":\"my-secret\"}")
                ),
                List.of(
                        createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), "https://example-keyvault.vault.azure.net/")
                )
        );

        // Mock KeyVaultTokenService to return a valid token
        AccessToken mockAccessToken = new AccessToken("mock-token", null);
        when(keyVaultTokenService.fetchAccessToken(any(ExecutionContext.class))).thenReturn(mockAccessToken);

        // Mock SecretClient to return a KeyVaultSecret
        KeyVaultSecret mockSecret = mock(KeyVaultSecret.class);
        when(mockSecret.getValue()).thenReturn("{\"principalId\": \"secretKey\", \"tenantId\": \"tenantIdValue\", \"principal_secret_enc\": \"secretValue\"}");
        SecretClient mockSecretClient = mock(SecretClient.class);
        when(mockSecretClient.getSecret("my-secret")).thenReturn(mockSecret);

        // Partial mock of getSecretClient to return the mock SecretClient
        doReturn(mockSecretClient).when(fetchCredentialsExecutionService).getSecretClient(any(), any(TokenCredential.class));

        // Act
        ActionResponseDto response = fetchCredentialsExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Successfully fetched secret", response.message());
        assertNotNull(response.credentialFields());
        assertEquals("secretValue", response.credentialFields().get(CREDENTIAL_FIELDS).get("principal_secret_enc"));

        // Verify interactions
        verify(keyVaultTokenService, times(1)).fetchAccessToken(any(ExecutionContext.class));
        verify(mockSecretClient, times(1)).getSecret("my-secret");
    }

    @Test
    void performAction_WhenSecretJsonProcessingExceptionOccurs_ShouldReturnFailureResponse() {
        // Arrange: Create real ExecutionContext
        ExecutionContext executionContext = createExecutionContext(
                List.of(
                        createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{\"secret_key\":\"my-secret\"}")
                ),
                List.of(
                        createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), "https://example-keyvault.vault.azure.net/")
                )
        );

        // Mock KeyVaultTokenService to return a valid token
        AccessToken mockAccessToken = new AccessToken("mock-token", null);
        when(keyVaultTokenService.fetchAccessToken(any(ExecutionContext.class))).thenReturn(mockAccessToken);

        // Mock SecretClient to return a KeyVaultSecret
        KeyVaultSecret mockSecret = mock(KeyVaultSecret.class);
        when(mockSecret.getValue()).thenReturn("{invalid-json}");
        SecretClient mockSecretClient = mock(SecretClient.class);
        when(mockSecretClient.getSecret("my-secret")).thenReturn(mockSecret);

        // Partial mock of getSecretClient to return the mock SecretClient
        doReturn(mockSecretClient).when(fetchCredentialsExecutionService).getSecretClient(any(), any(TokenCredential.class));

        // Act
        ActionResponseDto response = fetchCredentialsExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertFalse(response.success());
        assertEquals("Failed to fetch secret: Secret contains invalid JSON", response.message());
        assertNull(response.credentialFields());

        // Verify interactions
        verify(keyVaultTokenService, times(1)).fetchAccessToken(any(ExecutionContext.class));
        verify(mockSecretClient, times(1)).getSecret("my-secret");
    }

    @Test
    void performAction_WhenJsonProcessingExceptionOccurs_ShouldReturnFailureResponse() {
        // Arrange
        ExecutionContext executionContext = createExecutionContext(
                List.of(
                        createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{invalid-json}") // Invalid JSON
                ),
                List.of(
                        createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), "https://example-keyvault.vault.azure.net/")
                )
        );

        AccessToken mockAccessToken = new AccessToken("mock-token", OffsetDateTime.now().plusHours(1));
        when(keyVaultTokenService.fetchAccessToken(executionContext)).thenReturn(mockAccessToken);

        // Act
        ActionResponseDto response = fetchCredentialsExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertFalse(response.success());
        assertEquals("Failed to fetch secret: custom query contains invalid JSON", response.message());
        assertNull(response.credentialFields());
    }

    @Test
    void performAction_WhenRuntimeExceptionOccurs_ShouldReturnFailureResponse() {
        // Arrange
        ExecutionContext executionContext = createExecutionContext(
                List.of(
                        createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{\"secret_key\":\"my-secret\"}")
                ),
                List.of(
                        createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), "https://example-keyvault.vault.azure.net/")
                )
        );

        when(keyVaultTokenService.fetchAccessToken(executionContext)).thenThrow(new RuntimeException("Connection error"));

        // Act
        ActionResponseDto response = fetchCredentialsExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertFalse(response.success());
        assertEquals("Failed to fetch secret: Connection error", response.message());
        assertNull(response.credentialFields());
    }

    @Test
    void getActionName_ShouldReturnCorrectName() {
        assertEquals("fetch_credentials", fetchCredentialsExecutionService.getActionName());
    }

    // Helper method to create ExecutionContext
    private ExecutionContext createExecutionContext(List<ActionParamDetails> actionParams, List<ParamDetails> globalParams) {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionParams(actionParams);
        executionContext.setGlobalParams(globalParams);
        return executionContext;
    }

    // Helper method to create ActionParamDetails
    private ActionParamDetails createActionParam(String paramName, String paramValue) {
        ActionParamDetails paramDetails = new ActionParamDetails();
        paramDetails.setParamName(paramName);
        paramDetails.setParamValue(paramValue);
        return paramDetails;
    }

    private ParamDetails createGlobalParam(String paramName, String paramValue) {
        ParamDetails paramDetails = new ParamDetails();
        paramDetails.setParamName(paramName);
        paramDetails.setParamValue(paramValue);
        return paramDetails;
    }
}


