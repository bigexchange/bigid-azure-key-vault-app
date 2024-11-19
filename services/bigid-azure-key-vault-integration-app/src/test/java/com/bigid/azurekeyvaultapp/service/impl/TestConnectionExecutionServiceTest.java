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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
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
        assertTrue(response.success());
        assertEquals(CONNECTION_SUCCESSFUL, response.message());
        assertEquals(new HashMap<>(), response.credentialFields());

        // Verify interactions
        verify(keyVaultTokenService, times(1)).fetchAccessToken(executionContext);
    }

    @Test
    void performAction_WhenConnectionFails_ShouldReturnFailureResponse() {
        // Arrange
        doThrow(new RuntimeException("Simulated failure")).when(keyVaultTokenService).fetchAccessToken(executionContext);

        // Act
        ActionResponseDto response = testConnectionExecutionService.performAction(executionContext);

        // Assert
        assertNotNull(response);
        assertFalse(response.success());
        assertEquals("Connection to Azure Key Vault Failed: Simulated failure", response.message());
        assertEquals(new HashMap<>(), response.credentialFields());

        // Verify interactions
        verify(keyVaultTokenService, times(1)).fetchAccessToken(executionContext);
    }

    @Test
    void getActionName_ShouldReturnTestConnection() {
        // Act
        String actionName = testConnectionExecutionService.getActionName();

        // Assert
        assertEquals("test_connection", actionName);
    }
}
