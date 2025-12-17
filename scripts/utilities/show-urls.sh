#!/bin/bash

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "=========================================="
echo "SDV Developer Console - Quick Access URLs"
echo "=========================================="
echo ""

echo -e "${BLUE}🌐 Frontend & API Gateway${NC}"
echo "  Developer Console UI:    http://localhost:3000"
echo "  GraphQL Playground:      http://localhost:8080/graphql"
echo ""

echo -e "${BLUE}🔧 Microservices${NC}"
echo "  Scenario Service:        http://localhost:8081/swagger-ui.html"
echo "  Tracks Service:          http://localhost:8082/swagger-ui.html"
echo "  Message Queue Service:   http://localhost:8083/swagger-ui.html"
echo "  Webhook Service:         http://localhost:8084/swagger-ui.html"
echo ""

echo -e "${BLUE}📊 Infrastructure & Monitoring${NC}"
echo "  RabbitMQ Management:     http://localhost:15672    (admin/admin123)"
echo "  pgAdmin (Database):      http://localhost:5050     (admin@sdv.com/admin123)"
echo "  MinIO (Storage):         http://localhost:9001     (minioadmin/minioadmin)"
echo "  Prometheus (Metrics):    http://localhost:9090"
echo "  Grafana (Dashboards):    http://localhost:3001     (admin/admin)"
echo ""

echo -e "${YELLOW}🧪 Test the Complete Flow (Simulation → RabbitMQ → Webhook → DB)${NC}"
echo ""
echo "1️⃣  Publish test event to RabbitMQ:"
echo -e "${GREEN}"
cat << 'EOF'
curl -X POST http://localhost:8083/api/v1/events/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "scenario.created",
    "payload": {
      "scenarioId": "test-001",
      "name": "Highway Test",
      "timestamp": "'$(date -u +"%Y-%m-%dT%H:%M:%SZ")'"
    }
  }'
EOF
echo -e "${NC}"
echo ""

echo "2️⃣  Monitor in RabbitMQ:"
echo "   Open: http://localhost:15672 → Queues tab"
echo "   Check: scenario.events queue"
echo ""

echo "3️⃣  Verify in Database:"
echo "   Open: http://localhost:5050"
echo "   Query: SELECT * FROM webhook_events ORDER BY created_at DESC;"
echo ""

echo "4️⃣  Check service logs:"
echo "   docker logs webhook-management-service --tail 50"
echo ""

echo -e "${YELLOW}📋 Quick Health Check:${NC}"
echo ""

# Check services
echo -n "  Checking RabbitMQ... "
if curl -s -u admin:admin123 http://localhost:15672/api/overview > /dev/null 2>&1; then
    echo -e "${GREEN}✓ UP${NC}"
else
    echo -e "${RED}✗ DOWN${NC}"
fi

echo -n "  Checking Message Queue Service... "
STATUS=$(curl -s http://localhost:8083/actuator/health 2>/dev/null | grep -o '"status":"UP"')
if [ ! -z "$STATUS" ]; then
    echo -e "${GREEN}✓ UP${NC}"
else
    echo -e "${RED}✗ DOWN${NC}"
fi

echo -n "  Checking Webhook Service... "
STATUS=$(curl -s http://localhost:8084/actuator/health 2>/dev/null | grep -o '"status":"UP"')
if [ ! -z "$STATUS" ]; then
    echo -e "${GREEN}✓ UP${NC}"
else
    echo -e "${RED}✗ DOWN${NC}"
fi

echo -n "  Checking pgAdmin... "
if curl -s http://localhost:5050 > /dev/null 2>&1; then
    echo -e "${GREEN}✓ UP${NC}"
else
    echo -e "${RED}✗ DOWN${NC}"
fi

echo ""
echo "=========================================="
echo -e "${GREEN}All URLs are ready to use!${NC}"
echo "=========================================="
echo ""
echo "📖 For detailed documentation, see: SERVICE_URLS.md"
