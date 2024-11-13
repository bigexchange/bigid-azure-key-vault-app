package com.bigid.azurekeyvaultapp.constant;

import lombok.Getter;

@Getter
public enum GlobalParams {
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    TENANT_ID("tenant_id"),
    AUTHENTICATION_METHOD("authentication_method"),
    BASIC_AUTHENTICATION("Basic Authentication"),
    CLIENT_CREDENTIALS("Client Credentials"),
    USERNAME("username"),
    PASSWORD("password"),
    SCOPE("scope");

    private final String value;

    GlobalParams(String value) {
        this.value = value;
    }

}
