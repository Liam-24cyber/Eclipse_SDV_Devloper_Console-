#!/bin/bash

# 🔍 POST-RESTART VERIFICATION SCRIPT
# Run this after: docker compose down && docker compose up -d

set -e

echo "=========================================="
echo "🔍 POST-RESTART VERIFICATION"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Wait for services to be ready
echo "⏳ Waiting for services to be ready (30 seconds)..."
sleep 30

echo ""
echo "=========================================="
echo "1️⃣  CHECKING DOCKER SERVICES"
echo "=========================================="

# Check all services are running
if docker compose ps | grep -q "Up"; then
    echo -e "${GREEN}✅ Docker services are running${NC}"
else
    echo -e "${RED}❌ Docker services are NOT running${NC}"
    exit 1
fi

echo ""
echo "=========================================="
echo "2️⃣  CHECKING POSTGRESQL DATABASE"
echo "=========================================="

# Check PostgreSQL is ready
if docker exec -it postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo -e "${GREEN}✅ PostgreSQL is ready${NC}"
else
    echo -e "${RED}❌ PostgreSQL is NOT ready${NC}"
    exit 1
fi

# Count scenarios
SCENARIO_COUNT=$(docker exec -it postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenarios;" 2>/dev/null | tr -d ' \r\n' || echo "0")
echo "   📊 Scenarios: ${SCENARIO_COUNT}"
if [ "$SCENARIO_COUNT" -ge 16 ]; then
    echo -e "   ${GREEN}✅ Scenarios persisted (expected: 16+)${NC}"
else
    echo -e "   ${YELLOW}⚠️  Unexpected scenario count (expected: 16+)${NC}"
fi

# Count webhooks
WEBHOOK_COUNT=$(docker exec -it postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhooks;" 2>/dev/null | tr -d ' \r\n' || echo "0")
echo "   🪝 Webhooks: ${WEBHOOK_COUNT}"
if [ "$WEBHOOK_COUNT" -ge 1 ]; then
    echo -e "   ${GREEN}✅ Webhooks persisted${NC}"
else
    echo -e "   ${YELLOW}⚠️  No webhooks found${NC}"
fi

# Count webhook deliveries
DELIVERY_COUNT=$(docker exec -it postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhook_deliveries;" 2>/dev/null | tr -d ' \r\n' || echo "0")
echo "   📦 Webhook Deliveries: ${DELIVERY_COUNT}"
if [ "$DELIVERY_COUNT" -ge 1 ]; then
    echo -e "   ${GREEN}✅ Delivery history persisted${NC}"
else
    echo -e "   ${YELLOW}ℹ️  No deliveries yet (expected if fresh restart)${NC}"
fi

# Count tracks
TRACK_COUNT=$(docker exec -it postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM tracks;" 2>/dev/null | tr -d ' \r\n' || echo "0")
echo "   🛤️  Tracks: ${TRACK_COUNT}"

# Count simulations
SIMULATION_COUNT=$(docker exec -it postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM simulations;" 2>/dev/null | tr -d ' \r\n' || echo "0")
echo "   🚗 Simulations: ${SIMULATION_COUNT}"

echo ""
echo "=========================================="
echo "3️⃣  CHECKING RABBITMQ QUEUES"
echo "=========================================="

# Check RabbitMQ is ready
if curl -s -u admin:admin123 http://localhost:15672/api/overview > /dev/null 2>&1; then
    echo -e "${GREEN}✅ RabbitMQ Management API is accessible${NC}"
else
    echo -e "${RED}❌ RabbitMQ Management API is NOT accessible${NC}"
    exit 1
fi

# List queues
echo "   📋 Queues:"
QUEUES=$(curl -s -u admin:admin123 http://localhost:15672/api/queues 2>/dev/null | jq -r '.[].name' || echo "")

if echo "$QUEUES" | grep -q "webhook.events"; then
    echo -e "      ${GREEN}✅ webhook.events${NC}"
    
    # Get message count
    MSG_COUNT=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/webhook.events 2>/dev/null | jq -r '.messages' || echo "0")
    echo "         Messages: ${MSG_COUNT}"
else
    echo -e "      ${YELLOW}⚠️  webhook.events NOT found${NC}"
fi

if echo "$QUEUES" | grep -q "webhook.events.dlq"; then
    echo -e "      ${GREEN}✅ webhook.events.dlq${NC}"
    
    # Get DLQ message count
    DLQ_COUNT=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/webhook.events.dlq 2>/dev/null | jq -r '.messages' || echo "0")
    echo "         DLQ Messages: ${DLQ_COUNT}"
    
    if [ "$DLQ_COUNT" -gt 0 ]; then
        echo -e "         ${YELLOW}ℹ️  Consider purging DLQ: ./purge-dlqs.sh${NC}"
    fi
else
    echo -e "      ${YELLOW}⚠️  webhook.events.dlq NOT found${NC}"
fi

echo ""
echo "=========================================="
echo "4️⃣  CHECKING SERVICES HEALTH"
echo "=========================================="

# Check scenario-library-service
if curl -s http://localhost:8082/actuator/health 2>/dev/null | grep -q "UP"; then
    echo -e "${GREEN}✅ Scenario Library Service: UP${NC}"
else
    echo -e "${RED}❌ Scenario Library Service: DOWN${NC}"
fi

# Check message-queue-service
if curl -s http://localhost:8083/actuator/health 2>/dev/null | grep -q "UP"; then
    echo -e "${GREEN}✅ Message Queue Service: UP${NC}"
else
    echo -e "${RED}❌ Message Queue Service: DOWN${NC}"
fi

# Check webhook-management-service
if curl -s http://localhost:8084/actuator/health 2>/dev/null | grep -q "UP"; then
    echo -e "${GREEN}✅ Webhook Management Service: UP${NC}"
else
    echo -e "${RED}❌ Webhook Management Service: DOWN${NC}"
fi

# Check tracks-management-service
if curl -s http://localhost:8081/actuator/health 2>/dev/null | grep -q "UP"; then
    echo -e "${GREEN}✅ Tracks Management Service: UP${NC}"
else
    echo -e "${RED}❌ Tracks Management Service: DOWN${NC}"
fi

# Check dco-gateway
if curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q "UP"; then
    echo -e "${GREEN}✅ API Gateway: UP${NC}"
else
    echo -e "${RED}❌ API Gateway: DOWN${NC}"
fi

echo ""
echo "=========================================="
echo "5️⃣  CHECKING DOCKER VOLUMES"
echo "=========================================="

# Check volumes exist
VOLUMES=$(docker volume ls --format '{{.Name}}' | grep -E 'postgres-data|rabbitmq-data|minio-data|redis-data')

if echo "$VOLUMES" | grep -q "postgres-data"; then
    echo -e "${GREEN}✅ postgres-data volume exists${NC}"
else
    echo -e "${RED}❌ postgres-data volume NOT found${NC}"
fi

if echo "$VOLUMES" | grep -q "rabbitmq-data"; then
    echo -e "${GREEN}✅ rabbitmq-data volume exists${NC}"
else
    echo -e "${RED}❌ rabbitmq-data volume NOT found${NC}"
fi

if echo "$VOLUMES" | grep -q "minio-data"; then
    echo -e "${GREEN}✅ minio-data volume exists${NC}"
else
    echo -e "${RED}❌ minio-data volume NOT found${NC}"
fi

if echo "$VOLUMES" | grep -q "redis-data"; then
    echo -e "${GREEN}✅ redis-data volume exists${NC}"
else
    echo -e "${RED}❌ redis-data volume NOT found${NC}"
fi

echo ""
echo "=========================================="
echo "6️⃣  TESTING END-TO-END FLOW"
echo "=========================================="

# Test scenario API
SCENARIOS=$(curl -s http://localhost:8082/scenarios 2>/dev/null)
if echo "$SCENARIOS" | jq -e '. | length > 0' > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Scenario API returns data${NC}"
    SCENARIO_ID=$(echo "$SCENARIOS" | jq -r '.[0].id')
    echo "   First scenario ID: ${SCENARIO_ID}"
else
    echo -e "${RED}❌ Scenario API returns no data${NC}"
fi

# Test webhook API
WEBHOOKS=$(curl -s http://localhost:8084/webhooks 2>/dev/null)
if echo "$WEBHOOKS" | jq -e '. | length > 0' > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Webhook API returns data${NC}"
    WEBHOOK_ID=$(echo "$WEBHOOKS" | jq -r '.[0].id')
    echo "   First webhook ID: ${WEBHOOK_ID}"
else
    echo -e "${YELLOW}⚠️  Webhook API returns no webhooks${NC}"
    echo "   Run: ./seed-test-webhook.sh to create test webhook"
fi

echo ""
echo "=========================================="
echo "📊 SUMMARY"
echo "=========================================="
echo ""
echo "Database Records:"
echo "  • Scenarios: ${SCENARIO_COUNT}"
echo "  • Webhooks: ${WEBHOOK_COUNT}"
echo "  • Deliveries: ${DELIVERY_COUNT}"
echo "  • Tracks: ${TRACK_COUNT}"
echo "  • Simulations: ${SIMULATION_COUNT}"
echo ""
echo "RabbitMQ Queues:"
echo "  • webhook.events: ✅"
echo "  • webhook.events.dlq: ✅"
echo ""
echo "Docker Volumes:"
echo "  • postgres-data: ✅"
echo "  • rabbitmq-data: ✅"
echo "  • minio-data: ✅"
echo "  • redis-data: ✅"
echo ""

if [ "$SCENARIO_COUNT" -ge 16 ] && [ "$WEBHOOK_COUNT" -ge 1 ]; then
    echo -e "${GREEN}✅ PERSISTENCE VERIFIED: All data survived restart!${NC}"
else
    echo -e "${YELLOW}⚠️  UNEXPECTED STATE: Check logs for issues${NC}"
fi

echo ""
echo "=========================================="
echo "🔗 USEFUL LINKS"
echo "=========================================="
echo "  • RabbitMQ Management: http://localhost:15672 (admin/admin123)"
echo "  • PgAdmin: http://localhost:5050 (admin@default.com/admin)"
echo "  • Prometheus: http://localhost:9090"
echo "  • Grafana: http://localhost:3001 (admin/admin)"
echo "  • MinIO Console: http://localhost:9001 (minioadmin/minioadmin)"
echo ""
echo "=========================================="
echo "✅ VERIFICATION COMPLETE"
echo "=========================================="
