package com.bigid.azurekeyvaultapp.util;

import com.bigid.appinfrastructure.dto.ActionParamDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.ParamDetails;

import java.util.Map;
import java.util.stream.Collectors;

public class ParamsMapUtils {

    public static Map<String, String> getGlobalParamsMap(ExecutionContext executionContext) {
        return executionContext.getGlobalParams()
                .stream()
                .collect(Collectors.toMap(ParamDetails::getParamName, ParamDetails::getParamValue));
    }

    public static Map<String, Object> getActionParamsMap(ExecutionContext executionContext) {
        return executionContext.getActionParams()
                .stream()
                .collect(Collectors.toMap(ActionParamDetails::getParamName, ActionParamDetails::getParamValue));
    }
}
