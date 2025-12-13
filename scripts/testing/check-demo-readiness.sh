#!/bin/bash

# 🎬 Demo Readiness Check
# Verifies all services are ready for demo recording

echo "🎬 Demo Readiness Check"
echo "======================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_service() {
    local url=$1
    local name=$2
    
    if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "200\|302\|401"; then
        echo -e "${GREEN}✅ $name${NC} - Ready"
        return 0
    else
        echo -e "${RED}❌ $name${NC} - Not ready"
        return 1
    fi
}

all_ready=true

echo "Checking services..."
echo ""

# Check UI
if ! check_service "http://localhost:3000" "UI (Developer Console)"; then
    all_ready=false
fi

# Check pgAdmin
if ! check_service "http://localhost:5050" "pgAdmin"; then
    all_ready=false
fi

# Check MinIO
if ! check_service "http://localhost:9001" "MinIO Console"; then
    all_ready=false
fi

# Check RabbitMQ
if ! check_service "http://localhost:15672" "RabbitMQ Management"; then
    all_ready=false
fi

# Check Prometheus
if ! check_service "http://localhost:9090" "Prometheus"; then
    all_ready=false
fi

# Check Grafana
if ! check_service "http://localhost:3001" "Grafana"; then
    all_ready=false
fi

# Check API Gateway
if ! check_service "http://localhost:8080/actuator/health" "API Gateway"; then
    all_ready=false
fi

echo ""
echo "======================="

if [ "$all_ready" = true ]; then
    echo -e "${GREEN}🎉 ALL SERVICES READY FOR DEMO!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Open browser tabs (see DEMO_RECORDING_GUIDE.md)"
    echo "2. Login to all services"
    echo "3. Prepare pgAdmin SQL query"
    echo "4. Start recording!"
    echo ""
    echo "Quick reference:"
    echo "  UI:         http://localhost:3000 (admin / admin123)"
    echo "  pgAdmin:    http://localhost:5050 (admin@example.com / admin)"
    echo "  MinIO:      http://localhost:9001 (minioadmin / minioadmin)"
    echo "  RabbitMQ:   http://localhost:15672 (guest / guest)"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana:    http://localhost:3001 (admin / admin)"
else
    echo -e "${RED}⚠️  SOME SERVICES NOT READY${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Check if all containers are running:"
    echo "   docker-compose ps"
    echo ""
    echo "2. Restart failed services:"
    echo "   docker-compose restart"
    echo ""
    echo "3. Wait 2-3 minutes for services to fully start"
    echo ""
    echo "4. Run this check again:"
    echo "   ./check-demo-readiness.sh"
fi

echo ""
