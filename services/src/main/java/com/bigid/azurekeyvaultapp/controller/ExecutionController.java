package com.bigid.azurekeyvaultapp.controller;

import com.bigid.appinfrastructure.controllers.AbstractExecutionController;
import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.StatusEnum;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Objects;


@Controller
public class ExecutionController extends AbstractExecutionController {

    @Autowired
    @Qualifier("executionServices")
    private Map<String, ExecutionService> executionServices;

    @Override
    public ResponseEntity<ActionResponseDetails> executeAction(@RequestBody ExecutionContext executionContext) {
        String action = executionContext.getActionName();
        String executionId = executionContext.getExecutionId();
        ExecutionService executionService = executionServices.get(action);

        if (Objects.isNull(executionService)) {
            return unresolvedActionResponse(action, executionId);
        }

        ActionResponseDto actionResponseDto = executionService.performAction(executionContext);

        return actionResponseDto.success()
                ? generateSuccessResponse(executionId, actionResponseDto.message(), actionResponseDto.credentialFields())
                : generateFailedResponse(executionId, new Exception(actionResponseDto.message()));
    }

    private ResponseEntity<ActionResponseDetails> generateSuccessResponse(String executionId, String message, Map<String, Map<String, String>> secretMap) {
        ResponseEntity<ActionResponseDetails> actionResponseDetailsResponseEntity = generateSyncSuccessMessage(executionId, message);
        Objects.requireNonNull(actionResponseDetailsResponseEntity.getBody()).setAdditionalData(secretMap);
        return actionResponseDetailsResponseEntity;
    }

    private ResponseEntity<ActionResponseDetails> unresolvedActionResponse(String action, String executionId) {
        return ResponseEntity.badRequest().body(
                new ActionResponseDetails(executionId,
                        StatusEnum.ERROR,
                        0d,
                        "Got unresolved action = " + action));
    }

}