package com.bigid.azurekeyvaultapp.service.impl;

import com.bigid.appinfrastructure.dto.ExecutionContext;
import com.bigid.azurekeyvaultapp.dto.ActionResponseDto;
import com.bigid.azurekeyvaultapp.service.KeyVaultTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestConnectionExecutionServiceTest {

    private static final String CONNECTION_SUCCESSFUL = "Connection to Azure Key Vault successful!";

    @Mock
    private KeyVaultTokenService keyVaultTokenService;

    @InjectMocks
    private TestConnectionExecutionService testConnectionExecutionService;

    private final ExecutionContext executionContext = new ExecutionContext();

    @Test
    void performAction_WhenConnectionSuccessful_ShouldReturnSuccessResponse() {

        // Act
        ActionResponseDto response = testConnectionExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertEquals(CONNECTION_SUCCESSFUL, response.message());
        assertEquals(new HashMap<>(), response.credentialFields());

        // Verify interactions
        verify(keyVaultTokenService).fetchAccessToken(executionContext);
    }

    @Test
    void performAction_WhenConnectionFails_ShouldReturnFailureResponse() {
        // Arrange
        doThrow(new RuntimeException("Simulated failure")).when(keyVaultTokenService).fetchAccessToken(executionContext);

        // Act
        assertThrows(RuntimeException.class, () -> testConnectionExecutionService.performAction(executionContext), "Simulated failure");

        // Verify interactions
        verify(keyVaultTokenService).fetchAccessToken(executionContext);
    }

    @Test
    void getActionName_ShouldReturnTestConnection() {
        // Act
        String actionName = testConnectionExecutionService.getActionName();

        // Assert
        assertEquals("test_connection", actionName);
    }
}
