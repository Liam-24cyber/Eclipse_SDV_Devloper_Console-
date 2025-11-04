# 🔧 Fix Action Plan - SDV Developer Console

## Date: November 3, 2025

---

## 📋 **Executive Summary**

This document provides step-by-step fixes for the 4 identified issues preventing end-to-end webhook delivery flow.

**Priority Order:**
1. ✅ Verify Event Publication & Webhook Subscriptions (CRITICAL)
2. ✅ Fix GraphQL Mutations to Use Gateway
3. ✅ Clean Up Dead Letter Queues
4. ✅ Fix Redis Connection

---

## 🎯 **Issue #1: No Webhook Deliveries Created** (CRITICAL)

### Root Cause
The webhook consumer fires but delivery logic doesn't match subscribed event types. Either:
- No events are being published
- Event type strings don't align with webhook subscriptions
- Webhooks are inactive or misconfigured

### Fix Steps

#### Step 1.1: Verify Current Webhook Subscriptions
```bash
# Check what event types webhooks are subscribed to
docker exec postgres psql -U postgres -d postgres -c \
"SELECT w.id, w.url, w.active, wet.event_type 
FROM webhooks w 
LEFT JOIN webhook_event_types wet ON w.id = wet.webhook_id 
ORDER BY w.id;"
```

**Expected Output:**
```
id | url | active | event_type
---+-----+--------+-----------
1  | ... | t      | scenario.created
1  | ... | t      | scenario.updated
2  | ... | t      | simulation.started
```

**Action:** Note down the exact event_type strings. These MUST match what publishers emit.

---

#### Step 1.2: Verify Events Are Being Published

**Test A: Check RabbitMQ for Message Flow**
```bash
# Check if messages are flowing through queues
curl -s -u admin:admin http://localhost:15672/api/queues/%2F | \
jq '.[] | select(.name | contains("events")) | {name, messages, messages_ready, consumers, message_stats}'
```

**Test B: Trigger an Event and Monitor**
```bash
# Terminal 1: Monitor webhook service logs
docker logs -f webhook-management-service

# Terminal 2: Monitor RabbitMQ queues in real-time
watch -n 1 'curl -s -u admin:admin http://localhost:15672/api/queues/%2F/scenario.events | jq "{messages, messages_ready, consumers}"'

# Terminal 3: Create a scenario via GraphQL (see Issue #2 fix)
```

---

#### Step 1.3: Align Event Type Strings

**Review Publisher Code:**
```bash
# Find where events are published
find ./scenario-management-service -name "*.java" -exec grep -l "publishEvent\|EventPublisher" {} \;

# Check event type strings in code
grep -r "eventType\|EventType" ./scenario-management-service/src --include="*.java"
```

**Common Mismatches:**
| Publisher Emits | Webhook Expects | Fix |
|----------------|-----------------|-----|
| `SCENARIO_CREATED` | `scenario.created` | Change to dot notation |
| `scenario-created` | `scenario.created` | Change hyphens to dots |
| `scenario_created` | `scenario.created` | Change underscores to dots |

**Fix Location:** Update event publishers to use the exact strings from `webhook_event_types` table.

---

#### Step 1.4: Seed Test Webhook with Correct Event Types

```bash
# Create a script to seed a test webhook
cat > seed-test-webhook.sh << 'EOF'
#!/bin/bash

# Delete existing test webhooks
docker exec postgres psql -U postgres -d postgres -c \
"DELETE FROM webhook_event_types WHERE webhook_id IN (SELECT id FROM webhooks WHERE url LIKE '%localhost:3000%');
DELETE FROM webhooks WHERE url LIKE '%localhost:3000%';"

# Insert test webhook
docker exec postgres psql -U postgres -d postgres -c \
"INSERT INTO webhooks (id, url, secret, active, created_at, updated_at) 
VALUES (999, 'http://host.docker.internal:3000/webhook', 'test-secret-123', true, NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET active=true, updated_at=NOW();"

# Subscribe to all relevant event types
docker exec postgres psql -U postgres -d postgres -c \
"INSERT INTO webhook_event_types (webhook_id, event_type) VALUES
(999, 'scenario.created'),
(999, 'scenario.updated'),
(999, 'scenario.deleted'),
(999, 'track.created'),
(999, 'track.updated'),
(999, 'simulation.started'),
(999, 'simulation.completed')
ON CONFLICT DO NOTHING;"

echo "✅ Test webhook seeded with ID 999"
echo "📍 URL: http://host.docker.internal:3000/webhook"
echo "🔑 Secret: test-secret-123"

# Verify
docker exec postgres psql -U postgres -d postgres -c \
"SELECT w.id, w.url, wet.event_type 
FROM webhooks w 
JOIN webhook_event_types wet ON w.id = wet.webhook_id 
WHERE w.id = 999;"
EOF

chmod +x seed-test-webhook.sh
./seed-test-webhook.sh
```

---

#### Step 1.5: Add Debug Logging to WebhookEventConsumer

**Locate the file:**
```bash
find ./webhook-management-service -name "WebhookEventConsumer.java"
```

**Add logging around delivery logic:**
```java
@RabbitListener(queues = {"scenario.events", "track.events", "simulation.events"})
public void handleEvent(DomainEvent event) {
    log.info("🔔 Received event: type={}, id={}", event.getEventType(), event.getEventId());
    
    // Find matching webhooks
    List<Webhook> matchingWebhooks = webhookRepository.findByEventType(event.getEventType());
    log.info("📊 Found {} webhooks subscribed to event type: {}", 
             matchingWebhooks.size(), event.getEventType());
    
    if (matchingWebhooks.isEmpty()) {
        log.warn("⚠️  No webhooks subscribed to event type: {}", event.getEventType());
        return;
    }
    
    // Deliver to each webhook
    for (Webhook webhook : matchingWebhooks) {
        log.info("📤 Creating delivery for webhook: id={}, url={}", 
                 webhook.getId(), webhook.getUrl());
        webhookService.deliverEventToWebhooks(event, List.of(webhook));
    }
    
    log.info("✅ Event processing complete for eventId={}", event.getEventId());
}
```

**Rebuild and redeploy:**
```bash
cd webhook-management-service
mvn clean package -DskipTests
docker-compose up -d --build webhook-management-service
docker logs -f webhook-management-service
```

---

#### Step 1.6: Manual Event Publishing Test

**Create a test script to publish directly to RabbitMQ:**
```bash
cat > publish-test-event.sh << 'EOF'
#!/bin/bash

# Install rabbitmqadmin if not present
if ! command -v rabbitmqadmin &> /dev/null; then
    curl -o rabbitmqadmin http://localhost:15672/cli/rabbitmqadmin
    chmod +x rabbitmqadmin
fi

# Publish a test event
./rabbitmqadmin publish \
  exchange=amq.default \
  routing_key=scenario.events \
  properties='{"content_type":"application/json"}' \
  payload='{
    "eventId": "test-event-001",
    "eventType": "scenario.created",
    "aggregateId": "scenario-123",
    "timestamp": "'$(date -u +%Y-%m-%dT%H:%M:%S.000Z)'",
    "payload": {
      "scenarioId": "scenario-123",
      "name": "Test Scenario",
      "description": "Manual test event"
    }
  }'

echo "✅ Test event published to scenario.events queue"
echo "📊 Check webhook deliveries:"
echo "docker exec postgres psql -U postgres -d postgres -c 'SELECT * FROM webhook_deliveries ORDER BY created_at DESC LIMIT 5;'"
EOF

chmod +x publish-test-event.sh
./publish-test-event.sh
```

---

#### Step 1.7: Verification Checklist

```bash
# ✅ Check 1: Webhook deliveries created
docker exec postgres psql -U postgres -d postgres -c \
"SELECT id, webhook_id, event_type, status, created_at 
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 10;"

# ✅ Check 2: HTTP requests sent (check webhook service logs)
docker logs webhook-management-service --tail 50 | grep -i "delivery\|webhook\|http"

# ✅ Check 3: Mock webhook server received request
# (If running mock-webhook-server.js)
```

**Success Criteria:**
- ✅ Deliveries appear in `webhook_deliveries` table
- ✅ Status is `SUCCESS` or `PENDING`
- ✅ Logs show "Creating delivery for webhook"
- ✅ Mock server receives POST request

---

## 🌐 **Issue #2: GraphQL Mutations Not Working**

### Root Cause
Requests were sent to service ports (8081) instead of gateway (8080), and mutations may not trigger event publishing.

### Fix Steps

#### Step 2.1: Use Gateway for All GraphQL Requests

**WRONG (Direct to service):**
```bash
curl -X POST http://localhost:8081/graphql ...
```

**CORRECT (Through gateway):**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "query": "mutation CreateScenario($input: CreateScenarioInput!) { createScenario(input: $input) { id name createdAt } }",
    "variables": {
      "input": {
        "name": "Test Scenario via Gateway",
        "description": "Testing event publication",
        "trackId": "track-001"
      }
    }
  }'
```

---

#### Step 2.2: Get Valid Auth Token

```bash
# Login and get token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@sdv.com",
    "password": "admin123"
  }' | jq -r '.token'

# Save token for reuse
export AUTH_TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sdv.com","password":"admin123"}' | jq -r '.token')

echo "Token saved to \$AUTH_TOKEN"
```

---

#### Step 2.3: Create Complete Test Script

```bash
cat > test-graphql-mutations.sh << 'EOF'
#!/bin/bash

# Get auth token
echo "🔑 Getting auth token..."
AUTH_TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sdv.com","password":"admin123"}' | jq -r '.token')

if [ -z "$AUTH_TOKEN" ] || [ "$AUTH_TOKEN" = "null" ]; then
  echo "❌ Failed to get auth token"
  exit 1
fi

echo "✅ Token obtained: ${AUTH_TOKEN:0:20}..."

# Create scenario
echo -e "\n📝 Creating scenario..."
SCENARIO_RESPONSE=$(curl -s -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "query": "mutation { createScenario(input: { name: \"GraphQL Test Scenario\", description: \"Testing event flow\", trackId: \"track-001\" }) { id name createdAt } }"
  }')

echo "Response: $SCENARIO_RESPONSE"

SCENARIO_ID=$(echo $SCENARIO_RESPONSE | jq -r '.data.createScenario.id')
echo "✅ Scenario created: $SCENARIO_ID"

# Wait for event processing
sleep 2

# Check RabbitMQ
echo -e "\n📊 Checking RabbitMQ for events..."
curl -s -u admin:admin http://localhost:15672/api/queues/%2F/scenario.events | \
jq '{messages, messages_ready, message_stats}'

# Check webhook deliveries
echo -e "\n📬 Checking webhook deliveries..."
docker exec postgres psql -U postgres -d postgres -c \
"SELECT id, webhook_id, event_type, status, created_at 
FROM webhook_deliveries 
WHERE event_type = 'scenario.created' 
ORDER BY created_at DESC 
LIMIT 5;"

echo -e "\n✅ Test complete!"
EOF

chmod +x test-graphql-mutations.sh
./test-graphql-mutations.sh
```

---

#### Step 2.4: Verify Event Publishing in Mutation Handlers

**Check scenario service mutation code:**
```bash
# Find mutation handler
find ./scenario-management-service -name "*Resolver.java" -o -name "*Mutation*.java"

# Look for event publishing
grep -r "publishEvent\|eventPublisher\|DomainEvent" ./scenario-management-service/src --include="*.java" -A 3
```

**Expected Pattern:**
```java
@MutationMapping
public Scenario createScenario(@Argument CreateScenarioInput input) {
    Scenario scenario = scenarioService.create(input);
    
    // ✅ MUST HAVE: Event publishing
    DomainEvent event = DomainEvent.builder()
        .eventType("scenario.created")
        .aggregateId(scenario.getId())
        .payload(scenario)
        .build();
    eventPublisher.publish(event);
    
    return scenario;
}
```

**If missing:** Add event publishing to all mutation handlers.

---

## 🗑️ **Issue #3: Clean Up Dead Letter Queues**

### Root Cause
Old Jackson deserialization failures left messages in DLQs.

### Fix Steps

#### Step 3.1: Purge All DLQs

```bash
cat > purge-dlqs.sh << 'EOF'
#!/bin/bash

echo "🗑️  Purging dead letter queues..."

# List of DLQs to purge
DLQS=(
  "scenario.events.dlq"
  "track.events.dlq"
  "simulation.events.dlq"
)

for DLQ in "${DLQS[@]}"; do
  echo "Purging: $DLQ"
  
  # Get message count before
  BEFORE=$(curl -s -u admin:admin http://localhost:15672/api/queues/%2F/$DLQ | jq '.messages')
  echo "  Messages before: $BEFORE"
  
  # Purge queue
  curl -s -u admin:admin -X DELETE \
    "http://localhost:15672/api/queues/%2F/$DLQ/contents"
  
  # Verify purge
  sleep 1
  AFTER=$(curl -s -u admin:admin http://localhost:15672/api/queues/%2F/$DLQ | jq '.messages')
  echo "  Messages after: $AFTER"
  echo "  ✅ Purged $(($BEFORE - $AFTER)) messages"
  echo ""
done

echo "✅ All DLQs purged!"
EOF

chmod +x purge-dlqs.sh
./purge-dlqs.sh
```

---

#### Step 3.2: Verify DLQs Are Empty

```bash
# Check all DLQ message counts
curl -s -u admin:admin http://localhost:15672/api/queues/%2F | \
jq '.[] | select(.name | contains(".dlq")) | {name, messages}'
```

**Expected Output:**
```json
{"name": "scenario.events.dlq", "messages": 0}
{"name": "track.events.dlq", "messages": 0}
{"name": "simulation.events.dlq", "messages": 0}
```

---

## 🔴 **Issue #4: Fix Redis Connection**

### Root Cause
Redis container not running or gateway configured with wrong host.

### Fix Steps

#### Step 4.1: Verify Redis in Docker Compose

```bash
# Check if redis service exists in docker-compose.yml
grep -A 10 "redis:" docker-compose.yml
```

**Expected:**
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
  networks:
    - sdv-network
```

**If missing:** Add Redis service to docker-compose.yml.

---

#### Step 4.2: Start Redis Container

```bash
# If redis is in docker-compose.yml
docker-compose up -d redis

# Verify redis is running
docker ps | grep redis

# Test connection
docker exec -it $(docker ps -q -f name=redis) redis-cli ping
# Expected: PONG
```

---

#### Step 4.3: Update Gateway Configuration

```bash
# Check gateway environment variables
docker exec api-gateway env | grep REDIS

# Should see:
# REDIS_HOST=redis
# REDIS_PORT=6379
```

**If wrong:** Update docker-compose.yml:
```yaml
api-gateway:
  environment:
    - REDIS_HOST=redis  # NOT localhost
    - REDIS_PORT=6379
  depends_on:
    - redis
```

---

#### Step 4.4: Restart Gateway

```bash
# Restart gateway with new config
docker-compose restart api-gateway

# Check logs - should see Redis connection success
docker logs api-gateway --tail 50 | grep -i redis
```

**Success Pattern:**
```
✅ Connected to Redis at redis:6379
✅ Lettuce connection established
```

---

## 📊 **Complete End-to-End Verification**

### Run Full E2E Test

```bash
cat > test-full-e2e-flow.sh << 'EOF'
#!/bin/bash

echo "🚀 Starting Full E2E Test"
echo "=========================="

# 1. Start mock webhook server
echo -e "\n1️⃣  Starting mock webhook server..."
node mock-webhook-server.js &
MOCK_PID=$!
sleep 2

# 2. Purge DLQs
echo -e "\n2️⃣  Purging DLQs..."
./purge-dlqs.sh

# 3. Seed test webhook
echo -e "\n3️⃣  Seeding test webhook..."
./seed-test-webhook.sh

# 4. Get auth token
echo -e "\n4️⃣  Getting auth token..."
AUTH_TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sdv.com","password":"admin123"}' | jq -r '.token')
echo "✅ Token: ${AUTH_TOKEN:0:20}..."

# 5. Create scenario via GraphQL
echo -e "\n5️⃣  Creating scenario..."
RESPONSE=$(curl -s -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "query": "mutation { createScenario(input: { name: \"E2E Test Scenario\", description: \"Full flow test\", trackId: \"track-001\" }) { id name } }"
  }')
echo "Response: $RESPONSE"

# 6. Wait for event processing
echo -e "\n6️⃣  Waiting for event processing..."
sleep 3

# 7. Check RabbitMQ
echo -e "\n7️⃣  Checking RabbitMQ..."
curl -s -u admin:admin http://localhost:15672/api/queues/%2F/scenario.events | \
jq '{messages, consumers, message_stats}'

# 8. Check webhook deliveries
echo -e "\n8️⃣  Checking webhook deliveries..."
docker exec postgres psql -U postgres -d postgres -c \
"SELECT id, webhook_id, event_type, status, attempt_count, created_at 
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 5;"

# 9. Check webhook service logs
echo -e "\n9️⃣  Webhook service logs (last 20 lines)..."
docker logs webhook-management-service --tail 20

# 10. Summary
echo -e "\n🎯 E2E Test Summary"
echo "==================="
DELIVERY_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c \
"SELECT COUNT(*) FROM webhook_deliveries WHERE created_at > NOW() - INTERVAL '1 minute';")
echo "📬 Webhook deliveries created: $DELIVERY_COUNT"

if [ $DELIVERY_COUNT -gt 0 ]; then
  echo "✅ SUCCESS! E2E flow is working!"
else
  echo "❌ FAILED! No webhook deliveries created"
fi

# Cleanup
kill $MOCK_PID 2>/dev/null

echo -e "\n✅ Test complete!"
EOF

chmod +x test-full-e2e-flow.sh
./test-full-e2e-flow.sh
```

---

## 🎯 **Success Criteria Checklist**

### ✅ Issue #1: Webhook Deliveries
- [ ] Events published to RabbitMQ queues
- [ ] Event types match webhook subscriptions
- [ ] Webhook service consumes events
- [ ] Deliveries created in `webhook_deliveries` table
- [ ] HTTP POST requests sent to webhook URLs
- [ ] Mock server receives webhook payloads

### ✅ Issue #2: GraphQL Mutations
- [ ] Mutations go through gateway (port 8080)
- [ ] Valid auth token obtained
- [ ] Mutations return successful responses
- [ ] Events published after mutation execution
- [ ] RabbitMQ shows message flow

### ✅ Issue #3: DLQs Cleaned
- [ ] All DLQs show 0 messages
- [ ] No new messages entering DLQs
- [ ] Jackson serialization working correctly

### ✅ Issue #4: Redis Connected
- [ ] Redis container running
- [ ] Gateway connects successfully
- [ ] No Redis connection warnings in logs
- [ ] Rate limiting/caching operational

---

## 📝 **Execution Timeline**

**Total Estimated Time:** 45-60 minutes

| Task | Duration | Dependencies |
|------|----------|--------------|
| Issue #1 Steps 1-4 | 20 min | None |
| Issue #1 Steps 5-7 | 15 min | Steps 1-4 |
| Issue #2 | 10 min | Issue #1 complete |
| Issue #3 | 5 min | None (parallel) |
| Issue #4 | 10 min | None (parallel) |
| E2E Verification | 10 min | All issues resolved |

---

## 🆘 **Troubleshooting Guide**

### If webhook deliveries still not created:

1. **Check event type exact match:**
   ```bash
   # Compare publisher vs subscriber
   docker logs webhook-management-service | grep "event type"
   docker exec postgres psql -U postgres -d postgres -c "SELECT DISTINCT event_type FROM webhook_event_types;"
   ```

2. **Verify webhook active status:**
   ```bash
   docker exec postgres psql -U postgres -d postgres -c \
   "UPDATE webhooks SET active=true WHERE active=false RETURNING id, url;"
   ```

3. **Check consumer binding:**
   ```bash
   curl -s -u admin:admin http://localhost:15672/api/bindings/%2F | \
   jq '.[] | select(.source == "" and .destination_type == "queue")'
   ```

### If GraphQL mutations timeout:

1. **Check gateway health:**
   ```bash
   curl -s http://localhost:8080/actuator/health | jq '.'
   ```

2. **Verify service registration:**
   ```bash
   docker logs api-gateway | grep -i "service\|discovery"
   ```

3. **Test direct service endpoint:**
   ```bash
   curl -s http://localhost:8081/actuator/health
   ```

---

## 📚 **Reference Documents**

- [Quick E2E Test Guide](QUICK_E2E_TEST_GUIDE.md)
- [Event Flow Documentation](EVENT_FLOW_DOCUMENTATION.md)
- [RabbitMQ Fix Summary](RABBITMQ_FIX_SUMMARY.md)
- [Current Issues Analysis](CURRENT_ISSUES_ANALYSIS.md)

---

**Last Updated:** November 3, 2025
**Next Review:** After executing all fix steps
