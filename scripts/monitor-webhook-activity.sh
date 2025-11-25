#!/bin/bash

# 🎥 Webhook Service Activity Recorder
# This script captures webhook service logs and shows event processing activity

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
RED='\033[0;31m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║       Webhook Service Activity Monitor                ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

DURATION=${1:-30}
echo -e "${YELLOW}📹 Recording webhook service activity for ${DURATION} seconds...${NC}"
echo -e "${CYAN}   Watch for: Event reception → Processing → Delivery${NC}"
echo ""
echo "────────────────────────────────────────────────────────────────────────────────"

# Statistics
RECEIVED_COUNT=0
PROCESSING_COUNT=0
DELIVERED_COUNT=0
FAILED_COUNT=0

# Follow logs with timeout
timeout $DURATION docker logs -f webhook-management-service 2>&1 | while IFS= read -r line; do
    # Parse and display different types of events
    if echo "$line" | grep -q "Received scenario event"; then
        ((RECEIVED_COUNT++))
        EVENT_ID=$(echo "$line" | grep -o '"eventId":"[^"]*' | cut -d'"' -f4)
        EVENT_TYPE=$(echo "$line" | grep -o '"eventType":"[^"]*' | cut -d'"' -f4)
        echo -e "${GREEN}📥 [RECEIVED]${NC} Scenario Event: $EVENT_TYPE (ID: $EVENT_ID)"
        
    elif echo "$line" | grep -q "Received simulation event"; then
        ((RECEIVED_COUNT++))
        EVENT_ID=$(echo "$line" | grep -o '"eventId":"[^"]*' | cut -d'"' -f4)
        EVENT_TYPE=$(echo "$line" | grep -o '"eventType":"[^"]*' | cut -d'"' -f4)
        echo -e "${CYAN}📥 [RECEIVED]${NC} Simulation Event: $EVENT_TYPE (ID: $EVENT_ID)"
        
    elif echo "$line" | grep -q "Received track event"; then
        ((RECEIVED_COUNT++))
        EVENT_ID=$(echo "$line" | grep -o '"eventId":"[^"]*' | cut -d'"' -f4)
        EVENT_TYPE=$(echo "$line" | grep -o '"eventType":"[^"]*' | cut -d'"' -f4)
        echo -e "${BLUE}📥 [RECEIVED]${NC} Track Event: $EVENT_TYPE (ID: $EVENT_ID)"
        
    elif echo "$line" | grep -q "Processing event"; then
        ((PROCESSING_COUNT++))
        EVENT_INFO=$(echo "$line" | sed 's/.*Processing event //')
        echo -e "${YELLOW}⚙️  [PROCESSING]${NC} $EVENT_INFO"
        
    elif echo "$line" | grep -q "Found.*webhooks for event type"; then
        WEBHOOK_INFO=$(echo "$line" | sed 's/.*Found //')
        echo -e "${MAGENTA}🔍 [MATCHED]${NC} $WEBHOOK_INFO"
        
    elif echo "$line" | grep -q "Delivering event.*to webhook"; then
        DELIVERY_INFO=$(echo "$line" | sed 's/.*Delivering event //')
        echo -e "${CYAN}📤 [DELIVERING]${NC} Event $DELIVERY_INFO"
        
    elif echo "$line" | grep -q "Successfully delivered"; then
        ((DELIVERED_COUNT++))
        SUCCESS_INFO=$(echo "$line" | sed 's/.*Successfully delivered //')
        echo -e "${GREEN}✅ [SUCCESS]${NC} Delivered $SUCCESS_INFO"
        
    elif echo "$line" | grep -q "Failed to deliver"; then
        ((FAILED_COUNT++))
        FAIL_INFO=$(echo "$line" | sed 's/.*Failed to deliver //')
        echo -e "${RED}❌ [FAILED]${NC} $FAIL_INFO"
        
    elif echo "$line" | grep -qi "error\|exception" && ! echo "$line" | grep -q "No error"; then
        echo -e "${RED}⚠️  [ERROR]${NC} $(echo "$line" | tail -c 100)"
    fi
done 2>/dev/null || true

echo "────────────────────────────────────────────────────────────────────────────────"
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              Activity Summary                          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}📊 Recording Complete!${NC}"
echo ""

# Get actual statistics from the recording
ACTUAL_RECEIVED=$(docker logs webhook-management-service --since ${DURATION}s 2>&1 | grep -c "Received.*event" || echo 0)
ACTUAL_PROCESSING=$(docker logs webhook-management-service --since ${DURATION}s 2>&1 | grep -c "Processing event" || echo 0)
ACTUAL_DELIVERED=$(docker logs webhook-management-service --since ${DURATION}s 2>&1 | grep -c "Successfully delivered" || echo 0)
ACTUAL_FAILED=$(docker logs webhook-management-service --since ${DURATION}s 2>&1 | grep -c "Failed to deliver" || echo 0)

echo -e "${YELLOW}Activity in last ${DURATION} seconds:${NC}"
echo -e "  📥 Events Received:    ${GREEN}$ACTUAL_RECEIVED${NC}"
echo -e "  ⚙️  Events Processed:   ${CYAN}$ACTUAL_PROCESSING${NC}"
echo -e "  ✅ Webhooks Delivered: ${GREEN}$ACTUAL_DELIVERED${NC}"
echo -e "  ❌ Webhooks Failed:    ${RED}$ACTUAL_FAILED${NC}"
echo ""

# Show current webhook deliveries in database
TOTAL_DB_DELIVERIES=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhook_deliveries;" | xargs)
echo -e "${CYAN}💾 Total Webhook Deliveries in Database:${NC} $TOTAL_DB_DELIVERIES"
echo ""
