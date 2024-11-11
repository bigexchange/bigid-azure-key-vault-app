package com.bigid.azurekeyvaultapp.controller;

import com.bigid.appinfrastructure.controllers.AbstractExecutionController;
import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.StatusEnum;
import com.bigid.azurekeyvaultapp.dto.ActionResponseWithAdditionalDetails;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Objects;


@Controller
public class ExecutionController extends AbstractExecutionController {

    @Autowired
    private Map<String, ExecutionService> executionServices;

    @Override
    public ResponseEntity<ActionResponseDetails> executeAction(@RequestBody ExecutionContext executionContext) {
        String action = executionContext.getActionName();
        String executionId = executionContext.getExecutionId();
        ExecutionService executionService = executionServices.get(action);

        if (Objects.isNull(executionService)) {
            return unresolvedActionResponse(action, executionId);
        }

        Triple<Boolean, String, Map<String, String>> statusMessageSecretTriple = executionService.performAction(executionContext);

        return statusMessageSecretTriple.getLeft()
                ? generateSuccessResponse(executionId, statusMessageSecretTriple.getMiddle(), statusMessageSecretTriple.getRight())
                : generateFailedResponse(executionId, new Exception(statusMessageSecretTriple.getMiddle()));
    }

    private ResponseEntity<ActionResponseDetails> generateSuccessResponse(String executionId, String message, Map<String, String> secretMap) {
        return secretMap.isEmpty()
                ? generateSyncSuccessMessage(executionId, message)
                : ResponseEntity.status(HttpStatus.OK).body(new ActionResponseWithAdditionalDetails(executionId, StatusEnum.COMPLETED, 1d, message, secretMap));
    }

    private ResponseEntity<ActionResponseDetails> unresolvedActionResponse(String action, String executionId) {
        return ResponseEntity.badRequest().body(
                new ActionResponseDetails(executionId,
                        StatusEnum.ERROR,
                        0d,
                        "Got unresolved action = " + action));
    }

}