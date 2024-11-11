package com.bigid.azurekeyvaultapp.constant;

import lombok.Getter;

@Getter
public enum ActionParams {
    AZURE_KEY_VAULT_URL("azureKeyVaultURL"),
    CREDENTIAL_PROVIDER_CUSTOM_QUERY("credentialProviderCustomQuery");

    private final String value;

    ActionParams(String value) {
        this.value = value;
    }

}
