package com.bigid.azurekeyvaultapp.service;

import com.azure.core.credential.AccessToken;
import com.bigid.appinfrastructure.dto.ExecutionContext;

public interface KeyVaultTokenService {
    AccessToken fetchAccessToken(ExecutionContext executionContext);
}
