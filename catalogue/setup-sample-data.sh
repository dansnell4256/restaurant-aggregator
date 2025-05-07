#!/bin/bash

# setup-sample-data.sh
# Script to set up sample restaurant data for the catalogue service
# This script should be run from the project root directory

echo "Setting up sample data for the catalogue service..."

# Check if we're in the project root directory
if [ ! -d "catalogue" ]; then
    echo "Error: Please run this script from the project root directory"
    exit 1
fi

# Make sure the catalogue service is built
echo "Building catalogue service..."
./gradlew catalogue:build --exclude-task test -q || {
    echo "Error: Failed to build catalogue service"
    exit 1
}

# Start the catalogue service with the dev profile to load sample data
echo "Starting catalogue service with dev profile to load sample data..."
echo "The service will start and load the sample data automatically."
echo "Press Ctrl+C when the loading is complete (after you see 'Sample data loaded successfully' message)."
echo "-------------------------------------------------------------"
./gradlew catalogue:bootRun --args='--spring.profiles.active=dev' || {
    echo "Error: Failed to start catalogue service"
    exit 1
}

echo "-------------------------------------------------------------"
echo "Sample data setup complete."
echo ""
echo "To use the catalogue service with the sample data:"
echo "  ./gradlew catalogue:bootRun --args='--spring.profiles.active=dev'"
echo ""
echo "To reset the sample data without restarting, use the API endpoint:"
echo "  curl -X POST http://localhost:8081/api/v1/admin/reset-test-data"
echo ""
