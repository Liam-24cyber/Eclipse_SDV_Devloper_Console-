#!/bin/bash
# Set Java 17 for this project
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

echo "✓ JAVA_HOME set to: $JAVA_HOME"
java -version
echo ""
echo "Maven version:"
mvn -version
