#!/bin/bash

# Navigate to the project directory (adjust if needed)
cd $(dirname $0)

# Clean and package the application
echo "Cleaning and packaging the application..."
./mvnw clean package -DskipTests

# Run the application
echo "Starting the LifeLink application..."
./mvnw spring-boot:run