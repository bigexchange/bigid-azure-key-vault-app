package com.bigid.azurekeyvaultapp.controllers;

import com.bigid.appinfrastructure.controllers.AbstractExecutionController;
import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.appinfrastructure.dto.StatusEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ExecutionController extends AbstractExecutionController{
    @Override
    public ResponseEntity<ActionResponseDetails> executeAction(@RequestBody ExecutionContext executionContext) {
        String action = executionContext.getActionName();
        String executionId = executionContext.getExecutionId();
        switch (action) {
            case ("action1"):
                //your code
                return generateSyncSuccessMessage(executionId, "informative massage about the action");
            case ("action2"):
                //your code
                return generateSyncSuccessMessage(executionId, "informative massage about the action");
            case("action3"):
                //your code
                return generateSyncSuccessMessage(executionId, "informative massage about the action");
            case("action4"):
                //your code;
                return generateFailedResponse(executionId, new Exception("failed to excecut action"));
            default:
                return ResponseEntity.badRequest().body(
                        new ActionResponseDetails(executionId,
                                StatusEnum.ERROR,
                                0d,
                                "Got unresolved action = " + action));
        }
    }

}