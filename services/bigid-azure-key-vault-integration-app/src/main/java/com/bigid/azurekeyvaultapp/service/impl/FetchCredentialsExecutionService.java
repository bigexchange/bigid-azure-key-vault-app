package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.constant.ActionParams;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import com.bigid.azurekeyvaultapp.util.ParamsMapUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class FetchCredentialsExecutionService implements ExecutionService {

    private static final String SECRET_KEY = "secret_key";
    private static final String SUCCESSFULLY_FETCHED_SECRET = "Successfully fetched secret";
    public static final String FAILED_TO_FETCH_SECRET = "Failed to fetch secret";

    @Autowired
    private KeyVaultTokenService keyVaultTokenService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ActionResponseDto performAction(ExecutionContext executionContext) {
        try {
            AccessToken accessToken = keyVaultTokenService.fetchAccessToken(executionContext);
            TokenCredential tokenCredential = request -> Mono.just(accessToken);

            Map<String, Object> actionParamsMap = ParamsMapUtils.getActionParamsMap(executionContext);
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(actionParamsMap.get(ActionParams.AZURE_KEY_VAULT_URL.getValue()).toString())
                    .credential(tokenCredential)
                    .buildClient();

            // Fetch the secret from Azure Key Vault
            JsonNode rootNode = objectMapper.readTree(actionParamsMap.get(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue()).toString());
            String secretKey = rootNode.get(SECRET_KEY).asText();

            String secretValue = secretClient.getSecret(secretKey).getValue();
            log.info(SUCCESSFULLY_FETCHED_SECRET);

            return new ActionResponseDto(true, SUCCESSFULLY_FETCHED_SECRET, Map.of(secretKey, secretValue));

        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Failed to fetch secret: {}", e.getMessage(), e);
            return new ActionResponseDto(false, FAILED_TO_FETCH_SECRET, null);
        }
    }

    @Override
    public String getActionName() {
        return "fetch_credentials";
    }

}