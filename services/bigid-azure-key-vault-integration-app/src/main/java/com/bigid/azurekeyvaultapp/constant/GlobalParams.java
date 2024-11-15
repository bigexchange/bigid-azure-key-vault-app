package com.bigid.azurekeyvaultapp.constant;

import lombok.Getter;

@Getter
public enum GlobalParams {
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    TENANT_ID("tenant_id"),
    AUTHENTICATION_METHOD("authentication_method"),
    CLIENT_CREDENTIALS("Client Credentials"),
    SCOPE("scope");

    private final String value;

    GlobalParams(String value) {
        this.value = value;
    }

}
