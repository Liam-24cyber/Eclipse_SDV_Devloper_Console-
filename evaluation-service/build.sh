#!/bin/bash

# Build script for Evaluation Service

set -e

echo "🚀 Building Evaluation Service..."

# Navigate to service directory
cd "$(dirname "$0")"

# Clean and build
echo "📦 Running Maven build..."
mvn clean install -DskipTests

echo "✅ Build complete!"
echo ""
echo "Next steps:"
echo "  1. Run locally: java -jar app/target/evaluation-service-app-latest.jar"
echo "  2. Build Docker: docker build -f Dockerfile.app -t evaluation-service:latest ."
echo "  3. Run Docker: docker run -p 8085:8085 evaluation-service:latest"
echo ""
