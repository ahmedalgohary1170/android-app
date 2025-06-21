#!/bin/bash

# This script sets up the Gradle wrapper properly

# Install Gradle locally if not installed
if ! command -v gradle &> /dev/null
then
    echo "Gradle is not installed. Installing Gradle..."
    mkdir -p /tmp/gradle-install
    cd /tmp/gradle-install
    wget https://services.gradle.org/distributions/gradle-7.5-bin.zip
    unzip -q gradle-7.5-bin.zip
    export PATH=$PATH:/tmp/gradle-install/gradle-7.5/bin
    cd -
fi

# Generate Gradle wrapper files properly
gradle wrapper --gradle-version 7.5 --distribution-type bin

# Make sure gradlew is executable
chmod +x gradlew

echo "Gradle wrapper setup completed successfully!"
