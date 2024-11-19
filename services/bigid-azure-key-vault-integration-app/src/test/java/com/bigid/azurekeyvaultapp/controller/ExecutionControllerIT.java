package com.bigid.azurekeyvaultapp.controller;

import com.bigid.appinfrastructure.dto.ActionParamDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;
import com.bigid.azurekeyvaultapp.constant.ActionParams;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@Disabled
class ExecutionControllerIT {

    @Value("${action.param.custom_query}")
    private String customQuery;

    @Value("${global.param.client_id}")
    private String clientId;

    @Value("${global.param.client_secret}")
    private String clientSecret;

    @Value("${global.param.tenant_id}")
    private String tenantId;

    @Value("${global.param.scope}")
    private String scope;

    @Value("${global.param.azure_key_vault_url}")
    private String azureKeyVaultUrl;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fetchWithClientSecret() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("fetch_credentials");
        executionContext.setExecutionId("exec-123");
        executionContext.setActionParams(List.of(
                createActionParam(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue(), customQuery)
        ));
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.CLIENT_ID.getValue(), clientId),
                createGlobalParam(GlobalParams.CLIENT_SECRET.getValue(), clientSecret),
                createGlobalParam(GlobalParams.TENANT_ID.getValue(), tenantId),
                createGlobalParam(GlobalParams.SCOPE.getValue(), scope),
                createGlobalParam(GlobalParams.AUTHENTICATION_METHOD.getValue(), GlobalParams.CLIENT_CREDENTIALS.getValue()),
                createGlobalParam(GlobalParams.AZURE_KEY_VAULT_URL.getValue(), azureKeyVaultUrl)
        ));

        // Act & Assert
        mockMvc.perform(post("/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(executionContext)))
                .andExpect(status().isOk())//.andDo(print());
                .andExpect(jsonPath("$.executionId").value("exec-123"))
                .andExpect(jsonPath("$.statusEnum").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Successfully fetched secret"))
                .andExpect(jsonPath("$.additionalData.credentialFields.principal_secret_enc").value("secretValue"));
    }

    @Test
    void testConnectionWithClientSecret() throws Exception {
        // Arrange
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setActionName("test_connection");
        executionContext.setExecutionId("exec-123");
        executionContext.setGlobalParams(List.of(
                createGlobalParam(GlobalParams.CLIENT_ID.getValue(), clientId),
                createGlobalParam(GlobalParams.CLIENT_SECRET.getValue(), clientSecret),
                createGlobalParam(GlobalParams.TENANT_ID.getValue(), tenantId),
                createGlobalParam(GlobalParams.SCOPE.getValue(), scope),
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