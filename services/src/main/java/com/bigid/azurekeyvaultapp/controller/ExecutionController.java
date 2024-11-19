package com.bigid.azurekeyvaultapp.controller;

import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.StatusEnum;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Objects;


@Controller
public class ExecutionController {

    @Autowired
    @Qualifier("executionServices")
    private Map<String, ExecutionService> executionServices;

    @PostMapping({"/execute"})
    @ResponseBody
    public ResponseEntity<ActionResponseDetails> executeAction(@RequestBody ExecutionContext executionContext, WebRequest webRequest) {
        String action = executionContext.getActionName();
        String executionId = executionContext.getExecutionId();
        webRequest.setAttribute("executionId", executionId, RequestAttributes.SCOPE_REQUEST);
        ExecutionService executionService = executionServices.get(action);

        if (Objects.isNull(executionService)) {
            throw new IllegalArgumentException("Got unresolved action = " + action);
        }

        ActionResponseDto actionResponseDto = executionService.performAction(executionContext);

        return generateSuccessResponse(executionId, actionResponseDto.message(), actionResponseDto.credentialFields());
    }

    private ResponseEntity<ActionResponseDetails> generateSuccessResponse(String executionId, String message, Map<String, Map<String, String>> secretMap) {
        return ResponseEntity.status(HttpStatus.OK).body(new ActionResponseDetails(executionId, StatusEnum.COMPLETED, 1.0, message, secretMap, null));
    }

}