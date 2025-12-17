#!/bin/bash

# ========================================================================
# Complete Project Rebuild Script
# This script rebuilds ALL components of the SDV Developer Console
# ========================================================================

set -e  # Exit on error

echo "========================================="
echo "SDV Developer Console - Complete Rebuild"
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

echo "Working directory: $SCRIPT_DIR"
echo ""

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

# Step 1: Clean previous builds
echo "========================================="
echo "Step 1: Cleaning Previous Builds"
echo "========================================="

print_info "Stopping all running containers..."
docker-compose down 2>/dev/null || true

print_info "Removing old Docker images..."
docker rmi -f scenario-library-service:1.0 2>/dev/null || true
docker rmi -f tracks-management-service:1.0 2>/dev/null || true
docker rmi -f dco-gateway:1.0 2>/dev/null || true
docker rmi -f developer-console-ui:1.0 2>/dev/null || true
docker rmi -f postgres:1.0 2>/dev/null || true
docker rmi -f minio:1.0 2>/dev/null || true

print_status "Cleanup complete"
echo ""

# Step 2: Build Frontend (Next.js)
echo "========================================="
echo "Step 2: Building Frontend (Next.js)"
echo "========================================="

cd developer-console-ui/app

print_info "Installing dependencies..."
npm install --legacy-peer-deps

print_info "Building Next.js application..."
npm run build

print_status "Frontend build complete"
echo ""

cd ../..

# Step 3: Build Backend Services (Maven)
echo "========================================="
echo "Step 3: Building Backend Services"
echo "========================================="

# Set JAVA_HOME to Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
print_info "Using Java: $JAVA_HOME"

# Build Scenario Library Service
print_info "Building scenario-library-service..."
cd scenario-library-service
mvn clean install -DskipTests -Dcyclonedx.skip=true
print_status "scenario-library-service built"
cd ..

# Build Tracks Management Service  
print_info "Building tracks-management-service..."
cd tracks-management-service
mvn clean install -DskipTests -Dcyclonedx.skip=true
print_status "tracks-management-service built"
cd ..

# Build DCO Gateway
print_info "Building dco-gateway..."
cd dco-gateway
mvn clean install -DskipTests -Dcyclonedx.skip=true
print_status "dco-gateway built"
cd ..

echo ""

# Step 4: Build Docker Images
echo "========================================="
echo "Step 4: Building Docker Images"
echo "========================================="

print_info "Building scenario-library-service:1.0..."
docker build --no-cache -t scenario-library-service:1.0 -f scenario-library-service/Dockerfile.app .
print_status "scenario-library-service image built"

print_info "Building tracks-management-service:1.0..."
docker build --no-cache -t tracks-management-service:1.0 -f tracks-management-service/Dockerfile.app .
print_status "tracks-management-service image built"

print_info "Building dco-gateway:1.0..."
docker build --no-cache -t dco-gateway:1.0 -f dco-gateway/Dockerfile.app .
print_status "dco-gateway image built"

print_info "Building developer-console-ui:1.0..."
docker build --no-cache -t developer-console-ui:1.0 -f developer-console-ui/Dockerfile .
print_status "developer-console-ui image built"

print_info "Building postgres:1.0..."
docker build -t postgres:1.0 -f postgres/Dockerfile.database .
print_status "postgres image built"

print_info "Building minio:1.0..."
docker build -t minio:1.0 -f minio/Dockerfile.minio .
print_status "minio image built"

echo ""

# Step 5: Verify Builds
echo "========================================="
echo "Step 5: Verifying Builds"
echo "========================================="

print_info "Checking Docker images..."
docker images | grep -E "scenario-library-service|tracks-management-service|dco-gateway|developer-console-ui|postgres|minio" | grep "1.0"

print_status "All images verified"
echo ""

# Step 6: Summary
echo "========================================="
echo "Build Complete Summary"
echo "========================================="
echo ""
echo "✅ Frontend (Next.js) built successfully"
echo "✅ Backend services built successfully:"
echo "   - scenario-library-service"
echo "   - tracks-management-service"
echo "   - dco-gateway"
echo "✅ Docker images created successfully:"
echo "   - scenario-library-service:1.0"
echo "   - tracks-management-service:1.0"
echo "   - dco-gateway:1.0"
echo "   - developer-console-ui:1.0"
echo "   - postgres:1.0"
echo "   - minio:1.0"
echo ""
echo "========================================="
echo "Next Steps:"
echo "========================================="
echo ""
echo "1. Configure Minio keys (if not already done):"
echo "   - Open Minio console: http://localhost:9001"
echo "   - Create access keys"
echo "   - Update minio/minio_keys.env with:"
echo "     AWS_ACCESS_KEY_ID=<your-access-key>"
echo "     AWS_SECRET_ACCESS_KEY=<your-secret-key>"
echo ""
echo "2. Start all services:"
echo "   ./20-deploy-script.sh <access-key> <secret-key>"
echo ""
echo "3. Access the application:"
echo "   Frontend:  http://localhost:3000"
echo "   Gateway:   http://localhost:8080"
echo "   Scenario:  http://localhost:8082"
echo "   Tracks:    http://localhost:8081"
echo ""
echo "========================================="
echo "Build completed successfully! 🎉"
echo "========================================="
