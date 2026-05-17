package com.bigid.azurekeyvaultapp.service.provider;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.bigid.azurekeyvaultapp.constant.GlobalParams;
import com.bigid.azurekeyvaultapp.dto.ManagedIdentityParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ManagedIdentityProvider implements CredentialProvider {

    private final Validator validator;

    public ManagedIdentityProvider(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(String authMethod) {
        return GlobalParams.MANAGED_IDENTITY.getValue().equals(authMethod);
    }

    @Override
    public TokenCredential create(Map<String, String> globalParamsMap) {
        ManagedIdentityParams params = new ManagedIdentityParams(
                globalParamsMap.get(GlobalParams.AZURE_KEY_VAULT_URL.getValue()),
                globalParamsMap.get(GlobalParams.SCOPE.getValue()),
                globalParamsMap.get(GlobalParams.CLIENT_ID.getValue())
        );
        Set<ConstraintViolation<ManagedIdentityParams>> violations = validator.validate(params);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
        ManagedIdentityCredentialBuilder builder = new ManagedIdentityCredentialBuilder();
        if (StringUtils.isNotBlank(params.clientId())) {
            builder.clientId(params.clientId());
        }
        return builder.build();
    }
}
