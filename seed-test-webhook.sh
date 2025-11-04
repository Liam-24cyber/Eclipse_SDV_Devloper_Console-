#!/bin/bash

echo "🌱 Seeding test webhook..."

# Delete existing test webhooks
docker exec postgres psql -U postgres -d postgres -c \
"DELETE FROM webhook_event_types WHERE webhook_id IN (SELECT id FROM webhooks WHERE url LIKE '%localhost:3000%');
DELETE FROM webhooks WHERE url LIKE '%localhost:3000%';"

# Insert test webhook
docker exec postgres psql -U postgres -d postgres -c \
"INSERT INTO webhooks (id, name, description, url, secret, is_active, created_at, updated_at) 
VALUES ('99999999-9999-9999-9999-999999999999', 'Test E2E Webhook', 'Webhook for end-to-end testing', 'http://host.docker.internal:3000/webhook', 'test-secret-123', true, NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET is_active=true, updated_at=NOW();"

# Subscribe to all relevant event types
docker exec postgres psql -U postgres -d postgres -c \
"INSERT INTO webhook_event_types (webhook_id, event_type) VALUES
('99999999-9999-9999-9999-999999999999', 'scenario.created'),
('99999999-9999-9999-9999-999999999999', 'scenario.updated'),
('99999999-9999-9999-9999-999999999999', 'scenario.deleted'),
('99999999-9999-9999-9999-999999999999', 'track.created'),
('99999999-9999-9999-9999-999999999999', 'track.updated'),
('99999999-9999-9999-9999-999999999999', 'simulation.started'),
('99999999-9999-9999-9999-999999999999', 'simulation.completed'),
('99999999-9999-9999-9999-999999999999', 'simulation.failed')
ON CONFLICT DO NOTHING;"

echo ""
echo "✅ Test webhook seeded with ID: 99999999-9999-9999-9999-999999999999"
echo "📍 URL: http://host.docker.internal:3000/webhook"
echo "🔑 Secret: test-secret-123"
echo ""

# Verify
echo "📊 Webhook subscriptions:"
docker exec postgres psql -U postgres -d postgres -c \
"SELECT w.id, w.url, wet.event_type 
FROM webhooks w 
JOIN webhook_event_types wet ON w.id = wet.webhook_id 
WHERE w.id = '99999999-9999-9999-9999-999999999999'
ORDER BY wet.event_type;"
