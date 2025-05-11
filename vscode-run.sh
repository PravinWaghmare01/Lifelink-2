#!/bin/bash

echo "Starting LifeLink Organ Donation System for VS Code environment..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first or use the Maven wrapper."
    echo "Using Maven wrapper instead..."
    cmd="./mvnw"
else
    cmd="mvn"
fi

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! command -v mysql &> /dev/null; then
    echo "MySQL client not found. Make sure MySQL server is running."
else
    mysql_status=$(mysql -u root -proot -e "SELECT 1" 2>/dev/null || echo "failed")
    if [[ $mysql_status == *"failed"* ]]; then
        echo "Warning: Cannot connect to MySQL. Make sure MySQL is running with username 'root' and password 'root'."
        echo "You can edit the database configuration in src/main/resources/application.properties"
    else
        echo "MySQL connection successful."
        
        # Create database if it doesn't exist
        mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS lifelink;" 
        echo "Database 'lifelink' is ready."
    fi
fi

# Run the application
echo "Building and starting the application..."
$cmd spring-boot:run

# Check if the application started successfully
if [ $? -ne 0 ]; then
    echo "Failed to start the application. Please check the logs above for details."
else
    echo "Application started successfully!"
    echo "Access the application at: http://localhost:8080/lifelink"
fi