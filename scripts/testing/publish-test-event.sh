#!/bin/bash

echo "📤 Publishing test event to RabbitMQ..."

# Create a test event payload
EVENT_PAYLOAD='{
  "eventId": "test-event-'$(date +%s)'",
  "eventType": "scenario.created",
  "aggregateId": "scenario-test-123",
  "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
  "payload": {
    "scenarioId": "scenario-test-123",
    "name": "Manual Test Scenario",
    "description": "Testing webhook delivery manually"
  }
}'

echo "Event payload:"
echo "$EVENT_PAYLOAD" | jq '.'

# Use RabbitMQ HTTP API to publish message
curl -s -u admin:admin123 -X POST \
  "http://localhost:15672/api/exchanges/%2F/amq.default/publish" \
  -H "Content-Type: application/json" \
  -d '{
    "properties": {
      "content_type": "application/json",
      "delivery_mode": 2
    },
    "routing_key": "scenario.events",
    "payload": "'"$(echo $EVENT_PAYLOAD | sed 's/"/\\"/g')"'",
    "payload_encoding": "string"
  }' | jq '.'

echo ""
echo "✅ Event published to scenario.events queue"
echo ""
echo "📊 Wait 3 seconds and check results..."
sleep 3

echo ""
echo "🔍 Checking webhook deliveries:"
docker exec postgres psql -U postgres -d postgres -c \
"SELECT id, webhook_id, event_type, status, attempt_count, status_code, created_at 
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 5;"

echo ""
echo "📝 Check webhook service logs:"
docker logs webhook-management-service --tail 30 | grep -i "event\|delivery\|webhook" || echo "No relevant logs found"
