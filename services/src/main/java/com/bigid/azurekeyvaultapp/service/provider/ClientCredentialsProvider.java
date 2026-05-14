package com.bigid.azurekeyvaultapp.service.provider;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.validator.ClientCredentialsParamsValidator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClientCredentialsProvider implements CredentialProvider {

    private final ClientCredentialsParamsValidator clientCredentialsParamsValidator;

    public ClientCredentialsProvider(ClientCredentialsParamsValidator clientCredentialsParamsValidator) {
        this.clientCredentialsParamsValidator = clientCredentialsParamsValidator;
    }

    @Override
    public boolean supports(String authMethod) {
        return GlobalParams.CLIENT_CREDENTIALS.getValue().equals(authMethod);
    }

    @Override
    public TokenCredential create(Map<String, String> globalParamsMap) {
        clientCredentialsParamsValidator.validate(globalParamsMap);
        return new ClientSecretCredentialBuilder()
                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
                .clientSecret(globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue()))
                .tenantId(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))
                .build();
    }
}
