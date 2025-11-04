# ✅ POSTMAN E2E API - COMPLETE SETUP

## 🎉 SUCCESS! Your E2E API is Ready!

**API Server:** http://localhost:9191  
**Status:** ✅ Running  
**Postman Collection:** Ready to import

---

## 🚀 HOW TO USE (SIMPLE 3-STEP PROCESS)

### **Step 1: Start All Services** (if not already running)
```bash
./start-all-services.sh
```

### **Step 2: Start E2E API Server**
```bash
./start-e2e-api.sh
```
Or manually:
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
node e2e-api-server.js
```

### **Step 3: Import Postman Collection**
1. Open Postman
2. Click **Import**
3. Select file: `E2E_Demo_API.postman_collection.json`
4. Start making requests!

---

## 🎬 RUN COMPLETE E2E WORKFLOW

### **Option 1: From Postman** (Easiest!)

1. Open collection: **SDV E2E Demo API**
2. Navigate to: **E2E Workflow** → **🎬 RUN COMPLETE E2E WORKFLOW**
3. Click: **Send**
4. View results!

### **Option 2: From cURL**

```bash
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "My Test Scenario",
    "scenarioDescription": "Testing E2E workflow"
  }' | python3 -m json.tool
```

### **Expected Response:**
```json
{
  "success": true,
  "message": "E2E workflow completed successfully",
  "timestamp": "2025-11-04T07:00:40.113Z",
  "results": {
    "step1_scenario": {
      "scenarioId": "550e8400-e29b-41d4-a716-446655440017",
      "name": "My Test Scenario",
      "description": "Testing E2E workflow",
      "status": "CREATED"
    },
    "step5_event": {
      "published": true,
      "eventId": "event-1762239636918",
      "eventType": "SCENARIO_CREATED"
    },
    "step7_webhooks": {
      "deliveries": 1,
      "success": true
    },
    "summary": {
      "totalScenarios": 19,
      "totalTracks": 13,
      "activeWebhooks": 1,
      "totalDeliveries": 2
    }
  }
}
```

---

## 📋 ALL AVAILABLE ENDPOINTS

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Health check ✅ |
| `GET` | `/api/status` | System status (services, database, queues) |
| `POST` | `/api/e2e/run` | **⭐ Run complete E2E workflow** |
| `POST` | `/api/scenario/create` | Create a new scenario |
| `GET` | `/api/tracks` | Get available tracks |
| `POST` | `/api/simulation/create` | Create a simulation |
| `POST` | `/api/event/publish` | Publish event to RabbitMQ |
| `GET` | `/api/webhooks/deliveries` | Get webhook deliveries |
| `GET` | `/api/database/scenarios` | Get scenarios from database |
| `GET` | `/api/rabbitmq/queues` | Get RabbitMQ queue status |

---

## 🔥 QUICK TEST COMMANDS

### **Test Health:**
```bash
curl http://localhost:9191/health
```

### **Test System Status:**
```bash
curl http://localhost:9191/api/status | python3 -m json.tool
```

### **Create Scenario:**
```bash
curl -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Quick Test", "description": "Created via API"}' \
  | python3 -m json.tool
```

### **Get Webhook Deliveries:**
```bash
curl http://localhost:9191/api/webhooks/deliveries?limit=5 | python3 -m json.tool
```

### **Get Scenarios:**
```bash
curl http://localhost:9191/api/database/scenarios?limit=5 | python3 -m json.tool
```

---

## ✅ WHY YOUR QUESTIONS ARE NOW ANSWERED

### **Q: RabbitMQ Queue Messages = 0, why?**
**A:** ✅ **This is CORRECT behavior!**
- Events are published to RabbitMQ
- Webhook service consumes them **instantly** (within milliseconds)
- Queue shows 0 because messages are already processed
- Check with: `GET /api/rabbitmq/queues` - you'll see consumers=1

### **Q: Total Webhook Deliveries = 0, why?**
**A:** ✅ **Now FIXED!**
- The issue was the script was publishing to wrong exchange
- E2E API now publishes to correct exchange: `sdv.events`
- Uses correct routing key: `scenario.created`
- Webhook service now receives events and creates deliveries
- Check with: `GET /api/webhooks/deliveries`

---

## 🎯 COMPLETE WORKFLOW EXPLANATION

When you run `POST /api/e2e/run`, here's what happens:

1. **Creates Scenario** → Inserts into PostgreSQL `scenario` table
2. **Verifies** → Queries database to confirm scenario exists
3. **Gets Track** → Fetches first available track from database
4. **Creates Simulation** → Generates simulation record
5. **Publishes Event** → Sends to RabbitMQ exchange `sdv.events` with routing key `scenario.created`
6. **RabbitMQ Routes** → Event goes to `scenario.events` queue
7. **Webhook Service Consumes** → Picks up event from queue (queue goes to 0)
8. **Webhook Service Processes** → Finds configured webhooks listening for `SCENARIO_CREATED`
9. **Webhook Delivery** → Attempts HTTP POST to webhook URL
10. **Records Delivery** → Inserts record into `webhook_deliveries` table
11. **Returns Summary** → API returns all results

---

## 🎥 PERFECT FOR DEMOS

### **Recording Setup:**
```bash
# Terminal 1: Services
./start-all-services.sh

# Terminal 2: E2E API
./start-e2e-api.sh

# Postman: Ready to trigger
```

### **Demo Flow:**
1. Show Postman collection
2. Click "Send" on E2E workflow request
3. Show response in Postman
4. Open pgAdmin, show new scenario
5. Open RabbitMQ UI, show queue consumers
6. Show webhook_deliveries table
7. Run again to show repeatability

---

## 📦 FILES CREATED

```
Eclipse_SDV_Devloper_Console-/
├── e2e-api-server.js                      # Node.js Express server (port 9191)
├── start-e2e-api.sh                       # Startup script
├── E2E_Demo_API.postman_collection.json   # Postman collection (IMPORT THIS!)
├── POSTMAN_E2E_GUIDE.md                   # Complete documentation
├── POSTMAN_QUICK_REF.md                   # Quick reference card
└── POSTMAN_SETUP_COMPLETE.md              # This file
```

---

## 🆘 TROUBLESHOOTING

### **API Server Won't Start:**
```bash
# Install dependencies
npm install express

# Check port
lsof -i :9191

# Start manually
node e2e-api-server.js
```

### **Connection Refused:**
```bash
# Check if services are running
docker ps

# Restart services
./start-all-services.sh
```

### **No Webhook Deliveries:**
```bash
# Check webhooks exist
curl http://localhost:9191/api/status | grep webhooks

# Check webhook service logs
docker logs webhook-management-service --tail 50

# Verify webhook configuration
docker exec postgres psql -U postgres -d postgres -c "SELECT * FROM webhooks WHERE is_active=true;"
```

---

## 🎉 SUMMARY

| What | Value |
|------|-------|
| **API Server** | http://localhost:9191 |
| **Status** | ✅ Running |
| **Health Check** | http://localhost:9191/health |
| **E2E Endpoint** | http://localhost:9191/api/e2e/run |
| **Postman Collection** | E2E_Demo_API.postman_collection.json |
| **Full Guide** | POSTMAN_E2E_GUIDE.md |
| **Quick Ref** | POSTMAN_QUICK_REF.md |

### **One Command to Test Everything:**
```bash
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "Quick Test"}' | python3 -m json.tool
```

---

## 🎊 YOU'RE ALL SET!

Your E2E API is fully operational and ready for:
- ✅ Postman testing
- ✅ Live demos
- ✅ Video recording
- ✅ Integration testing
- ✅ CI/CD pipelines
- ✅ Team collaboration

**Happy Testing!** 🚀

---

**Created:** November 4, 2025  
**Port:** 9191  
**Status:** ✅ Operational  
**Purpose:** REST API endpoints for complete E2E workflow automation
