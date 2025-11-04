# 🔍 Current Issues Analysis - SDV Developer Console

## Date: November 4, 2025, 02:30 UTC

---

## ✅ **What's Working**

| Component | Status | Evidence |
|-----------|--------|----------|
| **All Services Running** | ✅ WORKING | 5/5 services healthy |
| **RabbitMQ Infrastructure** | ✅ WORKING | All queues created, consumers connected |
| **Jackson DateTime Serialization** | ✅ FIXED | No more deserialization errors |
| **GraphQL Queries** | ✅ WORKING | 100% success rate (11/11 queries) |
| **Database** | ✅ WORKING | 16 scenarios, 13 tracks seeded |
| **Webhook Service Startup** | ✅ WORKING | No errors, consuming from queues |
| **RabbitMQ Message Consumption** | ✅ WORKING | 0 messages in queues = being consumed |

---

## ⚠️ **Issues Identified**

### Issue #1: No Webhook Deliveries Being Created ⚠️ **MAIN PROBLEM**

**Symptom:**
- Webhook service is consuming events from RabbitMQ
- 2 webhooks are registered in database
- **BUT: 0 webhook deliveries created**
- No HTTP requests being sent to webhook endpoints

**Evidence:**
```sql
-- Registered webhooks
SELECT * FROM webhooks;
-- Result: 2 rows (active webhooks exist)

-- Webhook deliveries
SELECT COUNT(*) FROM webhook_deliveries;
-- Result: 0 (NO deliveries created!)
```

**What This Means:**
The webhook service is consuming domain events (scenario.events, track.events, simulation.events) but the `WebhookEventConsumer` is not triggering the webhook delivery logic.

**Possible Root Causes:**
1. ❓ Event types don't match webhook subscriptions
2. ❓ WebhookEventConsumer logic not executing delivery creation
3. ❓ Events being consumed but not processed correctly
4. ❓ No events being published to the queues

---

### Issue #2: Cannot Trigger Events via GraphQL ⚠️

**Symptom:**
- GraphQL mutations don't return responses
- No events published to RabbitMQ when creating scenarios/simulations

**Commands Tried:**
```bash
# Create scenario mutation
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { createScenario(...) { id } }"}'

# Result: No output, no event published
```

**Possible Root Causes:**
1. ❓ Gateway Redis connection issues (seen in logs)
2. ❓ GraphQL mutations not implemented
3. ❓ Event publishing not triggered on create operations
4. ❓ Need to use different endpoint or method

---

### Issue #3: Dead Letter Queue Messages 📋 **INFO ONLY**

**Symptom:**
- `simulation.events.dlq` has 7 messages
- `scenario.events.dlq` has 2 messages

**What This Means:**
These are messages from BEFORE the Jackson fix that failed to deserialize. They're stuck in DLQ from earlier errors.

**Action Needed:**
- ✅ These can be ignored (old failed messages)
- 🔄 Optionally: Purge DLQ to clean up
- ✅ New events should NOT go to DLQ (Jackson is fixed)

---

### Issue #4: Gateway Redis Connection Warnings 📋 **INFO ONLY**

**Symptom:**
```
Unable to connect to Redis; nested exception is 
io.lettuce.core.RedisConnectionException: 
Unable to connect to localhost:6379
```

**Impact:**
- May affect GraphQL query performance
- Possibly affecting mutations
- Caching not working

**Action Needed:**
- 🔄 Redis not running or not accessible
- 🔄 Check if Redis is required for core functionality
- 🔄 Start Redis or update configuration

---

## 🎯 **Critical Path to Fix**

### Priority 1: Verify Event Publishing Works
**Goal:** Confirm events are being published to RabbitMQ when actions occur

**Action Items:**
1. ✅ Check RabbitMQ management UI for message flow
2. 🔄 Manually publish a test message to `scenario.events` queue
3. 🔄 Verify webhook service consumes and processes it
4. 🔄 Check if webhook delivery is created

**How to Test:**
```bash
# Check RabbitMQ queue stats
curl -s -u admin:admin http://localhost:15672/api/queues | jq '.[] | {name, messages, consumers}'

# Check webhook service logs
docker logs webhook-management-service --tail 50 --since 5m

# Check webhook deliveries
docker exec postgres psql -U postgres -d postgres \
  -c "SELECT * FROM webhook_deliveries ORDER BY created_at DESC LIMIT 5;"
```

---

### Priority 2: Understand Webhook Event Matching
**Goal:** Verify webhook subscriptions match the events being published

**Action Items:**
1. 🔄 Check what event types the registered webhooks are subscribed to
2. 🔄 Verify event types being published match webhook subscriptions
3. 🔄 Review `WebhookEventConsumer` logic for event filtering

**How to Check:**
```sql
-- Check webhook subscriptions
SELECT w.id, w.url, wet.event_type 
FROM webhooks w 
JOIN webhook_event_types wet ON w.id = wet.webhook_id 
WHERE w.active = true;

-- Expected event types that should match:
-- scenario.*, track.*, simulation.*
```

---

### Priority 3: Test Webhook Delivery Creation Directly
**Goal:** Verify the webhook service CAN create deliveries when it receives proper events

**Action Items:**
1. 🔄 Review `WebhookEventConsumer.java` code
2. 🔄 Check `WebhookService.java` for delivery creation logic
3. 🔄 Add debug logging to see if events are being processed
4. 🔄 Manually trigger webhook delivery if possible

---

## 🔬 **Diagnostic Commands**

### Check RabbitMQ Message Flow:
```bash
# List all queues with stats
curl -s -u admin:admin http://localhost:15672/api/queues/%2F | jq '.[] | {name, messages, messages_ready, consumers}'

# Check specific queue details
curl -s -u admin:admin http://localhost:15672/api/queues/%2F/scenario.events | jq '.'
```

### Check Webhook Service:
```bash
# Check recent logs
docker logs webhook-management-service --tail 100 --since 10m

# Check if service is processing messages
docker logs webhook-management-service 2>&1 | grep -i "consumed\|event\|webhook\|delivery"

# Restart with verbose logging if needed
docker-compose restart webhook-management-service
```

### Check Database State:
```bash
# Check webhooks
docker exec postgres psql -U postgres -d postgres \
  -c "SELECT * FROM webhooks WHERE active = true;"

# Check webhook event type subscriptions
docker exec postgres psql -U postgres -d postgres \
  -c "SELECT w.url, wet.event_type FROM webhooks w JOIN webhook_event_types wet ON w.id = wet.webhook_id WHERE w.active = true;"

# Check webhook deliveries
docker exec postgres psql -U postgres -d postgres \
  -c "SELECT COUNT(*) as total_deliveries FROM webhook_deliveries;"
```

### Manually Publish Test Event:
```bash
# Publish to scenario.events queue directly
# (Would need RabbitMQ admin tools or REST API)
```

---

## 📊 **Current Architecture Understanding**

```
┌─────────────────┐
│   UI / Client   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   API Gateway   │ ← ⚠️ Redis connection issue
└────────┬────────┘
         │
         ▼
┌──────────────────────┐
│ Scenario/Track Svc   │
└────────┬─────────────┘
         │
         ▼
┌──────────────────────┐
│ Message Queue Svc    │ ← ✅ Publishes to RabbitMQ
└────────┬─────────────┘
         │
         ▼
┌──────────────────────┐
│     RabbitMQ         │ ← ✅ Queues exist, routing works
└────────┬─────────────┘
         │
         ├─→ scenario.events ──┐
         ├─→ track.events ─────┼─→ ┌────────────────────┐
         └─→ simulation.events ┘   │  Webhook Service   │ ← ⚠️ Consuming but not creating deliveries
                                    └─────────┬──────────┘
                                              │
                                              ▼
                                    ┌─────────────────────┐
                                    │ Webhook Deliveries  │ ← ❌ 0 deliveries!
                                    └─────────────────────┘
```

---

## ❓ **Questions to Answer**

1. **Are events being published to RabbitMQ when scenarios/simulations are created?**
   - 🔄 Need to verify message publishing
   - 🔄 Check message queue service logs

2. **What event types are the registered webhooks subscribed to?**
   - 🔄 Query `webhook_event_types` table
   - 🔄 Verify they match `scenario.*`, `track.*`, `simulation.*`

3. **Is the WebhookEventConsumer processing events correctly?**
   - 🔄 Review Java code logic
   - 🔄 Add debug logging
   - 🔄 Check for exceptions

4. **Do we need to trigger events differently?**
   - 🔄 Maybe GraphQL mutations aren't the right way
   - 🔄 Check for REST APIs
   - 🔄 Review API documentation

---

## 🎯 **Immediate Next Steps**

### Step 1: Check Webhook Event Type Subscriptions
```bash
docker exec postgres psql -U postgres -d postgres \
  -c "SELECT w.id, w.url, w.active, wet.event_type FROM webhooks w LEFT JOIN webhook_event_types wet ON w.id = wet.webhook_id;"
```

### Step 2: Review WebhookEventConsumer Code
- Check the event processing logic
- Verify event type matching
- Look for delivery creation code

### Step 3: Enable Debug Logging
- Increase log level for webhook service
- Monitor logs while triggering events

### Step 4: Manually Test Event Processing
- Publish a test message to RabbitMQ queue
- Verify webhook service processes it
- Check if delivery is created

---

## 📝 **Summary**

**Main Issue:** Webhook service is consuming events but NOT creating webhook deliveries.

**Root Cause:** Unknown - needs investigation into:
1. Event type matching between published events and webhook subscriptions
2. WebhookEventConsumer logic for delivery creation
3. Whether events are actually being published

**Recommended Action:** Focus on Step 1-4 above to diagnose the delivery creation failure.

---

**Last Updated:** November 4, 2025, 02:30 UTC
