package com.bigid.azurekeyvaultapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ActionResponseDto {

    private boolean success;
    private String message;
    private Map<String, String> credentialFields;
}
