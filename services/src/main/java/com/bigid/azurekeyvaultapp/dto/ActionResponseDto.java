package com.bigid.azurekeyvaultapp.dto;

import java.util.Map;

public record ActionResponseDto(String message, Map<String, Map<String, String>> credentialFields) {
}

