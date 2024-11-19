package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.constant.ActionParams;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import com.bigid.azurekeyvaultapp.util.ParamsMapUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    public static final String FAILED_TO_FETCH_SECRET = "Failed to fetch secret: %s";
    public static final String CREDENTIAL_FIELDS = "credentialFields";

    @Autowired
    private KeyVaultTokenService keyVaultTokenService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ActionResponseDto performAction(ExecutionContext executionContext) {
        try {
            AccessToken accessToken = keyVaultTokenService.fetchAccessToken(executionContext);
            TokenCredential tokenCredential = request -> Mono.just(accessToken);

            Map<String, String> globalParamsMap = ParamsMapUtils.getGlobalParamsMap(executionContext);
            Map<String, Object> actionParamsMap = ParamsMapUtils.getActionParamsMap(executionContext);

            SecretClient secretClient = getSecretClient(globalParamsMap, tokenCredential);

            // Fetch the secret from Azure Key Vault
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(actionParamsMap.get(ActionParams.CREDENTIAL_PROVIDER_CUSTOM_QUERY.getValue()).toString());
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("custom query contains invalid JSON");
            }
            if (!rootNode.has(SECRET_KEY)) {
                throw new IllegalArgumentException("secret_key field is missing in custom query");
            }
            String secretKey = rootNode.get(SECRET_KEY).asText();

            String secretValue = secretClient.getSecret(secretKey).getValue();
            log.info(SUCCESSFULLY_FETCHED_SECRET);
            Map<String, String> secretsMap;
            try {
                secretsMap = objectMapper.readValue(secretValue, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Secret contains invalid JSON");
            }
            Map<String, Map<String, String>> secrets = Map.of(CREDENTIAL_FIELDS, secretsMap);

            return new ActionResponseDto(true, SUCCESSFULLY_FETCHED_SECRET, secrets);

        } catch (RuntimeException e) {
            log.error("Failed to fetch secret: {}", e.getMessage(), e);
            return new ActionResponseDto(false, String.format(FAILED_TO_FETCH_SECRET, e.getMessage()), null);
        }
    }

    @Override
    public String getActionName() {
        return "fetch_credentials";
    }

    public SecretClient getSecretClient(Map<String, String> globalParamsMap, TokenCredential tokenCredential) {
        return new SecretClientBuilder()
                .vaultUrl(globalParamsMap.get(GlobalParams.AZURE_KEY_VAULT_URL.getValue()))
                .credential(tokenCredential)
                .buildClient();
    }

}