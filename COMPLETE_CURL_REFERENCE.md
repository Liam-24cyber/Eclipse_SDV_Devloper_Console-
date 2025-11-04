# 🔥 COMPLETE CURL COMMAND REFERENCE

**E2E API Server:** http://localhost:9191  
**All Available Endpoints**

---

## 📋 SYSTEM & MONITORING

### **1. Health Check**
```bash
curl http://localhost:9191/health
```

### **2. System Status** (Shows all services, database counts, RabbitMQ)
```bash
curl http://localhost:9191/api/status | python3 -m json.tool
```

### **3. RabbitMQ Queue Status**
```bash
curl http://localhost:9191/api/rabbitmq/queues | python3 -m json.tool
```

---

## 🎬 COMPLETE E2E WORKFLOW

### **4. Run Complete E2E Workflow** ⭐ Most Important!
```bash
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "Test Scenario", "scenarioDescription": "E2E Test"}' \
  | python3 -m json.tool
```

**What it does:**
- Creates scenario
- Verifies in database
- Gets track
- Creates simulation
- Publishes event to RabbitMQ
- Waits for webhook processing
- Checks deliveries
- Returns complete summary

---

## 📝 SCENARIO OPERATIONS

### **5. Create Scenario**
```bash
curl -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Custom Scenario",
    "description": "Created via API",
    "type": "CAN"
  }' | python3 -m json.tool
```

### **6. Get All Scenarios from Database**
```bash
curl http://localhost:9191/api/database/scenarios?limit=10 | python3 -m json.tool
```

**With custom limit:**
```bash
curl http://localhost:9191/api/database/scenarios?limit=20 | python3 -m json.tool
```

---

## 🏁 TRACK OPERATIONS

### **7. Get All Tracks**
```bash
curl http://localhost:9191/api/tracks | python3 -m json.tool
```

---

## 🎮 SIMULATION OPERATIONS

### **8. Create Simulation**
```bash
curl -X POST http://localhost:9191/api/simulation/create \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "name": "My Simulation",
    "status": "PENDING"
  }' | python3 -m json.tool
```

---

## 📡 EVENT PUBLISHING

### **9. Publish SCENARIO_CREATED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SCENARIO_CREATED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "name": "Test Scenario",
      "description": "Event test",
      "status": "CREATED",
      "type": "CAN"
    }
  }' | python3 -m json.tool
```

### **10. Publish SCENARIO_UPDATED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SCENARIO_UPDATED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "name": "Updated Scenario",
      "description": "Updated via event",
      "status": "UPDATED"
    }
  }' | python3 -m json.tool
```

### **11. Publish SIMULATION_STARTED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SIMULATION_STARTED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "simulationId": "sim-123",
      "name": "Test Simulation",
      "status": "STARTED"
    }
  }' | python3 -m json.tool
```

### **12. Publish SIMULATION_COMPLETED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SIMULATION_COMPLETED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "simulationId": "sim-123",
      "name": "Test Simulation",
      "status": "COMPLETED"
    }
  }' | python3 -m json.tool
```

### **13. Publish TRACK_CREATED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "TRACK_CREATED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "trackId": "track-123",
      "name": "New Track",
      "status": "CREATED"
    }
  }' | python3 -m json.tool
```

### **14. Publish TRACK_UPDATED Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "TRACK_UPDATED",
    "scenarioId": "YOUR-SCENARIO-ID-HERE",
    "data": {
      "trackId": "track-123",
      "name": "Updated Track",
      "status": "UPDATED"
    }
  }' | python3 -m json.tool
```

---

## 🔔 WEBHOOK OPERATIONS

### **15. Get All Webhook Deliveries**
```bash
curl http://localhost:9191/api/webhooks/deliveries | python3 -m json.tool
```

### **16. Get Limited Webhook Deliveries**
```bash
curl http://localhost:9191/api/webhooks/deliveries?limit=5 | python3 -m json.tool
```

### **17. Filter Webhook Deliveries by Event Type**
```bash
curl http://localhost:9191/api/webhooks/deliveries?eventType=SCENARIO_CREATED | python3 -m json.tool
```

```bash
curl http://localhost:9191/api/webhooks/deliveries?eventType=SCENARIO_UPDATED | python3 -m json.tool
```

```bash
curl http://localhost:9191/api/webhooks/deliveries?eventType=SIMULATION_STARTED | python3 -m json.tool
```

### **18. Limit + Filter Combined**
```bash
curl http://localhost:9191/api/webhooks/deliveries?limit=3&eventType=SCENARIO_CREATED | python3 -m json.tool
```

---

## 🎯 COMPLETE TEST SUITE

### **Run All Tests in Sequence:**

```bash
#!/bin/bash
echo "=== 1. Health Check ==="
curl http://localhost:9191/health
echo -e "\n"

echo "=== 2. System Status ==="
curl http://localhost:9191/api/status | python3 -m json.tool
echo -e "\n"

echo "=== 3. Get Tracks ==="
curl http://localhost:9191/api/tracks | python3 -m json.tool
echo -e "\n"

echo "=== 4. Get Scenarios ==="
curl http://localhost:9191/api/database/scenarios?limit=5 | python3 -m json.tool
echo -e "\n"

echo "=== 5. Create Scenario ==="
curl -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Scenario", "description": "API Test"}' \
  | python3 -m json.tool
echo -e "\n"

echo "=== 6. Run Complete E2E Workflow ==="
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "E2E Test"}' \
  | python3 -m json.tool
echo -e "\n"

echo "=== 7. Check Webhook Deliveries ==="
curl http://localhost:9191/api/webhooks/deliveries?limit=10 | python3 -m json.tool
echo -e "\n"

echo "=== 8. Check RabbitMQ Queues ==="
curl http://localhost:9191/api/rabbitmq/queues | python3 -m json.tool
echo -e "\n"
```

---

## 🔥 QUICK REFERENCE TABLE

| # | Endpoint | Method | Purpose |
|---|----------|--------|---------|
| 1 | `/health` | GET | Health check |
| 2 | `/api/status` | GET | System status |
| 3 | `/api/rabbitmq/queues` | GET | RabbitMQ queue status |
| 4 | `/api/e2e/run` | POST | ⭐ Complete E2E workflow |
| 5 | `/api/scenario/create` | POST | Create scenario |
| 6 | `/api/database/scenarios` | GET | Get scenarios |
| 7 | `/api/tracks` | GET | Get tracks |
| 8 | `/api/simulation/create` | POST | Create simulation |
| 9-14 | `/api/event/publish` | POST | Publish events (6 types) |
| 15 | `/api/webhooks/deliveries` | GET | Get all deliveries |
| 16 | `/api/webhooks/deliveries?limit=N` | GET | Get limited deliveries |
| 17 | `/api/webhooks/deliveries?eventType=X` | GET | Filter by event type |
| 18 | `/api/webhooks/deliveries?limit=N&eventType=X` | GET | Limit + filter |

---

## 💡 PRACTICAL EXAMPLES

### **Example 1: Create Scenario and Verify**
```bash
# Create
RESPONSE=$(curl -s -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Test", "description": "Demo"}')

echo $RESPONSE | python3 -m json.tool

# Verify in database
curl http://localhost:9191/api/database/scenarios?limit=5 | python3 -m json.tool
```

### **Example 2: Publish Event and Check Delivery**
```bash
# Publish event
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SCENARIO_CREATED",
    "scenarioId": "test-123",
    "data": {"name": "Test"}
  }' | python3 -m json.tool

# Wait for processing
sleep 3

# Check deliveries
curl http://localhost:9191/api/webhooks/deliveries?limit=5 | python3 -m json.tool
```

### **Example 3: Run E2E 5 Times**
```bash
for i in {1..5}; do
  echo "=== Run $i ==="
  curl -s -X POST http://localhost:9191/api/e2e/run \
    -H "Content-Type: application/json" \
    -d "{\"scenarioName\": \"Test Run $i\"}" \
    | python3 -m json.tool
  echo -e "\n"
  sleep 2
done
```

---

## 📊 TOTAL AVAILABLE ENDPOINTS

**Count:** 18+ endpoints (including variations)

**Categories:**
- 🔧 **System:** 3 endpoints (health, status, queues)
- 🎬 **E2E Workflow:** 1 endpoint (complete flow)
- 📝 **Scenarios:** 2 endpoints (create, list)
- 🏁 **Tracks:** 1 endpoint (list)
- 🎮 **Simulations:** 1 endpoint (create)
- 📡 **Events:** 6 event types (publish)
- 🔔 **Webhooks:** 4+ variations (list, filter, limit)

---

## 🎯 RECOMMENDED WORKFLOW

```bash
# 1. Check health
curl http://localhost:9191/health

# 2. Check system status
curl http://localhost:9191/api/status | python3 -m json.tool

# 3. Run complete E2E (creates everything + verifies)
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "My Test"}' | python3 -m json.tool

# 4. Check results
curl http://localhost:9191/api/webhooks/deliveries?limit=5 | python3 -m json.tool
```

---

**Total Commands:** 18+  
**Most Important:** `/api/e2e/run` (does everything!)  
**For Monitoring:** `/api/status` + `/api/webhooks/deliveries`
