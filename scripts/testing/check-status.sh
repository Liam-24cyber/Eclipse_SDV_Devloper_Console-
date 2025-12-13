#!/bin/bash

##############################################################################
# Service Status Checker - Quick health check for all services
##############################################################################

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo -e "${BLUE}  SDV Developer Console - Status Check${NC}"
echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo ""

# Check Docker containers
echo -e "${YELLOW}Docker Containers:${NC}"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "developer-console|dco-gateway|tracks-management|scenario-library|webhook-management|message-queue|postgres|rabbitmq|redis|minio" || echo -e "${RED}No containers running${NC}"
echo ""

# Check service health endpoints
echo -e "${YELLOW}Service Health:${NC}"

check_health() {
    local name=$1
    local url=$2
    
    printf "  %-30s" "$name:"
    response=$(curl -s -f "$url" 2>/dev/null || echo "")
    
    if [[ $response == *"UP"* ]] || [[ $response == *"healthy"* ]] || [[ $response == *"status"* ]]; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Down${NC}"
    fi
}

check_health "DCO Gateway" "http://localhost:8080/actuator/health"
check_health "Tracks Management" "http://localhost:8081/actuator/health"
check_health "Scenario Library" "http://localhost:8082/actuator/health"
check_health "Message Queue" "http://localhost:8083/api/v1/health"
check_health "Webhook Management" "http://localhost:8084/actuator/health"

echo ""

# Check databases
echo -e "${YELLOW}Data Stores:${NC}"
printf "  %-30s" "PostgreSQL:"
if docker exec postgres pg_isready -U postgres >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Ready${NC}"
else
    echo -e "${RED}✗ Not Ready${NC}"
fi

printf "  %-30s" "RabbitMQ:"
if curl -s -u admin:admin123 http://localhost:15672/api/overview >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Ready${NC}"
else
    echo -e "${RED}✗ Not Ready${NC}"
fi

printf "  %-30s" "Redis:"
if docker exec redis redis-cli ping >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Ready${NC}"
else
    echo -e "${RED}✗ Not Ready${NC}"
fi

echo ""

# Check RabbitMQ queues
echo -e "${YELLOW}RabbitMQ Queues:${NC}"
queues=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F 2>/dev/null || echo "")
if [ -n "$queues" ]; then
    echo "$queues" | jq -r '.[] | select(.name | contains("events")) | "  \(.name): \(.messages) messages"' 2>/dev/null || echo "  Unable to parse queue info"
else
    echo -e "  ${RED}Cannot connect to RabbitMQ${NC}"
fi

echo ""
echo -e "${BLUE}═══════════════════════════════════════${NC}"
echo ""
echo "Run './test-e2e.sh' for full end-to-end testing"
echo "Access UI at: http://localhost:3000"
echo "RabbitMQ UI: http://localhost:15672 (admin/admin123)"
echo ""
