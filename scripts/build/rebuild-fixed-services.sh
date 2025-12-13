#!/bin/bash

# ========================================================================
# Rebuild Fixed Services Script
# Rebuilds scenario-library-service and webhook-management-service
# with Jackson JSR310 fixes applied
# ========================================================================

set -e  # Exit on error

echo "========================================="
echo "Rebuilding Fixed Services"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Function to print status
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${YELLOW}ℹ${NC} $1"
}

# Set JAVA_HOME to Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
print_info "Using Java: $JAVA_HOME"
echo ""

# Step 1: Build scenario-library-service
echo "========================================="
echo "Building scenario-library-service"
echo "========================================="

print_info "Running Maven build..."
cd scenario-library-service
mvn clean install -DskipTests -Dcyclonedx.skip=true
print_status "scenario-library-service Maven build complete"
cd ..

print_info "Stopping existing scenario-library-service container..."
docker-compose stop scenario-library-service 2>/dev/null || true
docker-compose rm -f scenario-library-service 2>/dev/null || true

print_info "Removing old Docker image..."
docker rmi -f scenario-library-service:1.0 2>/dev/null || true

print_info "Building new Docker image..."
docker build -t scenario-library-service:1.0 -f scenario-library-service/Dockerfile.app .
print_status "scenario-library-service Docker image built"

echo ""

# Step 2: Build webhook-management-service
echo "========================================="
echo "Building webhook-management-service"
echo "========================================="

print_info "Running Maven build..."
cd webhook-management-service
mvn clean install -DskipTests -Dcyclonedx.skip=true
print_status "webhook-management-service Maven build complete"
cd ..

print_info "Stopping existing webhook-management-service container..."
docker-compose stop webhook-management-service 2>/dev/null || true
docker-compose rm -f webhook-management-service 2>/dev/null || true

print_info "Removing old Docker image..."
docker rmi -f webhook-management-service:1.0 2>/dev/null || true

print_info "Building new Docker image..."
docker build -t webhook-management-service:1.0 -f webhook-management-service/Dockerfile.app .
print_status "webhook-management-service Docker image built"

echo ""

# Step 3: Restart services
echo "========================================="
echo "Restarting Services"
echo "========================================="

print_info "Starting scenario-library-service..."
docker-compose up -d scenario-library-service
print_status "scenario-library-service started"

print_info "Starting webhook-management-service..."
docker-compose up -d webhook-management-service
print_status "webhook-management-service started"

echo ""

# Wait for services to be healthy
print_info "Waiting for services to be healthy (30 seconds)..."
sleep 30

# Check service status
echo ""
echo "========================================="
echo "Service Status"
echo "========================================="

docker-compose ps scenario-library-service webhook-management-service

echo ""
echo "========================================="
echo "Rebuild Complete! 🎉"
echo "========================================="
echo ""
echo "✅ scenario-library-service - Jackson JSR310 configured"
echo "✅ webhook-management-service - Jackson JSR310 + RabbitMQ deserialization fixed"
echo ""
echo "Services should now properly handle:"
echo "  - Java 8 date/time types (LocalDateTime, Instant, etc.)"
echo "  - RabbitMQ message deserialization from other services"
echo "  - GraphQL and REST API responses with timestamps"
echo ""
echo "Next steps:"
echo "  1. Test GraphQL queries to verify date/time serialization works"
echo "  2. Publish test events to RabbitMQ to verify webhook delivery works"
echo "  3. Check webhook_deliveries table for stored webhook calls"
echo ""
