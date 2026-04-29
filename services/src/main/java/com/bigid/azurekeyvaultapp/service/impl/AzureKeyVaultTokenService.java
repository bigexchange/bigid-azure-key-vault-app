package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import com.bigid.azurekeyvaultapp.util.ParamsMapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class AzureKeyVaultTokenService implements KeyVaultTokenService {
    @Override
    public AccessToken fetchAccessToken(ExecutionContext executionContext) {
        Map<String, String> globalParamsMap = ParamsMapUtils.getGlobalParamsMap(executionContext);

        String authMethod = globalParamsMap.get(GlobalParams.AUTHENTICATION_METHOD.getValue());
        TokenCredential credential;

        if (GlobalParams.CLIENT_CREDENTIALS.getValue().equals(authMethod)) {
            validateClientCredentialsParams(globalParamsMap);
            credential = getClientSecretCredential(globalParamsMap);
        } else if (GlobalParams.MANAGED_IDENTITY.getValue().equals(authMethod)) {
            credential = getManagedIdentityCredential(globalParamsMap);
        } else {
            throw new IllegalArgumentException("Authentication method not supported");
        }

        return Objects.requireNonNull(credential
                .getToken(new TokenRequestContext()
                        .addScopes(globalParamsMap.get(GlobalParams.SCOPE.getValue())))
                .block());
    }

    public TokenCredential getClientSecretCredential(Map<String, String> globalParamsMap) {
        return new ClientSecretCredentialBuilder()
                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
                .clientSecret(globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue()))
                .tenantId(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))
                .build();
    }

    public TokenCredential getManagedIdentityCredential(Map<String, String> globalParamsMap) {
        return new ManagedIdentityCredentialBuilder().clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue())).build();
    }

    private void validateClientCredentialsParams(Map<String, String> globalParamsMap) {
        if (isBlank(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))) {
            throw new IllegalArgumentException("client_id is required for Client Credentials authentication");
        }
        if (isBlank(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))) {
            throw new IllegalArgumentException("tenant_id is required for Client Credentials authentication");
        }
        if (isBlank(globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue()))) {
            throw new IllegalArgumentException("client_secret is required for Client Credentials authentication");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
