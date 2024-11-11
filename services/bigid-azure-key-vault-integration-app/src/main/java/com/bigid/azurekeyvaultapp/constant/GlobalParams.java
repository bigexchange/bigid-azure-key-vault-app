package com.bigid.azurekeyvaultapp.constant;

import lombok.Getter;

@Getter
public enum GlobalParams {
    CLIENT_ID("clientId"),
    CLIENT_SECRET("clientSecret"),
    TENANT_ID("tenantId");

    private final String value;

    GlobalParams(String value) {
        this.value = value;
    }

}
