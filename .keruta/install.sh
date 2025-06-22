#!/bin/sh
# This is a sample setup script for Keruta repositories.
# This script is executed after the repository is cloned.
# You can customize this script to install dependencies, set up the environment, etc.

set -e

echo "Starting setup script for repository"

# Check if package.json exists and run npm install
if [ -f "package.json" ]; then
    echo "Found package.json, running npm install"
    npm install
fi

# Check if requirements.txt exists and run pip install
if [ -f "requirements.txt" ]; then
    echo "Found requirements.txt, running pip install"
    pip install -r requirements.txt
fi

# Check if build.gradle or build.gradle.kts exists and run gradle build
if [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
    echo "Found Gradle build file, running gradle build"
    ./gradlew build
fi

# Check if pom.xml exists and run mvn install
if [ -f "pom.xml" ]; then
    echo "Found pom.xml, running mvn install"
    mvn install
fi

echo "Setup script completed successfully"