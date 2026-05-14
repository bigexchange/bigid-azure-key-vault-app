package com.bigid.azurekeyvaultapp.validator;

import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClientCredentialsParamsValidator {

    public void validate(Map<String, String> globalParamsMap) {
        if (StringUtils.isBlank(globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()))) {
            throw new IllegalArgumentException("client_id is required for Client Credentials authentication");
        }
        if (StringUtils.isBlank(globalParamsMap.get(GlobalParams.TENANT_ID.getValue()))) {
            throw new IllegalArgumentException("tenant_id is required for Client Credentials authentication");
        }
        if (StringUtils.isBlank(globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue()))) {
            throw new IllegalArgumentException("client_secret is required for Client Credentials authentication");
        }
    }
}
