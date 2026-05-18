package com.bigid.azurekeyvaultapp.service.provider;

import com.azure.core.credential.TokenCredential;

import java.util.Map;

public interface CredentialProvider {

    boolean supports(String authMethod);

    TokenCredential create(Map<String, String> globalParamsMap);
}
