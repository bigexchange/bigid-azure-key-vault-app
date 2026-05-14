package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import com.bigid.azurekeyvaultapp.service.provider.CredentialProvider;
import com.bigid.azurekeyvaultapp.util.ParamsMapUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AzureKeyVaultTokenService implements KeyVaultTokenService {

    private final List<CredentialProvider> credentialProviders;

    public AzureKeyVaultTokenService(List<CredentialProvider> credentialProviders) {
        this.credentialProviders = credentialProviders;
    }

    @Override
    public AccessToken fetchAccessToken(ExecutionContext executionContext) {
        Map<String, String> globalParamsMap = ParamsMapUtils.getGlobalParamsMap(executionContext);
        String authMethod = globalParamsMap.get(GlobalParams.AUTHENTICATION_METHOD.getValue());

        TokenCredential credential = credentialProviders.stream()
                .filter(provider -> provider.supports(authMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Authentication method not supported"))
                .create(globalParamsMap);

        return Objects.requireNonNull(credential
                .getToken(new TokenRequestContext()
                        .addScopes(globalParamsMap.get(GlobalParams.SCOPE.getValue())))
                .block());
    }

}
