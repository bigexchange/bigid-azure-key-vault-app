package com.bigid.azurekeyvaultapp.service.impl;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
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

        if (!globalParamsMap.get(GlobalParams.AUTHENTICATION_METHOD.getValue()).equals(GlobalParams.CLIENT_CREDENTIALS.getValue())) {
            throw new IllegalArgumentException("Authentication method not supported");
        }

        TokenCredential credential = getTokenCredential(globalParamsMap);
//                : new ClientCertificateCredentialBuilder() // TODO: implement if needed
//                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
//                .tenantId(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))
//                .pfxCertificate("certPath")
//                .clientCertificatePassword("certPassword")
//                .build();

        return Objects.requireNonNull(credential
                .getToken(new TokenRequestContext()
                        .addScopes(globalParamsMap.get(GlobalParams.SCOPE.getValue())))
                .block());
    }

    public TokenCredential getTokenCredential(Map<String, String> globalParamsMap) {
        return new ClientSecretCredentialBuilder()
                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
                .clientSecret(globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue()))
                .tenantId(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))
                .build();
    }

}
