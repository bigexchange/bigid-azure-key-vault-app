package com.bigid.azurekeyvaultapp.config;

import com.bigid.appinfrastructure.dto.ActionResponseDetails;
import com.bigid.appinfrastructure.dto.StatusEnum;
import com.bigid.azurekeyvaultapp.service.impl.AzureKeyVaultTokenService;
import com.bigid.azurekeyvaultapp.service.impl.FetchCredentialsExecutionService;
import com.bigid.azurekeyvaultapp.service.impl.TestConnectionExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Map<Class<?>, String> exceptionMessagePrefixes = Map.of(
            FetchCredentialsExecutionService.class, "Failed to fetch secret: %s",
            TestConnectionExecutionService.class, "Connection to Azure Key Vault Failed: %s",
            AzureKeyVaultTokenService.class, "Connection to Azure Key Vault Failed: %s"
    );

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ActionResponseDetails> handleAccessDeniedException(RuntimeException e, WebRequest request) {
        String dynamicPrefixedErrorMessage = getDynamicPrefixedErrorMessage(e);
        log.error(dynamicPrefixedErrorMessage, e);
        return generateFailedResponse((String) request.getAttribute("executionId", RequestAttributes.SCOPE_REQUEST), dynamicPrefixedErrorMessage);
    }

    private ResponseEntity<ActionResponseDetails> generateFailedResponse(String executionId, String errorMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ActionResponseDetails(executionId, StatusEnum.ERROR, 0.0, errorMessage));
    }

    private String getDynamicPrefixedErrorMessage(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::getClassName) // Extract class names from the stack trace
                .map(className -> exceptionMessagePrefixes.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().getName().equals(className)) // Match class name
                        .map(entry -> String.format(entry.getValue(), e.getMessage())) // Format message
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull) // Keep only non-null messages
                .findFirst() // Return the first match
                .orElse(e.getMessage()); // Default to exception message if no match
    }
}
