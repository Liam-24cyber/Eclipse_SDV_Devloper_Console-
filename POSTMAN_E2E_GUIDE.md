# 🎯 E2E DEMO API - POSTMAN GUIDE

**Purpose:** Trigger complete E2E workflow from Postman  
**Server:** Node.js REST API on port 9191  
**Date:** November 4, 2025

---

## 🚀 QUICK START (3 STEPS)

### **Step 1: Start All Services**
```bash
./start-all-services.sh
```
Wait until all services are running (check with `docker ps`)

### **Step 2: Start E2E API Server**
```bash
./start-e2e-api.sh
```
Server will run on: **http://localhost:9191**

### **Step 3: Import Postman Collection**
1. Open Postman
2. Click **Import**
3. Select file: `E2E_Demo_API.postman_collection.json`
4. Done! ✅

---

## 🎬 ONE-CLICK E2E WORKFLOW

### **Easiest Way: Run Complete Workflow**

**Endpoint:** `POST /api/e2e/run`

**In Postman:**
1. Open collection: **SDV E2E Demo API**
2. Open folder: **E2E Workflow**
3. Click: **🎬 RUN COMPLETE E2E WORKFLOW**
4. Click: **Send**

**What it does:**
- ✅ Creates scenario in database
- ✅ Verifies persistence
- ✅ Fetches track
- ✅ Creates simulation
- ✅ Publishes event to RabbitMQ
- ✅ Waits for webhook processing (3 seconds)
- ✅ Checks webhook deliveries
- ✅ Returns complete summary

**Expected Response:**
```json
{
  "success": true,
  "message": "E2E workflow completed successfully",
  "timestamp": "2025-11-04T14:30:25.000Z",
  "results": {
    "step1_scenario": {
      "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
      "name": "Postman E2E Test 1699112425",
      "description": "End-to-end workflow triggered from Postman",
      "status": "CREATED"
    },
    "step2_verification": {
      "found": true
    },
    "step3_track": {
      "trackId": "660e8400-e29b-41d4-a716-446655440001",
      "trackName": "Downtown City Circuit"
    },
    "step4_simulation": {
      "simulationId": "sim-1699112425",
      "name": "E2E Demo Simulation 14:30:25",
      "status": "PENDING"
    },
    "step5_event": {
      "published": true,
      "eventId": "event-1699112425",
      "eventType": "SCENARIO_CREATED"
    },
    "step7_webhooks": {
      "deliveries": 1,
      "success": true
    },
    "summary": {
      "totalScenarios": 17,
      "totalTracks": 13,
      "activeWebhooks": 1,
      "totalDeliveries": 25
    }
  }
}
```

---

## 📋 ALL AVAILABLE ENDPOINTS

### **System Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Health check |
| `GET` | `/api/status` | Complete system status |

### **Workflow Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/e2e/run` | **Run complete E2E workflow** |
| `POST` | `/api/scenario/create` | Create scenario |
| `GET` | `/api/tracks` | Get available tracks |
| `POST` | `/api/simulation/create` | Create simulation |
| `POST` | `/api/event/publish` | Publish event to RabbitMQ |
| `GET` | `/api/webhooks/deliveries` | Get webhook delivery history |
| `GET` | `/api/database/scenarios` | Get scenarios from DB |
| `GET` | `/api/rabbitmq/queues` | RabbitMQ queue status |

---

## 🔥 POSTMAN COLLECTION STRUCTURE

```
SDV E2E Demo API/
├── System/
│   ├── Health Check
│   └── System Status
│
├── E2E Workflow/
│   └── 🎬 RUN COMPLETE E2E WORKFLOW ⭐ START HERE!
│
├── Step 1 - Create Scenario/
│   └── Create Scenario
│
├── Step 2 - Get Tracks/
│   └── Get All Tracks
│
├── Step 3 - Create Simulation/
│   └── Create Simulation
│
├── Step 4 - Publish Event/
│   ├── Publish SCENARIO_CREATED Event
│   ├── Publish SCENARIO_UPDATED Event
│   └── Publish SIMULATION_STARTED Event
│
├── Step 5 - Check Results/
│   ├── Get Webhook Deliveries
│   ├── Get Scenarios from Database
│   └── Get RabbitMQ Queue Status
│
└── Direct GraphQL Calls/
    ├── Create Scenario via GraphQL
    └── Query All Scenarios
```

---

## 🎯 USAGE EXAMPLES

### **Example 1: Check System Status**

**Request:**
```http
GET http://localhost:9191/api/status
```

**Response:**
```json
{
  "success": true,
  "timestamp": "2025-11-04T14:30:25.000Z",
  "services": {
    "postgres": "running",
    "rabbitmq": "running",
    "dco-gateway": "running",
    "webhook-management-service": "running",
    "scenario-library-service": "running"
  },
  "database": {
    "scenarios": 16,
    "tracks": 13,
    "webhooks": 1,
    "deliveries": 25
  },
  "rabbitmq": {
    "queue": "scenario.events",
    "messages": 0
  }
}
```

### **Example 2: Create Scenario**

**Request:**
```http
POST http://localhost:9191/api/scenario/create
Content-Type: application/json

{
  "name": "My Custom Scenario",
  "description": "Created from Postman",
  "type": "CAN"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Scenario created successfully",
  "data": {
    "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
    "name": "My Custom Scenario",
    "description": "Created from Postman",
    "type": "CAN",
    "status": "CREATED"
  }
}
```

### **Example 3: Publish Event**

**Request:**
```http
POST http://localhost:9191/api/event/publish
Content-Type: application/json

{
  "eventType": "SCENARIO_CREATED",
  "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
  "data": {
    "name": "My Scenario",
    "description": "Published from Postman",
    "status": "CREATED"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Event published to RabbitMQ",
  "event": {
    "eventId": "event-1699112425",
    "eventType": "SCENARIO_CREATED",
    "timestamp": "2025-11-04T14:30:25.000Z",
    "source": "e2e-api-server",
    "data": {
      "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
      "name": "My Scenario",
      "description": "Published from Postman",
      "status": "CREATED"
    }
  },
  "rabbitmq": {
    "exchange": "sdv.events",
    "routingKey": "scenario.created",
    "queueMessages": 0
  }
}
```

### **Example 4: Check Webhook Deliveries**

**Request:**
```http
GET http://localhost:9191/api/webhooks/deliveries?limit=5
```

**Response:**
```json
{
  "success": true,
  "total": 25,
  "showing": 5,
  "data": [
    {
      "id": "123",
      "webhook_id": "c318032a-ab64-4457-bf55-ca5a2330fe63",
      "event_type": "SCENARIO_CREATED",
      "status": "SUCCESS",
      "response_status_code": "200",
      "created_at": "2025-11-04 14:30:25"
    }
  ]
}
```

---

## 🎬 STEP-BY-STEP MANUAL WORKFLOW

If you want to run each step individually:

### **1. Check System Status**
```
GET /api/status
```

### **2. Create Scenario**
```
POST /api/scenario/create
Body: { "name": "Test", "description": "Demo", "type": "CAN" }
```
**Save the `scenarioId` from response!**

### **3. Get Tracks**
```
GET /api/tracks
```
**Save a `trackId` from response!**

### **4. Create Simulation**
```
POST /api/simulation/create
Body: { "scenarioId": "<saved-id>", "name": "Test Sim" }
```

### **5. Publish Event**
```
POST /api/event/publish
Body: { 
  "eventType": "SCENARIO_CREATED", 
  "scenarioId": "<saved-id>",
  "data": { "name": "Test", "status": "CREATED" }
}
```

### **6. Wait 3 seconds**
(For webhook processing)

### **7. Check Webhook Deliveries**
```
GET /api/webhooks/deliveries?limit=10
```

---

## 🔧 CURL EXAMPLES

### **Complete E2E Workflow:**
```bash
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "Curl Test",
    "scenarioDescription": "E2E from curl"
  }' | jq
```

### **Create Scenario:**
```bash
curl -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Curl Scenario",
    "description": "Created via curl",
    "type": "CAN"
  }' | jq
```

### **Get System Status:**
```bash
curl http://localhost:9191/api/status | jq
```

### **Get Webhook Deliveries:**
```bash
curl http://localhost:9191/api/webhooks/deliveries?limit=10 | jq
```

### **Publish Event:**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "SCENARIO_CREATED",
    "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
    "data": {
      "name": "Test",
      "status": "CREATED"
    }
  }' | jq
```

---

## 🎥 DEMO WORKFLOW FOR VIDEO

### **Perfect for Screen Recording:**

1. **Start services** (in terminal 1):
   ```bash
   ./start-all-services.sh
   ```

2. **Start E2E API** (in terminal 2):
   ```bash
   ./start-e2e-api.sh
   ```

3. **Open Postman** (show on screen)
   - Import collection
   - Navigate to: **E2E Workflow** → **🎬 RUN COMPLETE E2E WORKFLOW**

4. **Click Send** (show request/response)

5. **Show results** in different tabs:
   - pgAdmin: `SELECT * FROM scenario ORDER BY created_at DESC LIMIT 5;`
   - RabbitMQ: http://localhost:15672/#/queues
   - UI: http://localhost:3000/scenarios

6. **Run again** (show multiple scenarios created)

---

## 🆘 TROUBLESHOOTING

### **API Server won't start:**
```bash
# Install Node.js dependencies
npm install express

# Try starting manually
node e2e-api-server.js
```

### **Connection refused errors:**
```bash
# Check if services are running
docker ps

# Restart services
./start-all-services.sh
```

### **No webhook deliveries:**
```bash
# Check webhook configuration
docker exec postgres psql -U postgres -d postgres -c "SELECT * FROM webhooks WHERE is_active=true;"

# Check webhook service logs
docker logs webhook-management-service --tail 50
```

### **Port 9191 already in use:**
```bash
# Find what's using port 9191
lsof -i :9191

# Kill the process
kill -9 <PID>

# Or change port in e2e-api-server.js
```

---

## 📊 MONITORING & VERIFICATION

### **Check API Server Health:**
```bash
curl http://localhost:9191/health
```

### **Watch API Logs:**
```bash
# Terminal output shows all API calls in real-time
```

### **Check Database:**
```bash
docker exec postgres psql -U postgres -d postgres -c "
SELECT 
  (SELECT COUNT(*) FROM scenario) as scenarios,
  (SELECT COUNT(*) FROM track) as tracks,
  (SELECT COUNT(*) FROM webhooks WHERE is_active=true) as webhooks,
  (SELECT COUNT(*) FROM webhook_deliveries) as deliveries;
"
```

### **Check RabbitMQ:**
```bash
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[] | {name: .name, messages: .messages, consumers: .consumers}'
```

---

## 🎯 BENEFITS

### ✅ **Postman Integration**
- Visual interface
- Save requests
- Create test suites
- Share with team

### ✅ **Repeatable**
- Run same workflow multiple times
- Consistent results
- No manual steps

### ✅ **Flexible**
- Run complete E2E or individual steps
- Customize payloads
- Test different scenarios

### ✅ **Professional**
- REST API standard
- JSON responses
- Error handling
- Documentation

---

## 📁 FILES CREATED

```
Eclipse_SDV_Devloper_Console-/
├── e2e-api-server.js                     # Node.js API server
├── start-e2e-api.sh                      # Startup script
├── E2E_Demo_API.postman_collection.json  # Postman collection
└── POSTMAN_E2E_GUIDE.md                  # This guide
```

---

## 🚀 COMPLETE SETUP COMMANDS

```bash
# 1. Start all services
./start-all-services.sh

# 2. Start E2E API server (in new terminal)
./start-e2e-api.sh

# 3. Test API
curl http://localhost:9191/health

# 4. Run complete E2E
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "Test"}' | jq

# 5. Check results
curl http://localhost:9191/api/status | jq
```

---

## 🎉 SUMMARY

| What | Where | Port |
|------|-------|------|
| **E2E API Server** | http://localhost:9191 | 9191 |
| **GraphQL Gateway** | http://localhost:8080/graphql | 8080 |
| **Frontend UI** | http://localhost:3000 | 3000 |
| **RabbitMQ UI** | http://localhost:15672 | 15672 |
| **pgAdmin** | http://localhost:5050 | 5050 |

### **One Command to Rule Them All:**
```bash
curl -X POST http://localhost:9191/api/e2e/run | jq
```

**Perfect for:**
- ✅ Live demos
- ✅ API testing
- ✅ Integration testing
- ✅ Video recordings
- ✅ Team collaboration

---

**Created:** November 4, 2025  
**Purpose:** REST API endpoints for E2E workflow automation  
**Impact:** Trigger complete workflows from Postman with one click! 🎊
