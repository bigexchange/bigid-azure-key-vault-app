package com.bigid.azurekeyvaultapp.service;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;

public interface ExecutionService {
    ActionResponseDto performAction(ExecutionContext executionContext);

    String getActionName();
}
