# Quick E2E Test Guide

## 🚀 Fast Track: Test the Complete Workflow

### Option 1: Direct Event Test (Recommended - Works Now!)
```bash
./test-direct-event-flow.sh
```

**What it tests:**
- ✅ Event publishing to RabbitMQ
- ✅ Message routing and consumption
- ✅ Webhook service processing
- ✅ Database storage (when webhooks are registered)

**Expected Result:**
```
Score: 2/3 (66% - PARTIAL SUCCESS)
✅ Event Published to RabbitMQ: PASS
✅ Queue Consumed by Services: PASS  
⚠️  Database Updated: FAIL (no webhooks registered yet)
```

---

### Option 2: Full Simulation Workflow Test
```bash
./test-complete-simulation-workflow.sh
```

**Status:** ⚠️ Blocked by Jackson serialization issue  
**See:** `JACKSON_DATE_TIME_FIX.md` for fix

---

## 📋 Prerequisites

### 1. Start All Services
```bash
docker-compose up -d
```

### 2. Seed the Database (First Time Only)
```bash
./seed-database.sh
```

**Creates:**
- 16 test scenarios
- 13 test tracks

---

## 🧪 Testing Scenarios

### Scenario A: Test Core Messaging (No Setup Required)
```bash
# Just run this - everything else is ready!
./test-direct-event-flow.sh
```

### Scenario B: Test with Webhook Delivery
```bash
# 1. Register a webhook (use webhook.site for testing)
curl -X POST http://localhost:8084/api/v1/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Webhook",
    "url": "https://webhook.site/YOUR-UNIQUE-URL",
    "eventTypes": ["simulation.started"]
  }'

# 2. Run the test
./test-direct-event-flow.sh

# 3. Check webhook.site - you should see the delivery!
```

### Scenario C: Manual Event Publishing
```bash
# Publish an event directly
curl -X POST http://localhost:8083/api/v1/events/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "simulation.started",
    "payload": {
      "simulationId": "test-123",
      "name": "Manual Test",
      "status": "RUNNING"
    }
  }'

# Check RabbitMQ
open http://localhost:15672
# Login: admin/admin123
# Go to Queues → simulation.events
```

---

## 📊 Quick Status Check

### Check All Services
```bash
./show-urls.sh
```

### Check RabbitMQ Queues
```bash
# Via Management UI
open http://localhost:15672

# Via CLI
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[] | {name:.name, messages:.messages, consumers:.consumers}'
```

### Check Database
```bash
# Count scenarios
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"

# Count tracks
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM track;"

# Check webhooks
docker exec postgres psql -U postgres -d postgres -c "SELECT * FROM webhooks;"

# Check deliveries
docker exec postgres psql -U postgres -d postgres -c "SELECT * FROM webhook_deliveries ORDER BY created_at DESC LIMIT 5;"
```

---

## 🔍 Debugging

### View Service Logs
```bash
# Message Queue Service
docker logs message-queue-service --tail 50 -f

# Webhook Service
docker logs webhook-management-service --tail 50 -f

# Scenario Service
docker logs scenario-library-service --tail 50 -f
```

### Check Service Health
```bash
# Message Queue
curl http://localhost:8083/actuator/health

# Webhook Service
curl http://localhost:8084/actuator/health

# Scenario Service
curl http://localhost:8081/actuator/health
```

---

## 🎯 What Each Test Shows

### test-direct-event-flow.sh ✅
**Verifies:**
- Event publishing API works
- RabbitMQ receives and routes messages
- Webhook service consumes messages
- End-to-end message flow

**Output:**
```
✓ Event published successfully!
✓ Messages were consumed immediately (good!)
Score: 2/3
```

### test-complete-simulation-workflow.sh ⚠️
**Verifies:**
- All services are running
- Database is seeded
- GraphQL API works (currently blocked)
- Full simulation launch flow

**Current Status:** Blocked by Jackson serialization

---

## 🏆 Success Criteria

### Minimum (Currently Achieved!)
- [x] RabbitMQ operational
- [x] Services can publish events
- [x] Messages are consumed
- [x] Database is accessible

### Full Success
- [x] Everything above ✅
- [ ] GraphQL queries work (needs Jackson fix)
- [ ] Webhooks are delivered (needs registered webhooks)
- [ ] Simulation can be launched via UI

---

## 📚 Key Files

| File | Purpose |
|------|---------|
| `test-direct-event-flow.sh` | Core messaging test (works now!) |
| `test-complete-simulation-workflow.sh` | Full E2E test (needs fix) |
| `seed-database.sh` | Populate test data |
| `show-urls.sh` | Show all service URLs |
| `E2E_SIMULATION_TEST_RESULTS.md` | Detailed test results |

---

## 🆘 Common Issues

### "No test data available"
```bash
./seed-database.sh
```

### "Event published but no delivery"
```bash
# Register a webhook first!
curl -X POST http://localhost:8084/api/v1/webhooks \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","url":"https://webhook.site/your-url","eventTypes":["simulation.started"]}'
```

### "Services are down"
```bash
docker-compose up -d
# Wait 30 seconds for services to start
./show-urls.sh
```

---

## 🎉 Quick Win

**Want to see the workflow in action right now?**

```bash
# 1. Seed database (if not done)
./seed-database.sh

# 2. Run the test
./test-direct-event-flow.sh

# That's it! ✅
```

**You should see:**
- ✅ Event published (HTTP 202)
- ✅ Message consumed from queue
- 📊 Clear pass/fail for each step

---

**For detailed results, see: `E2E_SIMULATION_TEST_RESULTS.md`**
