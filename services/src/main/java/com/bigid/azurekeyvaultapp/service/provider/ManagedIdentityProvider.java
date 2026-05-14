package com.bigid.azurekeyvaultapp.service.provider;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ManagedIdentityProvider implements CredentialProvider {

    @Override
    public boolean supports(String authMethod) {
        return GlobalParams.MANAGED_IDENTITY.getValue().equals(authMethod);
    }

    @Override
    public TokenCredential create(Map<String, String> globalParamsMap) {
        return new ManagedIdentityCredentialBuilder()
                .clientId(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))
                .build();
    }
}
