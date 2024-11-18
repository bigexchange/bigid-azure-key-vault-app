package com.bigid.azurekeyvaultapp.controller;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.bigid.appinfrastructure.dto.ActionParamDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
import com.bigid.azurekeyvaultapp.ConfigIT;
import com.bigid.azurekeyvaultapp.constant.ActionParams;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.service.impl.AzureKeyVaultTokenService;
import com.bigid.azurekeyvaultapp.service.impl.FetchCredentialsExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExecutionControllerTest extends ConfigIT {

    @SpyBean
    private FetchCredentialsExecutionService fetchCredentialsExecutionService;
    @SpyBean
    private AzureKeyVaultTokenService azureKeyVaultTokenService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        doReturn(new BasicAuthenticationCredential(vaultContainer.getUsername(),
                vaultContainer.getPassword()))
                .when(azureKeyVaultTokenService)
                .getTokenCredential(any());
    }

    @Test
    void executeAction_WhenConnectionTestedSuccessfully_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("test_connection");
        executionContext.setExecutionId("exec-123");
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue())
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isOk())//.andDo(print());
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Connection to Azure Key Vault successful!"));
    }

    @Test
    void executeAction_WhenUnsupportedAuthMethod_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("test_connection");
        executionContext.setExecutionId("exec-123");
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), "unsupported_auth_method")
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Connection to Azure Key Vault Failed: Authentication method not supported"));
    }

    @Test
    void executeAction_WhenSecretFetchedSuccessfully_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        doReturn(secretClient)
                .when(fetchCredentialsExecutionService)
                .getSecretClient(any(), any());
        //Map<String, String> secrets = Map.of("principalId", secretKey, "tenantId", "sds", "principal_secret_enc", secretValue);

        addSecret("my-secret", "{\"principalId\": \"secretKey\", \"tenantId\": \"tenantIdValue\", \"principal_secret_enc\": \"secretValue\"}");

        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("fetch_credentials");
        executionContext.setExecutionId("exec-123");
        executionContext.setActionParams(List.of(
                createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{\"secret_key\":\"my-secret\"}")
        ));
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue()),
                createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), getVaultBaseUrl())
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Successfully fetched secret"))
                .andExpect(jsonPath("$.additionalData.credentialFields.principal_secret_enc").value("secretValue"));
    }

    @Test
    void executeAction_WhenActionNameIsUnrecognized_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("unknown_action");
        executionContext.setExecutionId("exec-123");
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue())
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Got unresolved action = unknown_action"));
    }

    @Test
    void executeAction_WhenSecretKeyIsMissing_ShouldReturnError() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("fetch_credentials");
        executionContext.setExecutionId("exec-123");
        executionContext.setActionParams(List.of(
                createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "{}") // No secret_key field
        ));
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue()),
                createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), getVaultBaseUrl())
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to fetch secret: secret_key field is missing in custom query"));
    }

    @Test
    void executeAction_WhenCustomQueryIsInvalidJson_ShouldReturnError() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("fetch_credentials");
        executionContext.setExecutionId("exec-123");
        executionContext.setActionParams(List.of(
                createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), "invalid-json") // Invalid JSON
        ));
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue()),
                createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), getVaultBaseUrl())
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Failed to fetch secret: custom query contains invalid JSON"));
    }

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