#!/bin/bash

# 🚀 Start E2E API Server
# This script starts the Node.js API server that exposes E2E workflow endpoints

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  Starting E2E Demo API Server                         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js is not installed${NC}"
    echo -e "  Please install Node.js from: https://nodejs.org/"
    exit 1
fi

echo -e "${GREEN}✅ Node.js found: $(node --version)${NC}"

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo -e "${RED}✗ npm is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ npm found: $(npm --version)${NC}"
echo ""

# Install dependencies if needed
if [ ! -d "node_modules" ] || [ ! -f "node_modules/express/package.json" ]; then
    echo -e "${CYAN}📦 Installing dependencies...${NC}"
    npm install express
    echo -e "${GREEN}✅ Dependencies installed${NC}"
    echo ""
fi

# Check if services are running
echo -e "${CYAN}🔍 Checking if Docker services are running...${NC}"
if ! docker ps | grep -q postgres; then
    echo -e "${YELLOW}⚠️  PostgreSQL is not running${NC}"
    echo -e "   Run: ${CYAN}./start-all-services.sh${NC} first"
    echo ""
    read -p "Start services now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        ./start-all-services.sh
    else
        echo -e "${RED}Exiting...${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✅ Docker services are running${NC}"
echo ""

# Start the API server
echo -e "${CYAN}🚀 Starting E2E API Server...${NC}"
echo ""

node e2e-api-server.js
