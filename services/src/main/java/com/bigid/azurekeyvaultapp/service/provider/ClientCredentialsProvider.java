package com.bigid.azurekeyvaultapp.service.provider;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.dto.ClientCredentialsParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ClientCredentialsProvider implements CredentialProvider {

    private final Validator validator;

    public ClientCredentialsProvider(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(String authMethod) {
        return GlobalParams.CLIENT_CREDENTIALS.getValue().equals(authMethod);
    }

    @Override
    public TokenCredential create(Map<String, String> globalParamsMap) {
        ClientCredentialsParams params = new ClientCredentialsParams(
                globalParamsMap.get(GlobalParams.CLIENT_ID.getValue()),
                globalParamsMap.get(GlobalParams.TENANT_ID.getValue()),
                globalParamsMap.get(GlobalParams.CLIENT_SECRET.getValue())
        );
        validate(params);
        return new ClientSecretCredentialBuilder()
                .clientId(params.clientId())
                .clientSecret(params.clientSecret())
                .tenantId(params.tenantId())
                .build();
    }

    private <T> void validate(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
    }
}
