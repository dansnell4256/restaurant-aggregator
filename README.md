# Restaurant Aggregator

This project is a learning exercise for the following:

1. Java/Springboot Microservices
2. AI Supported Development
3. GitHub Actions
4. Monorepo Development

## Project Structure

The Restaurant Aggregator is a microservices-based application with the following components:

- **catalogue**: Service for restaurant catalogue management
- **menu**: Service for menu management
- **orders**: Service for order processing
- **common**: Shared library for common functionality

## Setup and Development

### Prerequisites

- Java 21 or later
- Gradle
- Docker and Docker Compose (for containerized deployment)

### Local Development

For local development:

1. Copy the example property files:

```bash
cp catalogue/src/main/resources/application-local.properties.example catalogue/src/main/resources/application-local.properties
cp menu/src/main/resources/application-local.properties.example menu/src/main/resources/application-local.properties
cp orders/src/main/resources/application-local.properties.example orders/src/main/resources/application-local.properties
```

2. Edit the property files with your actual credentials
3. Run the applications with the `local` profile:

```bash
./gradlew catalogue:bootRun --args='--spring.profiles.active=local'
```

### Docker Environment

For Docker Compose:

1. Copy the example .env file:

```bash
cp infrastructure/.env.example infrastructure/.env
```

2. Edit the .env file with actual credentials
3. Run Docker Compose:

```bash
cd infrastructure
docker-compose up
```

## Code Quality and Linting

The project uses a comprehensive set of linting tools to maintain code quality. We use the pre-commit framework to automatically run these checks before commits.

### Linting Setup

1. Install the pre-commit framework:

```bash
# Using pip
pip install pre-commit

# Or using Homebrew on macOS
brew install pre-commit
```

2. The repository already contains a `.pre-commit-config.yaml` file with our linting configuration
3. Install the pre-commit hooks:

```bash
pre-commit install
```

Now, the linting checks will automatically run on each commit.

### Running Linting Manually

You can also run the linting checks manually:

```bash
# Run all linting tools on all files
pre-commit run --all-files

# Run a specific check (e.g., just Checkstyle)
pre-commit run checkstyle
```

### Available Linting Tools

The project uses the following linting tools:

- **Checkstyle**: Enforces coding standards and style guidelines
- **SpotBugs**: Finds potential bugs in your code
- **PMD**: Detects common programming flaws and code smells

### Skipping Checks (Use Sparingly)

In rare cases where you need to bypass linting (not recommended):

```bash
git commit -m "Your message" --no-verify
```

## Testing

Run tests with:

```bash
./gradlew test
```

## Continuous Integration

The project uses GitHub Actions for continuous integration. Every push and pull request to the main branch triggers:

1. Code compilation
2. Running tests
3. Linting checks

## Additional Documentation

Refer to the following documents for more detailed information:

- [Credential Management](CREDENTIAL_MANAGEMENT.md)
