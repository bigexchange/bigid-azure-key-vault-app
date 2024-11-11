package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
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

        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
                .clientSecret(GlobalParams.CLIENT_SECRET.getValue())
                .tenantId(GlobalParams.TENANT_ID.getValue())
                .build();

        // Define the scope for Azure Key Vault
        String scope = "https://vault.azure.net/.default";

        return Objects.requireNonNull(credential
                .getToken(new TokenRequestContext()
                        .addScopes(scope))
                .block());
    }
}
