package com.bigid.azurekeyvaultapp.service;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public interface ExecutionService {
    Triple<Boolean, String, Map<String, String>> performAction(ExecutionContext executionContext);

    String getActionName();
}
