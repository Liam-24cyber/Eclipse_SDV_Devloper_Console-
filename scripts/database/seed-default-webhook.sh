#!/bin/bash
# Seed a default webhook directly in database for testing

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  Seeding Default Webhook for E2E Testing              ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Webhook configuration
WEBHOOK_ID="12345678-1234-1234-1234-123456789012"
WEBHOOK_NAME="Default E2E Test Webhook"
WEBHOOK_URL="https://webhook.site/unique-url-here"

echo -e "${YELLOW}📝 Creating webhook in database...${NC}"
echo -e "  Name: ${WEBHOOK_NAME}"
echo -e "  URL: ${WEBHOOK_URL}"
echo ""

# Insert webhook
docker exec postgres psql -U postgres -d postgres << 'EOF'
-- Delete existing test webhook if it exists
DELETE FROM webhook_event_types WHERE webhook_id = '12345678-1234-1234-1234-123456789012'::uuid;
DELETE FROM webhooks WHERE id = '12345678-1234-1234-1234-123456789012'::uuid;

-- Insert webhook
INSERT INTO webhooks (
    id, name, description, url, is_active, 
    max_retry_attempts, initial_retry_delay, backoff_multiplier, max_retry_delay,
    total_deliveries, successful_deliveries, failed_deliveries,
    created_at, updated_at
) VALUES (
    '12345678-1234-1234-1234-123456789012'::uuid,
    'Default E2E Test Webhook',
    'Default webhook for E2E testing - receives all events',
    'https://webhook.site/unique-url-here',
    true,
    3,
    5000,
    2.0,
    300000,
    0,
    0,
    0,
    NOW(),
    NOW()
);

-- Insert event type subscriptions
INSERT INTO webhook_event_types (webhook_id, event_type) VALUES
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.started'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.completed'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.failed'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'scenario.created'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'track.created');

EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Webhook created successfully!${NC}"
    echo ""
    echo -e "${BLUE}📊 Verification:${NC}"
    docker exec postgres psql -U postgres -d postgres -c "SELECT id, name, url, is_active FROM webhooks WHERE id = '${WEBHOOK_ID}';"
    echo ""
    docker exec postgres psql -U postgres -d postgres -c "SELECT event_type FROM webhook_event_types WHERE webhook_id = '${WEBHOOK_ID}';"
    echo ""
    echo -e "${GREEN}✓ Webhook is ready to receive events!${NC}"
    echo -e "${YELLOW}💡 To test: ./test-direct-event-flow.sh${NC}"
else
    echo -e "${RED}✗ Failed to create webhook${NC}"
    exit 1
fi
