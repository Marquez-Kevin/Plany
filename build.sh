#!/bin/bash

# Build script for Render deployment

echo "Building Plany application..."

# Navigate to the plany directory
cd plany

# Clean and package the application
mvn clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful! JAR file created in target/ directory"
    ls -la target/*.jar
else
    echo "Build failed!"
    exit 1
fi

# Copy the JAR to the root directory for easier access
cp target/*.jar ../plany.jar

echo "Build completed successfully!" 