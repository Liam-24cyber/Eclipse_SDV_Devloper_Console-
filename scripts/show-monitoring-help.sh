#!/bin/bash

# 🎯 Quick Monitoring Helper
# Shows available monitoring commands and their usage

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║       SDV Monitoring Tools - Quick Reference          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${CYAN}📊 Available Monitoring Tools:${NC}"
echo ""

echo -e "${GREEN}1. Enhanced E2E Demo${NC}"
echo -e "   ${YELLOW}Command:${NC} ./run-e2e-demo.sh"
echo -e "   ${YELLOW}Purpose:${NC} Complete workflow test with real-time monitoring"
echo -e "   ${YELLOW}Shows:${NC}  Queue stats, message flow, webhook deliveries, timeline"
echo ""

echo -e "${GREEN}2. RabbitMQ Live Monitor${NC}"
echo -e "   ${YELLOW}Command:${NC} ./monitor-rabbitmq-live.sh"
echo -e "   ${YELLOW}Purpose:${NC} Real-time queue monitoring (auto-refresh)"
echo -e "   ${YELLOW}Shows:${NC}  Queue stats, message rates, consumer counts"
echo -e "   ${YELLOW}Stop:${NC}    Press Ctrl+C"
echo ""

echo -e "${GREEN}3. Webhook Activity Recorder${NC}"
echo -e "   ${YELLOW}Command:${NC} ./monitor-webhook-activity.sh [seconds]"
echo -e "   ${YELLOW}Purpose:${NC} Record webhook processing activity"
echo -e "   ${YELLOW}Shows:${NC}  Event reception, processing, delivery status"
echo -e "   ${YELLOW}Example:${NC} ./monitor-webhook-activity.sh 60"
echo ""

echo -e "${CYAN}🔧 Quick Commands:${NC}"
echo ""

echo -e "${YELLOW}Check Queue Status:${NC}"
echo -e "   curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F | jq -r '.[] | \"\(.name): \(.messages) msgs\"'"
echo ""

echo -e "${YELLOW}Check Webhook Deliveries:${NC}"
echo -e "   docker exec postgres psql -U postgres -d postgres -c \"SELECT event_type, COUNT(*) FROM webhook_deliveries GROUP BY event_type;\""
echo ""

echo -e "${YELLOW}Stream Webhook Logs:${NC}"
echo -e "   docker logs -f webhook-management-service 2>&1 | grep -E \"Received|Delivered\""
echo ""

echo -e "${YELLOW}View Recent Deliveries:${NC}"
echo -e "   docker exec postgres psql -U postgres -d postgres -c \"SELECT * FROM webhook_deliveries ORDER BY created_at DESC LIMIT 10;\""
echo ""

echo -e "${CYAN}🎯 Common Workflows:${NC}"
echo ""

echo -e "${GREEN}Development Testing:${NC}"
echo -e "   Terminal 1: ./monitor-rabbitmq-live.sh"
echo -e "   Terminal 2: ./run-e2e-demo.sh"
echo ""

echo -e "${GREEN}Debugging Issues:${NC}"
echo -e "   Terminal 1: ./monitor-webhook-activity.sh 60"
echo -e "   Terminal 2: <trigger your test>"
echo ""

echo -e "${GREEN}Quick Health Check:${NC}"
echo -e "   ./run-e2e-demo.sh"
echo ""

echo -e "${CYAN}📚 Documentation:${NC}"
echo -e "   ${YELLOW}Full Guide:${NC}  cat MONITORING.md"
echo -e "   ${YELLOW}Summary:${NC}     cat MONITORING_SUMMARY.md"
echo ""

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""
