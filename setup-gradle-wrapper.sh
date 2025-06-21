#!/bin/bash

# This script downloads and sets up the Gradle wrapper

# Gradle version to use
GRADLE_VERSION=7.5

# Download gradle-wrapper.jar
mkdir -p gradle/wrapper
curl -L -o gradle/wrapper/gradle-wrapper.jar "https://github.com/gradle/gradle/raw/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar"

# Make sure gradlew is executable
chmod +x gradlew

echo "Gradle wrapper setup completed successfully!"
