#!/bin/sh
# This is a sample setup script for Keruta repositories.
# This script is executed after the repository is cloned.
# You can customize this script to install dependencies, set up the environment, etc.

set -ue


# Install Java if not already installed
if ! command -v java > /dev/null; then
 echo 'Java not found, attempting to install...'
 if command -v apt-get > /dev/null; then
   apt-get update && apt-get install -y --no-install-recommends default-jre && apt-get clean && rm -rf /var/lib/apt/lists/*
   export JAVA_HOME=/usr/lib/jvm/default-java
 elif command -v apk > /dev/null; then
   apk add --no-cache openjdk11-jre
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
 elif command -v yum > /dev/null; then
   yum install -y java-11-openjdk && yum clean all
   export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk
 else
   echo 'Warning: Java not found and could not be installed'
 fi
 # Add JAVA_HOME to environment if it was set
 if [ -n \"\$JAVA_HOME\" ]; then
   echo \"export JAVA_HOME=\$JAVA_HOME\" >> ~/.bashrc
   echo \"export PATH=\$PATH:\$JAVA_HOME/bin\" >> ~/.bashrc
   echo \"Java installed, JAVA_HOME set to \$JAVA_HOME\"
 fi
fi