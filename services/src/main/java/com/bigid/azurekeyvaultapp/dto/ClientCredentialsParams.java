package com.bigid.azurekeyvaultapp.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientCredentialsParams(
        @NotBlank(message = "client_id is required for Client Credentials authentication")
        String clientId,

        @NotBlank(message = "tenant_id is required for Client Credentials authentication")
        String tenantId,

        @NotBlank(message = "client_secret is required for Client Credentials authentication")
        String clientSecret
) {}
