# Credential Management

This document outlines how credentials are managed in the resturant-aggregator project.

## Local Development

For local development:

1. Copy the example property files:

cp catalogue/src/main/resources/application-local.properties.example catalogue/src/main/resources/application-local.properties
cp menu/src/main/resources/application-local.properties.example menu/src/main/resources/application-local.properties
cp orders/src/main/resources/application-local.properties.example orders/src/main/resources/application-local.properties

2. Edit the property files with your actual credentials
3. Run the applications with the `local` profile:

./gradlew catalogue --args='--spring.profiles.active=local'

4. IMPORTANT: Never commit your `application-local.properties` files to git

## Docker Environment

For Docker Compose:

1. Copy the example .env file:

cp infrastructure/.env.example infrastructure/.env

2. Edit the .env file with actual credentials
3. Run Docker Compose:

cd infrastructure
docker-compose up

## Production Environment

For production deployments:

1. Use environment variables or a secure vault solution
2. For Kubernetes deployments, use Kubernetes Secrets
3. For cloud deployments, use the cloud provider's secret management service:
- AWS: Secrets Manager or Parameter Store
- Azure: Key Vault
- GCP: Secret Manager

## Keystore for SSL Certificates

For SSL certificates:

1. Store keystore files outside of the project directory or in a designated `/config` directory
2. Reference them in the application with environment variables:

server.ssl.key-store=${KEYSTORE_PATH}
server.ssl.key-store-password=${KEYSTORE_PASSWORD}

## Additional Security Best Practices

1. Rotate credentials regularly
2. Use different credentials for different environments
3. Limit credential access to only necessary services
4. Audit credential access
5. Use the principle of least privilege