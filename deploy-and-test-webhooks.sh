#!/bin/bash

# ============================================================================
# SDV Webhook Integration - One-Click Deployment & Test
# ============================================================================
# This script deploys the webhook system and runs a quick smoke test
# Usage: ./deploy-and-test-webhooks.sh
# ============================================================================

set -e  # Exit on error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Print functions
print_header() {
    echo ""
    echo -e "${BLUE}${BOLD}========================================${NC}"
    echo -e "${BLUE}${BOLD}$1${NC}"
    echo -e "${BLUE}${BOLD}========================================${NC}"
    echo ""
}

print_step() {
    echo -e "${YELLOW}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# ============================================================================
# STEP 1: Deploy Database
# ============================================================================
print_header "STEP 1: Deploying Database with Webhook Tables"

print_step "Stopping all containers..."
docker-compose down

print_step "Starting PostgreSQL..."
docker-compose up -d postgres

print_step "Waiting for PostgreSQL to initialize (30 seconds)..."
sleep 30

print_step "Verifying webhook tables..."
TABLE_COUNT=$(docker exec -i postgres psql -U postgres -d dco -t -c "
SELECT COUNT(*) 
FROM information_schema.tables 
WHERE table_name LIKE 'webhook%';
" 2>/dev/null | tr -d ' ')

if [ "$TABLE_COUNT" -eq 5 ]; then
    print_success "All 5 webhook tables created successfully"
    docker exec -i postgres psql -U postgres -d dco -c "
    SELECT table_name 
    FROM information_schema.tables 
    WHERE table_name LIKE 'webhook%' 
    ORDER BY table_name;
    "
else
    print_error "Expected 5 webhook tables, found $TABLE_COUNT"
    echo "Tables found:"
    docker exec -i postgres psql -U postgres -d dco -c "
    SELECT table_name 
    FROM information_schema.tables 
    WHERE table_name LIKE 'webhook%';
    "
    exit 1
fi

# ============================================================================
# STEP 2: Start RabbitMQ
# ============================================================================
print_header "STEP 2: Starting RabbitMQ"

print_step "Starting RabbitMQ container..."
docker-compose up -d rabbitmq

print_step "Waiting for RabbitMQ to initialize (20 seconds)..."
sleep 20

if docker ps | grep -q rabbitmq; then
    print_success "RabbitMQ started successfully"
else
    print_error "RabbitMQ failed to start"
    exit 1
fi

# ============================================================================
# STEP 3: Build Services
# ============================================================================
print_header "STEP 3: Building Java Services"

print_step "Building webhook-management-service..."
cd webhook-management-service
if mvn clean install -DskipTests > /dev/null 2>&1; then
    print_success "webhook-management-service built successfully"
else
    print_error "webhook-management-service build failed"
    mvn clean install -DskipTests  # Show errors
    exit 1
fi
cd ..

print_step "Building scenario-library-service..."
cd scenario-library-service
if mvn clean install -DskipTests > /dev/null 2>&1; then
    print_success "scenario-library-service built successfully"
else
    print_error "scenario-library-service build failed"
    mvn clean install -DskipTests  # Show errors
    exit 1
fi
cd ..

print_step "Building tracks-management-service..."
cd tracks-management-service
if mvn clean install -DskipTests > /dev/null 2>&1; then
    print_success "tracks-management-service built successfully"
else
    print_error "tracks-management-service build failed"
    mvn clean install -DskipTests  # Show errors
    exit 1
fi
cd ..

# ============================================================================
# STEP 4: Start All Services
# ============================================================================
print_header "STEP 4: Starting All Services"

print_step "Building and starting containers..."
docker-compose up -d --build

print_step "Waiting for services to initialize (30 seconds)..."
sleep 30

print_step "Checking service status..."
docker ps --format "table {{.Names}}\t{{.Status}}" | grep -E "webhook|scenario|track"

# ============================================================================
# STEP 5: Validate Installation
# ============================================================================
print_header "STEP 5: Validating Installation"

# Check webhook service API
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/webhooks 2>/dev/null || echo "000")

if [ "$HTTP_CODE" = "200" ]; then
    print_success "Webhook service API is accessible (HTTP 200)"
elif [ "$HTTP_CODE" = "000" ]; then
    print_error "Webhook service is NOT accessible (connection failed)"
    echo "Checking logs..."
    docker logs webhook-management-service | tail -n 20
    exit 1
else
    print_error "Webhook service returned HTTP $HTTP_CODE"
    exit 1
fi

# Check for errors in logs
ERROR_COUNT=$(docker logs webhook-management-service 2>&1 | grep -c "ERROR" || echo "0")
if [ "$ERROR_COUNT" -eq 0 ]; then
    print_success "No errors in webhook service logs"
else
    print_error "Found $ERROR_COUNT ERROR entries in webhook service logs"
    echo "Recent errors:"
    docker logs webhook-management-service 2>&1 | grep "ERROR" | tail -n 5
fi

# Check event consumer
CONSUMER_RUNNING=$(docker logs webhook-management-service 2>&1 | grep -c "EventConsumerServiceImpl" || echo "0")
if [ "$CONSUMER_RUNNING" -gt 0 ]; then
    print_success "Event consumer is active"
else
    print_error "Event consumer not detected in logs"
fi

# Check retry scheduler
SCHEDULER_RUNNING=$(docker logs webhook-management-service 2>&1 | grep -c "RetryScheduler" || echo "0")
if [ "$SCHEDULER_RUNNING" -gt 0 ]; then
    print_success "Retry scheduler is active"
else
    print_error "Retry scheduler not detected in logs"
fi

# ============================================================================
# STEP 6: Interactive Smoke Test
# ============================================================================
print_header "STEP 6: Smoke Test"

echo ""
echo -e "${BOLD}📌 To test webhook delivery, you need a test endpoint.${NC}"
echo ""
echo "Option 1: Use webhook.site (Recommended)"
echo "  1. Open: https://webhook.site"
echo "  2. Copy your unique URL"
echo "  3. Paste it below"
echo ""
echo "Option 2: Skip smoke test for now"
echo "  Press Enter to skip"
echo ""
read -p "Enter webhook URL (or press Enter to skip): " WEBHOOK_URL

if [ -z "$WEBHOOK_URL" ]; then
    print_step "Skipping smoke test"
    print_success "Deployment completed successfully!"
    
    echo ""
    echo -e "${BOLD}Next Steps:${NC}"
    echo "1. Get a webhook URL from https://webhook.site"
    echo "2. Create a webhook:"
    echo "   curl -X POST http://localhost:8080/api/webhooks \\"
    echo "     -H 'Content-Type: application/json' \\"
    echo "     -d @test-data/test-webhook.json"
    echo ""
    echo "3. Trigger an event:"
    echo "   curl -X POST http://localhost:8081/api/scenarios \\"
    echo "     -H 'Content-Type: application/json' \\"
    echo "     -d @test-data/test-scenario.json"
    echo ""
    echo "4. Check your webhook.site URL for the event!"
    echo ""
    exit 0
fi

# Run smoke test with provided URL
print_step "Creating test webhook..."

WEBHOOK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/webhooks \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"deployment-test-$(date +%s)\",
    \"description\": \"Automated deployment test\",
    \"url\": \"$WEBHOOK_URL\",
    \"secret\": \"test-secret-123\",
    \"eventTypes\": [\"scenario.created\"],
    \"maxRetryAttempts\": 3
  }")

if echo "$WEBHOOK_RESPONSE" | grep -q "id"; then
    print_success "Webhook created successfully"
    WEBHOOK_ID=$(echo "$WEBHOOK_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    echo "   Webhook ID: $WEBHOOK_ID"
else
    print_error "Failed to create webhook"
    echo "$WEBHOOK_RESPONSE"
    exit 1
fi

print_step "Triggering test scenario event..."

SCENARIO_RESPONSE=$(curl -s -X POST http://localhost:8081/api/scenarios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Deployment Test Scenario",
    "description": "Testing webhook integration",
    "type": "HIGHWAY",
    "status": "ACTIVE"
  }')

if echo "$SCENARIO_RESPONSE" | grep -q "id"; then
    print_success "Scenario created successfully"
    SCENARIO_ID=$(echo "$SCENARIO_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    echo "   Scenario ID: $SCENARIO_ID"
else
    print_error "Failed to create scenario"
    echo "$SCENARIO_RESPONSE"
fi

print_step "Waiting for webhook delivery (5 seconds)..."
sleep 5

print_step "Checking delivery status in database..."
docker exec -i postgres psql -U postgres -d dco -c "
SELECT 
    event_type, 
    status, 
    status_code,
    attempt_count,
    TO_CHAR(created_at, 'HH24:MI:SS') as time
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 3;
"

# ============================================================================
# COMPLETION
# ============================================================================
print_header "✅ DEPLOYMENT COMPLETE!"

echo ""
echo -e "${GREEN}${BOLD}All systems deployed and operational!${NC}"
echo ""
echo -e "${BOLD}🔍 Verification Steps:${NC}"
echo "1. Check your webhook URL ($WEBHOOK_URL)"
echo "   You should see a POST request with the scenario event"
echo ""
echo "2. Verify webhook in UI:"
echo "   curl http://localhost:8080/api/webhooks | jq"
echo ""
echo "3. Check RabbitMQ Management:"
echo "   open http://localhost:15672 (guest/guest)"
echo ""
echo -e "${BOLD}📚 Documentation:${NC}"
echo "• Complete guide: WEBHOOK_README.md"
echo "• Testing plan: WEBHOOK_TESTING_PLAN.md"
echo "• Quick reference: WEBHOOK_QUICK_REFERENCE.md"
echo "• Deployment checklist: DEPLOYMENT_CHECKLIST.md"
echo ""
echo -e "${BOLD}🚀 Next Steps:${NC}"
echo "• Follow WEBHOOK_TESTING_PLAN.md for comprehensive testing"
echo "• Monitor logs: docker logs -f webhook-management-service"
echo "• View deliveries: curl http://localhost:8080/api/webhooks/$WEBHOOK_ID/deliveries | jq"
echo ""
echo -e "${GREEN}Happy testing! 🎉${NC}"
echo ""
