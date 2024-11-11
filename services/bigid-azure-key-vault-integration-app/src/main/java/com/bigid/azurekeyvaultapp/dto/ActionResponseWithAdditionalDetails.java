package com.bigid.azurekeyvaultapp.dto;

import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionResponseWithAdditionalDetails extends ActionResponseDetails {
    private Map<String, String> additionalData;

    public ActionResponseWithAdditionalDetails(String executionId, StatusEnum statusEnum, double progress, String message, Map<String, String> additionalData) {
        super(executionId, statusEnum, progress, message);
        this.additionalData = additionalData;
    }
}