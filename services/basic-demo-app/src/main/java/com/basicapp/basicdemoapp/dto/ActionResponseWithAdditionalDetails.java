package com.basicapp.basicdemoapp.dto;

import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.StatusEnum;
import lombok.Data;

import java.util.HashMap;

@Data
public class ActionResponseWithAdditionalDetails extends ActionResponseDetails {
    private HashMap<String, String> additionalData;

    public ActionResponseWithAdditionalDetails(String executionId, StatusEnum statusEnum, double progress, String message, HashMap<String, String> additionalData) {
        super(executionId, statusEnum, progress, message);
        this.additionalData = additionalData;
    }
}
