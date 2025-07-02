package com.bigid.azurekeyvaultapp;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.FixedDelay;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.SecretServiceVersion;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClient;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class ConfigIT {
    public static final String VAULT_NAME = "test-vault";
    protected static LowkeyVaultContainer vaultContainer;
    protected static SecretClient secretClient;

    @BeforeAll
    static void startContainer() {
        // Specify the Docker image for Lowkey Vault
        DockerImageName imageName = DockerImageName.parse("nagyesta/lowkey-vault:3.2.0");

        // Initialize the container using the builder
        vaultContainer = LowkeyVaultContainerBuilder.lowkeyVault(imageName)
                .vaultNames(Set.of(VAULT_NAME)) // Add a vault named 'test-vault'
                .build()
                .withImagePullPolicy(PullPolicy.defaultPolicy());

        // Start the container
        vaultContainer.start();

        iniSecretClient();
    }

    private static void iniSecretClient() {
        String vaultUrl = vaultContainer.getVaultBaseUrl(VAULT_NAME);
        final AuthorityOverrideFunction authorityOverrideFunction = new AuthorityOverrideFunction(
                vaultContainer.getVaultAuthority(VAULT_NAME),
                vaultContainer.getEndpointAuthority());
        final TokenCredential credential = new BasicAuthenticationCredential(vaultContainer.getUsername(), vaultContainer.getPassword());
        final ApacheHttpClient httpClient = new ApacheHttpClient(authorityOverrideFunction,
                new TrustSelfSignedStrategy(), new DefaultHostnameVerifier());
        secretClient = new SecretClientBuilder()
                .vaultUrl(vaultUrl)
                .credential(credential)
                .httpClient(httpClient)
                .serviceVersion(SecretServiceVersion.V7_3)
                .disableChallengeResourceVerification()
                .retryPolicy(new RetryPolicy(new FixedDelay(0, Duration.ZERO)))
                .buildClient();
    }

    protected static void addSecret(String key, String value) {
        KeyVaultSecret secret = secretClient.setSecret(key, value);
        assertNotNull(secret);
    }

    protected String getVaultBaseUrl() {
        return vaultContainer.getVaultBaseUrl(VAULT_NAME);
    }
}