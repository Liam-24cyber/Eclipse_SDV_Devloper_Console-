# RabbitMQ Queue Analysis - Current State

## What You're Seeing (Screenshot Analysis)

### ✅ **GOOD NEWS - Queues Are ConfigURED CorrectLY!**

You have **8 queues** set up, which is the **expected configuration** for the SDV Developer Console:

| Queue Name | Type | Features | Status | Messages | Purpose |
|------------|------|----------|--------|----------|---------|
| **scenario.events** | classic | D, TTL, DLX, DLK | ✅ running | 0 | Scenario-related events |
| **scenario.events.dlq** | classic | D | ✅ running | 0 | Dead letter queue for scenarios |
| **simulation.events** | classic | D, TTL, DLX, DLK | ✅ running | 0 | Simulation lifecycle events |
| **simulation.events.dlq** | classic | D | ✅ running | 0 | Dead letter queue for simulations |
| **track.events** | classic | D, TTL, DLX, DLK | ✅ running | 0 | Track-related events |
| **track.events.dlq** | classic | D | ✅ running | 0 | Dead letter queue for tracks |
| **webhook.events** | classic | D, TTL, DLX, DLK | ✅ running | 0 | Webhook delivery events |
| **webhook.events.dlq** | classic | D | ✅ running | 0 | Dead letter queue for webhooks |

### Queue Features Explained:
- **D** = Durable (survives RabbitMQ restart)
- **TTL** = Time To Live (messages expire after a set time)
- **DLX** = Dead Letter Exchange (failed messages go here)
- **DLK** = Dead Letter routing Key

## 🟡 Current Status: **NO MESSAGES** (0/0/0)

This means:
- ✅ **Queues are properly configured**
- 🟡 **No events have been published yet**
- 🟡 **No simulations have been run**

### Why Are All Message Counts Zero?

This is **EXPECTED** if:
1. No simulations have been started yet
2. No webhook events have been triggered
3. The system is idle (just installed/reset)

This is **NOT EXPECTED** if:
1. You've run simulations through the UI
2. You've created/updated scenarios or tracks
3. Webhooks are supposed to be firing

## 📊 What SHOULD Happen (Expected Flow)

### When You Run a Simulation:

1. **User Action**: Click "Run Simulation" in UI
   ```
   Input: scenarioId + trackId + configuration
   ```

2. **simulation.events Queue** receives:
   ```json
   {
     "eventType": "SIMULATION_STARTED",
     "simulationId": "uuid-here",
     "scenarioId": "scenario-uuid",
     "trackId": "track-uuid",
     "timestamp": "2025-11-03T10:00:00Z"
   }
   ```
   **Expected Count**: 1+ (depending on simulation lifecycle)

3. **webhook.events Queue** receives:
   ```json
   {
     "eventType": "WEBHOOK_TRIGGERED",
     "webhookUrl": "http://configured-webhook.com/endpoint",
     "payload": { "simulation": { ... } },
     "timestamp": "2025-11-03T10:00:01Z"
   }
   ```
   **Expected Count**: 1+ (for each webhook configured)

4. **scenario.events Queue** receives:
   ```json
   {
     "eventType": "SCENARIO_EXECUTED",
     "scenarioId": "scenario-uuid",
     "executionStatus": "RUNNING",
     "timestamp": "2025-11-03T10:00:02Z"
   }
   ```

5. **track.events Queue** receives:
   ```json
   {
     "eventType": "TRACK_IN_USE",
     "trackId": "track-uuid",
     "vehicleCount": 3,
     "timestamp": "2025-11-03T10:00:03Z"
   }
   ```

### Message Flow Timeline:
```
[User Clicks "Run Simulation"]
    ↓
[API Gateway receives request]
    ↓
[Message Queue Service publishes to RabbitMQ]
    ↓
[simulation.events] ← Message arrives here (Count: 1)
    ↓
[Webhook Service consumes message]
    ↓
[webhook.events] ← Webhook delivery event (Count: 1)
    ↓
[External Webhook Endpoint receives HTTP POST]
    ✓ Success!
```

## 🔍 How to Test if RabbitMQ is Working

### Test 1: Publish a Test Message via RabbitMQ UI

1. Click on **"scenario.events"** queue
2. Click **"Publish message"** section
3. Add payload:
   ```json
   {
     "eventType": "TEST_EVENT",
     "timestamp": "2025-11-03T10:00:00Z",
     "message": "Testing RabbitMQ"
   }
   ```
4. Click **"Publish message"**
5. You should see **Ready: 1** in the queue

### Test 2: Publish via Command Line

```bash
# Install amqp-tools if needed
# brew install rabbitmq-c  (on Mac)

# Publish a test message
docker exec -it rabbitmq rabbitmqadmin publish \
  exchange=amq.default \
  routing_key=scenario.events \
  payload='{"test":"message"}'

# Check queue depth
docker exec -it rabbitmq rabbitmqadmin list queues name messages
```

### Test 3: Check RabbitMQ Logs

```bash
# Check if RabbitMQ is receiving connections
docker logs rabbitmq --tail 50

# Look for:
# - "accepting AMQP connection" (good)
# - "connection <xxx> closed" (normal after publish)
# - "refused connection" (bad - authentication issue)
```

## 🔴 Dead Letter Queues (DLQ)

The `.dlq` queues are for **failed messages**:

- **scenario.events.dlq**: Failed scenario events
- **simulation.events.dlq**: Failed simulation events
- **track.events.dlq**: Failed track events
- **webhook.events.dlq**: Failed webhook deliveries

**Expected State**: All DLQs should have **0 messages** in a healthy system.

**If DLQs have messages**, it means:
- Webhook endpoints are unreachable
- Message processing failed
- Message format is invalid
- Consumer crashed during processing

---

## 🚨 CRITICAL ISSUES DISCOVERED

While testing RabbitMQ message flow, we discovered **TWO CRITICAL BLOCKING ISSUES**:

### Issue #1: Webhook Service Crash Loop 🔴
- **Status:** Service continuously restarting
- **Cause:** Missing database tables (`webhooks`, `webhook_deliveries`, etc.)
- **Impact:** Cannot start, no webhook notifications possible

### Issue #2: Jackson JSR310 Serialization Error 🔴
- **Status:** Message publishing completely blocked
- **Cause:** ObjectMapper not configured with JavaTimeModule
- **Impact:** Cannot publish any events to RabbitMQ

**📄 See detailed analysis:** `CRITICAL_ISSUES_SUMMARY.md`

---

## Summary of Current Issues

### ❌ What's NOT Working
1. **Webhook Service** - Crash loop due to missing DB tables
2. **Message Publishing** - Jackson serialization error
3. **Event Flow** - No messages can reach RabbitMQ
4. **Simulations** - Cannot send status updates
5. **Real-time Updates** - UI cannot receive events

### ✅ What IS Working
1. **RabbitMQ Server** - Running and healthy
2. **Queue Configuration** - All 8 queues properly set up
3. **Database Server** - Running (but missing webhook tables)
4. **Scenario Service** - Running
5. **Track Service** - Running
6. **Gateway** - Running
7. **UI** - Accessible

---
