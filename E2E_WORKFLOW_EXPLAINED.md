# 🔄 End-to-End Workflow Explanation

## Complete Data Flow: User → Database → RabbitMQ → Webhooks

This document explains **exactly what happens** at each step when you run a simulation in the SDV Developer Console.

---

## 📊 STEP-BY-STEP WORKFLOW

### **STEP 1: User Authentication**
```
INPUT:  Username: "developer", Password: "password"
OUTPUT: Basic Auth Header: "Basic ZGV2ZWxvcGVyOnBhc3N3b3Jk"
```

**What Happens:**
- User credentials are Base64 encoded
- Sent in HTTP `Authorization` header for all API requests
- Gateway validates against configured credentials

---

### **STEP 2: Fetch Available Scenarios**
```
INPUT:  GraphQL Query → searchScenarioByPattern(scenarioPattern: "", page: 0, size: 20)
OUTPUT: List of 16 scenarios from PostgreSQL database
```

**Request:**
```graphql
query {
  searchScenarioByPattern(scenarioPattern: "", page: 0, size: 20) {
    content {
      id
      name
      description
      type
      status
      file { path }
    }
    total
  }
}
```

**Response Example:**
```json
{
  "data": {
    "searchScenarioByPattern": {
      "content": [
        {
          "id": "93b866de-a642-4543-886c-a3597dbe9d8f",
          "name": "Urban Traffic Navigation",
          "description": "Navigate through busy city traffic...",
          "type": "CAN",
          "status": "CREATED",
          "file": {
            "path": "/scenarios/urban_traffic.txt"
          }
        }
        // ... 15 more scenarios
      ],
      "total": 16
    }
  }
}
```

**Database Query (Internal):**
```sql
SELECT * FROM scenario 
WHERE LOWER(name) LIKE LOWER('%') 
AND status = 'CREATED' 
LIMIT 20 OFFSET 0;
```

---

### **STEP 3: Fetch Available Tracks**
```
INPUT:  GraphQL Query → searchTrackByPattern(trackPattern: "", page: 0, size: 20)
OUTPUT: List of 13 tracks with vehicles from PostgreSQL database
```

**Request:**
```graphql
query {
  searchTrackByPattern(trackPattern: "", page: 0, size: 20) {
    content {
      id
      name
      state
      trackType
      vehicles {
        vin
        country
      }
    }
    total
  }
}
```

**Response Example:**
```json
{
  "data": {
    "searchTrackByPattern": {
      "content": [
        {
          "id": "660e8400-e29b-41d4-a716-446655440001",
          "name": "Downtown City Circuit",
          "state": "ACTIVE",
          "trackType": "URBAN",
          "vehicles": [
            { "vin": "VIN001", "country": "Germany" },
            { "vin": "VIN002", "country": "France" }
          ]
        }
        // ... 12 more tracks
      ],
      "total": 13
    }
  }
}
```

**Database Query (Internal):**
```sql
SELECT t.*, v.* FROM track t
LEFT JOIN track_vehicle tv ON t.id = tv.track_id
LEFT JOIN vehicle v ON tv.vehicle_id = v.id
WHERE t.state = 'ACTIVE'
LIMIT 20 OFFSET 0;
```

---

### **STEP 4: User Selects Scenario + Track and Starts Simulation**

```
INPUT:  
  - Scenario ID: "93b866de-a642-4543-886c-a3597dbe9d8f"
  - Track ID: "660e8400-e29b-41d4-a716-446655440001"
  
OUTPUT: Simulation created in database
```

**Request:**
```http
POST /api/simulation
Content-Type: application/json
Authorization: Basic ZGV2ZWxvcGVyOnBhc3N3b3Jk

{
  "scenarioId": "93b866de-a642-4543-886c-a3597dbe9d8f",
  "trackId": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Test Simulation",
  "description": "Running urban traffic scenario on city circuit"
}
```

**What Happens:**
1. **Database Insert** - New simulation record created:
   ```sql
   INSERT INTO simulation (id, scenario_id, track_id, name, description, status, created_at)
   VALUES (uuid(), '93b866de...', '660e8400...', 'Test Simulation', '...', 'RUNNING', NOW());
   ```

2. **Simulation Engine Starts** - Begins processing scenario steps

3. **Events Generated** - As simulation progresses, events are created

---

### **STEP 5: Events Sent to RabbitMQ**

```
INPUT:  Simulation events (vehicle data, sensor data, telemetry)
OUTPUT: Messages published to RabbitMQ exchange
```

**Example Event:**
```json
{
  "simulationId": "sim-12345",
  "timestamp": "2025-01-03T10:30:00Z",
  "eventType": "VEHICLE_POSITION",
  "data": {
    "vin": "VIN001",
    "latitude": 52.5200,
    "longitude": 13.4050,
    "speed": 45.5,
    "heading": 270
  }
}
```

**RabbitMQ Flow:**
```
Publisher (Simulation Engine)
    ↓
Exchange: "simulation-events"
    ↓
Routing Key: "vehicle.position"
    ↓
Queue: "webhook-events"
    ↓
Consumer (Webhook Service)
```

**Check RabbitMQ:**
```bash
# View queues
curl -u guest:guest http://localhost:15672/api/queues

# View messages in queue
curl -u guest:guest http://localhost:15672/api/queues/%2F/webhook-events/get \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"count":10,"ackmode":"ack_requeue_true","encoding":"auto"}'
```

---

### **STEP 6: Webhook Service Consumes Messages**

```
INPUT:  Messages from RabbitMQ queue "webhook-events"
OUTPUT: HTTP POST requests to registered webhook endpoints
```

**Webhook Service Process:**
1. **Listen** - Consumes messages from RabbitMQ queue
2. **Lookup** - Checks database for registered webhooks
   ```sql
   SELECT * FROM webhook WHERE active = true AND event_type = 'VEHICLE_POSITION';
   ```
3. **Transform** - Converts message to webhook format
4. **Send** - POSTs data to external URL

**Webhook HTTP Request:**
```http
POST https://external-system.com/webhook
Content-Type: application/json
X-Webhook-Signature: sha256=abc123...

{
  "event": "vehicle.position",
  "timestamp": "2025-01-03T10:30:00Z",
  "data": {
    "vin": "VIN001",
    "latitude": 52.5200,
    "longitude": 13.4050,
    "speed": 45.5
  }
}
```

**Database Log:**
```sql
INSERT INTO webhook_delivery (webhook_id, event_id, status, response_code, delivered_at)
VALUES ('webhook-123', 'event-456', 'SUCCESS', 200, NOW());
```

---

### **STEP 7: Monitor Webhook Deliveries**

```
INPUT:  Webhook delivery logs from database
OUTPUT: Success/failure statistics
```

**Query Webhook Status:**
```graphql
query {
  webhookDeliveries(simulationId: "sim-12345") {
    id
    webhookUrl
    eventType
    status
    responseCode
    deliveredAt
    retryCount
  }
}
```

**Example Response:**
```json
{
  "data": {
    "webhookDeliveries": [
      {
        "id": "delivery-1",
        "webhookUrl": "https://external-system.com/webhook",
        "eventType": "VEHICLE_POSITION",
        "status": "SUCCESS",
        "responseCode": 200,
        "deliveredAt": "2025-01-03T10:30:01Z",
        "retryCount": 0
      },
      {
        "id": "delivery-2",
        "webhookUrl": "https://external-system.com/webhook",
        "eventType": "SENSOR_DATA",
        "status": "FAILED",
        "responseCode": 503,
        "deliveredAt": "2025-01-03T10:30:02Z",
        "retryCount": 2
      }
    ]
  }
}
```

---

## 🔍 VERIFICATION CHECKLIST

### ✅ Check Each Component:

1. **PostgreSQL Database**
   ```bash
   docker exec -it dco-postgres psql -U dco_user -d dco_db -c "SELECT COUNT(*) FROM scenario WHERE status='CREATED';"
   # Expected: 16
   ```

2. **RabbitMQ Queues**
   ```bash
   curl -u guest:guest http://localhost:15672/api/queues
   # Look for: webhook-events, simulation-events
   ```

3. **Scenario Library Service**
   ```bash
   curl http://localhost:8081/actuator/health
   # Expected: {"status":"UP"}
   ```

4. **Tracks Management Service**
   ```bash
   curl http://localhost:8082/actuator/health
   # Expected: {"status":"UP"}
   ```

5. **Message Queue Service**
   ```bash
   curl http://localhost:8083/actuator/health
   # Expected: {"status":"UP"}
   ```

6. **Webhook Management Service**
   ```bash
   curl http://localhost:8084/actuator/health
   # Expected: {"status":"UP"}
   ```

7. **DCO Gateway**
   ```bash
   curl http://localhost:8080/actuator/health
   # Expected: {"status":"UP"}
   ```

---

## 📈 DATA FLOW DIAGRAM

```
┌─────────────┐
│  Web UI     │
│ (Port 3000) │
└──────┬──────┘
       │ GraphQL Query
       ↓
┌─────────────────┐
│  DCO Gateway    │
│   (Port 8080)   │
└────────┬────────┘
         │
         ├─→ Scenario Library (8081) ─→ PostgreSQL (scenarios table)
         ├─→ Tracks Management (8082) ─→ PostgreSQL (tracks table)
         └─→ Simulation Engine
                    │
                    ↓
            ┌───────────────┐
            │   RabbitMQ    │
            │  (Port 5672)  │
            └───────┬───────┘
                    │
                    ↓
         ┌──────────────────────┐
         │ Message Queue Service│
         │     (Port 8083)      │
         └──────────┬───────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │ Webhook Management   │
         │     (Port 8084)      │
         └──────────┬───────────┘
                    │
                    ↓
         ┌──────────────────────┐
         │  External Webhooks   │
         │  (Customer Systems)  │
         └──────────────────────┘
```

---

## 🧪 TEST COMMANDS

### Test the Complete Flow:

```bash
# 1. Run E2E test script
./test-e2e-api-flow.sh

# 2. Monitor RabbitMQ in real-time
docker logs -f message-queue-service

# 3. Monitor Webhook deliveries
docker logs -f webhook-management-service

# 4. Check database for new simulations
docker exec -it dco-postgres psql -U dco_user -d dco_db -c "SELECT * FROM simulation ORDER BY created_at DESC LIMIT 5;"
```

---

## 🐛 TROUBLESHOOTING

### If Scenarios Don't Appear:
```sql
-- Check scenario status
SELECT id, name, status, type FROM scenario;

-- Fix if needed
UPDATE scenario SET status = 'CREATED' WHERE status != 'CREATED';
```

### If Webhooks Aren't Delivered:
```bash
# Check RabbitMQ has messages
curl -u guest:guest http://localhost:15672/api/queues/%2F/webhook-events

# Check webhook service is consuming
docker logs webhook-management-service | grep "Processing message"

# Check registered webhooks
docker exec -it dco-postgres psql -U dco_user -d dco_db -c "SELECT * FROM webhook WHERE active = true;"
```

### If RabbitMQ Connection Fails:
```bash
# Restart RabbitMQ
docker-compose restart rabbitmq

# Check connectivity
docker exec -it message-queue-service nc -zv rabbitmq 5672
```

---

## 📝 NEXT STEPS

After running `./test-e2e-api-flow.sh`, you should:

1. **Verify Scenarios**: Open http://localhost:3000 and see 16 scenarios
2. **Check RabbitMQ**: Open http://localhost:15672 (guest/guest) and see queues
3. **Monitor Webhooks**: Run `docker logs -f webhook-management-service`
4. **Test Simulation**: Create a simulation and watch events flow through RabbitMQ

---

**🎯 EXPECTED RESULT**: Complete visibility from user click → database → queue → webhook → external system!
