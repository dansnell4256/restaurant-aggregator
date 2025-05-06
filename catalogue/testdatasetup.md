# Restaurant Aggregator: Test Data Implementation Guide

This guide explains how to implement and use the test data functionality for the Restaurant Aggregator's catalogue service. The test data includes sample restaurants and menu items for development and testing purposes.

## Overview

The test data system provides:

1. Sample data for two restaurant tenants (American Grill and Italian Bistro)
2. Automatic loading of data when running with dev or test profiles
3. An API endpoint to reset the data without restarting the application
4. Clear separation of test data from production code

## Implementation Details

### 1. Directory Structure

```
catalogue/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── catalogue/
│   │   │           ├── controller/
│   │   │           │   └── admin/
│   │   │           │       └── DataResetController.java  # Reset endpoint
│   │   │           ├── util/
│   │   │           │   └── TestDataLoader.java           # Data loader
│   │   │           └── repository/
│   │   │               ├── CategoryRepository.java
│   │   │               ├── CategoryItemRepository.java
│   │   │               ├── ComponentRepository.java
│   │   │               └── CategoryItemCustomizationRepository.java
│   │   └── resources/
│   │       ├── data/
│   │       │   └── restaurant-test-data.json             # Test data in JSON format
│   │       ├── application-dev.properties                # Dev profile configuration
│   │       └── application-test.properties               # Test profile configuration
```

### 2. Key Components

#### TestDataLoader.java

This class implements Spring's `CommandLineRunner` interface to load test data when the application starts. It only activates when using the `dev` or `test` profiles.

Key features:
- Clears existing data before loading
- Loads components, categories, category items, and customizations in the correct order
- Establishes relationships between entities
- Reports progress and errors using SLF4J logging

#### restaurant-test-data.json

This JSON file contains all the test data, including:
- Restaurant tenants
- Menu categories
- Components (ingredients)
- Menu items
- Customizations

The file is structured to match the entity relationships in the application.

#### DataResetController.java

This REST controller provides an endpoint to manually reset the test data without restarting the application. It's only available in dev and test profiles for security.

Endpoint:
- `POST /api/v1/admin/reset-test-data`

#### Repository Interfaces

The repository interfaces provide methods to interact with the database entities. Each repository includes methods specifically for tenant-aware operations.

### 3. Profile-Specific Configuration

The test data functionality uses Spring profiles to control when it's active:

- `application-dev.properties`: Development environment configuration
- `application-test.properties`: Testing environment configuration

Both use an in-memory H2 database with console access enabled.

## How to Use

### Loading Test Data on Startup

To run the application with test data, use the `dev` or `test` profile:

```bash
# For development
./gradlew catalogue:bootRun --args='--spring.profiles.active=dev'

# For testing
./gradlew catalogue:bootRun --args='--spring.profiles.active=test'
```

With Docker Compose:

```yaml
catalogue:
  environment:
    - SPRING_PROFILES_ACTIVE=docker,dev
```

### Accessing the Data

Once loaded, you can access the test data through the catalogue service's API endpoints:

```bash
# Get all categories for American Grill
curl http://localhost:8081/api/v1/tenants/american-grill/catalogue/categories

# Get all categories for Italian Bistro
curl http://localhost:8081/api/v1/tenants/italian-bistro/catalogue/categories
```

### Resetting the Data

To reset the test data without restarting:

```bash
curl -X POST http://localhost:8081/api/v1/admin/reset-test-data
```

### Viewing the Database (Development)

Access the H2 Console to view and query the database directly:

1. Open a browser to http://localhost:8081/h2-console
2. Connect using:
    - JDBC URL: `jdbc:h2:mem:catalogue-dev-db` (or `catalogue-test-db` for test profile)
    - Username: `dev` (or `test` for test profile)
    - Password: `dev` (or `test` for test profile)

## Modifying the Test Data

To modify the test data:

1. Edit the `restaurant-test-data.json` file
2. Restart the application or call the reset endpoint

For structural changes to the data model:

1. Update the entity classes
2. Update the `TestDataLoader.java` to handle the new structure
3. Update the `restaurant-test-data.json` file with the new data
4. Restart the application

## Best Practices

1. Keep the test data realistic but concise
2. Always use the tenant ID to scope operations
3. Use the reset endpoint before running tests that modify data
4. Don't use test data functionality in production environments
5. Keep test data free of sensitive or inappropriate content

## Troubleshooting

### Data not loading
- Verify you're using the `dev` or `test` profile
- Check the logs for errors during startup
- Ensure the `restaurant-test-data.json` file is in the correct location

### Reset endpoint not working
- Verify you're using the `dev` or `test` profile
- Check for errors in the server logs
- Ensure the controller is properly registered with Spring

### Entity relationship errors
- Check that the relationships in the JSON match the entity models
- Ensure IDs are properly linked between related entities
- Verify that required fields are not null

## Future Enhancements

Potential improvements for the test data system:

1. Add multiple test data sets for different scenarios
2. Implement data generation scripts for larger datasets
3. Add performance testing data with thousands of items
4. Create a UI for managing test data during development
5. Support export/import of real production data in anonymized form
