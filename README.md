# Azure Key Vault Integration

## Description
This application integrates with Azure Key Vault to securely manage and consume secrets within BigID data sources and configurations.

## Documentation

[TPA Framework](https://bigidio.atlassian.net/wiki/spaces/CON/pages/2121433138/TPA+Framework)

## Installation

```bash
$ cd services
$ mvn clean install
```

## Running the app

```bash
# Running a Spring Boot App with Maven
$ cd services
$ mvn spring-boot:run
```
```bash
# Running a Spring Boot JAR File
$ cd services
$ java -jar target/bigid-azure-key-vault-integration-236.0-SNAPSHOT.jar
```
```bash
# Running a Spring Boot Main class
run com.bigid.azurekeyvaultapp.AzureKeyVaultApplication from IDE
```
```bash
# Running a Spring Boot App with Docker
$ cd services
$ docker build -t bigid-azure-key-vault-integration:latest .
$ docker run -p 8083:8083 bigid-azure-key-vault-integration
```
# Running a Spring Boot App with docker-compose
```bash
# Run docker-compose from client-deployment folder:
$ cd client-deployment
$ docker-compose up -d
```
# Stopping a Spring Boot App with docker-compose
```bash
# Run docker-compose from client-deployment folder:
$ cd client-deployment
$ docker-compose down
```
# Running a Spring Boot App with Kubernetes
```bash
# Build the Docker image:
$ cd services
$ docker build -t bigid-azure-key-vault:latest .
```
```bash
# If using Kind, load the locally built image into the Kind cluster:
$ kind create cluster
$ kind load docker-image bigid-azure-key-vault:latest
```
```bash
# Apply the manifests from client-deployment folder:
$ cd client-deployment
$ kubectl apply -f deployment.yaml
$ kubectl apply -f service.yaml
```
```bash
# Access the app:
# Port forwarding to access the app from your local machine:
$ kubectl port-forward service/bigid-azure-key-vault-service 8083:8083
# Cluster IP: curl http://<cluster-ip>:8083
```

# Test

```bash
# unit tests
$ cd services
$ mvn test
```
## Test coverage
### Report Files:
#### HTML Report: target/site/jacoco/index.html
#### Open this file in a browser to view detailed coverage information.

## Integration Tests
### 1. Create the Required Secret in Azure Key Vault
#### To support the integration test, you need to create a secret in your Azure Key Vault with the specified name and format.

#### Secret Name: my-secret
#### Secret Value:
#### The value should be a JSON object like this:
```json
{
  "principalId": "your_principal_id",
  "principal_secret_enc": "your_principal_secret",
  "tenantId": "your_tenant_id"
}
```
### 2. Steps to Add the Secret to Azure Key Vault
#### Login to Azure CLI:
```bash
$ az login
```
#### Set the Correct Subscription:
```bash
az account set --subscription "<your-subscription-id>"
```
#### Add the Secret to Azure Key Vault: Replace <key-vault-name> with your Key Vault name:
```bash
#--vault-name: The name of your Azure Key Vault.
#--name: The name of the secret (my-secret).
#--value: The JSON object containing the required data.
az keyvault secret set --vault-name "<key-vault-name>" --name "my-secret" --value '{"principalId": "your_principal_id", "principal_secret_enc": "your_principal_secret", "tenantId": "your_tenant_id"}'
```
#### Verify the Secret Exists:
```bash
az keyvault secret show --vault-name "<key-vault-name>" --name "my-secret"
```
### 3. Obtain the Azure Key Vault Credentials
#### Obtain the Required Credentials:
#### Client ID: The application (service principal) registered in Azure Active Directory.

#### Go to Azure Portal > Azure Active Directory > App Registrations > Your App > Overview.
#### Client Secret: The secret created for your application.

#### Go to Azure Portal > Azure Active Directory > App Registrations > Your App > Certificates & Secrets > Client Secrets.
#### Tenant ID: The tenant associated with your Azure subscription.

#### Go to Azure Portal > Azure Active Directory > Overview.
#### Azure Key Vault URL: The URL of your Key Vault.

#### Go to Azure Portal > Key Vaults > Your Key Vault > Properties.

### 4. Fill application-test.yml with Azure Key Vault Credentials
### 5. Run the Integration Test
```bash
$ cd services
$ mvn test -Dtest=ExecutionControllerIT
```
