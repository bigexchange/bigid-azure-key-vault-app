package com.bigid.azurekeyvaultapp.dto;

import jakarta.validation.constraints.NotBlank;

public record ManagedIdentityParams(
        @NotBlank(message = "azure_key_vault_url is required")
        String azureKeyVaultUrl,

        @NotBlank(message = "scope is required")
        String scope,

        String clientId
) {}
