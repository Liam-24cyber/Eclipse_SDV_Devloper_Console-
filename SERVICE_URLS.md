# SDV Developer Console - Service URLs & Testing Guide

## 🌐 All Service URLs (Localhost)

### Core Application Services

#### 1. **Developer Console UI** (Frontend)
- **URL**: http://localhost:3000
- **Purpose**: Main web application interface
- **Description**: React-based UI for managing scenarios, tracks, and simulations

#### 2. **DCO Gateway** (API Gateway)
- **URL**: http://localhost:8080
- **GraphQL Playground**: http://localhost:8080/graphql
- **Purpose**: Main API gateway for all backend services
- **Description**: Aggregates all microservices behind a single GraphQL endpoint

---

### Microservices

#### 3. **Message Queue Service**
- **URL**: http://localhost:8083
- **Health Check**: http://localhost:8083/actuator/health
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **Purpose**: RabbitMQ integration and event publishing
- **Key Endpoints**:
  - `POST /api/v1/events/publish` - Publish events to RabbitMQ
  - `GET /api/v1/events/stats` - Get queue statistics

#### 4. **Scenario Library Service**
- **URL**: http://localhost:8081
- **Health Check**: http://localhost:8081/actuator/health
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Purpose**: Manage test scenarios
- **Key Endpoints**:
  - `GET /api/v1/scenarios` - List all scenarios
  - `POST /api/v1/scenarios` - Create new scenario
  - `GET /api/v1/scenarios/{id}` - Get scenario details

#### 5. **Tracks Management Service**
- **URL**: http://localhost:8082
- **Health Check**: http://localhost:8082/actuator/health
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **Purpose**: Manage test tracks
- **Key Endpoints**:
  - `GET /api/v1/tracks` - List all tracks
  - `POST /api/v1/tracks` - Create new track
  - `GET /api/v1/tracks/{id}` - Get track details

#### 6. **Webhook Management Service**
- **URL**: http://localhost:8084
- **Health Check**: http://localhost:8084/actuator/health
- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **Purpose**: Process RabbitMQ events and store to database
- **Key Endpoints**:
  - `GET /api/v1/webhooks` - List webhook configurations
  - `POST /api/v1/webhooks` - Create webhook
  - `GET /api/v1/events` - Get processed events from DB

---

### Infrastructure Services

#### 7. **RabbitMQ Management**
- **URL**: http://localhost:15672
- **Username**: `admin`
- **Password**: `admin123`
- **Purpose**: Monitor message queues, exchanges, and bindings
- **Features**:
  - View queue statistics
  - Monitor message flow
  - Inspect dead letter queues
  - Manual message publishing

#### 8. **PostgreSQL Database (via pgAdmin)**
- **URL**: http://localhost:5050
- **Username**: `admin@sdv.com`
- **Password**: `admin123`
- **Purpose**: Database administration and query tool
- **To Connect**:
  1. Login to pgAdmin
  2. Add new server:
     - Host: `postgres`
     - Port: `5432`
     - Database: `sdv_dco`
     - Username: `postgres`
     - Password: `postgres123`

#### 9. **MinIO (Object Storage)**
- **URL**: http://localhost:9001
- **Username**: `minioadmin`
- **Password**: `minioadmin`
- **Purpose**: Store simulation files and track data
- **Buckets**:
  - `scenarios` - Scenario definitions
  - `tracks` - Track files
  - `simulations` - Simulation results

#### 10. **Prometheus (Metrics)**
- **URL**: http://localhost:9090
- **Purpose**: Metrics collection and monitoring
- **Key Queries**:
  - RabbitMQ metrics
  - Service health metrics
  - Custom application metrics

#### 11. **Grafana (Dashboards)**
- **URL**: http://localhost:3001
- **Username**: `admin`
- **Password**: `admin`
- **Purpose**: Visualization dashboards for metrics
- **Features**:
  - Pre-configured RabbitMQ dashboard
  - Service performance metrics
  - Custom alerting

#### 12. **Redis**
- **Host**: `localhost:6379`
- **Purpose**: Caching and session management
- **Note**: No web UI by default (use Redis CLI or Redis Insight)

---

## 🧪 Testing the Complete Flow

### End-to-End Test: Simulation → RabbitMQ → Webhook → Database

#### **Step 1: Publish Event to RabbitMQ**

```bash
# Test scenario creation event
curl -X POST http://localhost:8083/api/v1/events/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "scenario.created",
    "payload": {
      "scenarioId": "test-scenario-001",
      "name": "Highway Test Scenario",
      "description": "Test autonomous driving on highway",
      "timestamp": "2025-11-03T19:00:00Z"
    }
  }'
```

**Expected Response:**
```json
{
  "eventId": "uuid-here",
  "status": "PUBLISHED",
  "timestamp": "2025-11-03T19:00:00.123Z"
}
```

#### **Step 2: Monitor in RabbitMQ**

1. Open http://localhost:15672
2. Login with `admin` / `admin123`
3. Go to **Queues** tab
4. Check message counts in:
   - `scenario.events` - Should show 1 message processed
   - `scenario.events.dlq` - Should be empty (if successful)

#### **Step 3: Verify in Database**

1. Open http://localhost:5050 (pgAdmin)
2. Login with `admin@sdv.com` / `admin123`
3. Connect to PostgreSQL database:
   - Host: `postgres`
   - Database: `sdv_dco`
   - Username: `postgres`
   - Password: `postgres123`
4. Run query:
```sql
-- Check if event was stored
SELECT * FROM webhook_events 
WHERE event_type = 'scenario.created' 
ORDER BY created_at DESC 
LIMIT 10;

-- Or check scenarios table if applicable
SELECT * FROM scenarios 
ORDER BY created_at DESC 
LIMIT 10;
```

#### **Step 4: Check Webhook Service Logs**

```bash
docker logs webhook-management-service --tail 50
```

Look for successful message processing logs.

---

## 📊 Quick Health Check Script

```bash
#!/bin/bash
echo "=== SDV Developer Console Health Check ==="
echo ""
echo "Frontend:"
curl -s -o /dev/null -w "  UI: %{http_code}\n" http://localhost:3000

echo ""
echo "Gateway:"
curl -s -o /dev/null -w "  DCO Gateway: %{http_code}\n" http://localhost:8080/graphql

echo ""
echo "Microservices:"
curl -s http://localhost:8081/actuator/health | jq -r '"  Scenario Service: \(.status)"'
curl -s http://localhost:8082/actuator/health | jq -r '"  Tracks Service: \(.status)"'
curl -s http://localhost:8083/actuator/health | jq -r '"  Message Queue Service: \(.status)"'
curl -s http://localhost:8084/actuator/health | jq -r '"  Webhook Service: \(.status)"'

echo ""
echo "Infrastructure:"
curl -s -o /dev/null -w "  RabbitMQ: %{http_code}\n" http://localhost:15672
curl -s -o /dev/null -w "  pgAdmin: %{http_code}\n" http://localhost:5050
curl -s -o /dev/null -w "  MinIO: %{http_code}\n" http://localhost:9001
curl -s -o /dev/null -w "  Prometheus: %{http_code}\n" http://localhost:9090
curl -s -o /dev/null -w "  Grafana: %{http_code}\n" http://localhost:3001
```

---

## 🔍 Monitoring RabbitMQ Message Flow

### Via RabbitMQ Management UI

1. **Navigate to**: http://localhost:15672
2. **Login**: admin / admin123
3. **Check Queues**: Click "Queues" tab
4. **Monitor Messages**:
   - Total messages in queue
   - Messages ready
   - Messages unacknowledged
   - Consumer count

### Via Message Queue Service API

```bash
# Get queue statistics
curl http://localhost:8083/api/v1/events/stats

# Get specific queue info
curl http://localhost:8083/api/v1/events/queues/scenario.events
```

---

## 🎯 Complete Test Scenarios

### Test 1: Scenario Creation Flow

```bash
# 1. Create scenario via API
curl -X POST http://localhost:8081/api/v1/scenarios \
  -H "Content-Type: application/json" \
  -d '{
    "name": "City Driving Test",
    "description": "Urban environment test",
    "difficulty": "MEDIUM"
  }'

# 2. Check RabbitMQ for scenario.created event
# Visit: http://localhost:15672/#/queues/%2F/scenario.events

# 3. Verify in database via pgAdmin
# Visit: http://localhost:5050
```

### Test 2: Track Upload Flow

```bash
# 1. Create track
curl -X POST http://localhost:8082/api/v1/tracks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nurburgring",
    "location": "Germany",
    "length": 20832,
    "difficulty": "HARD"
  }'

# 2. Check track.events queue
# Visit: http://localhost:15672/#/queues/%2F/track.events

# 3. Verify MinIO storage
# Visit: http://localhost:9001
```

### Test 3: Simulation Execution Flow

```bash
# 1. Trigger simulation via GraphQL
# Visit: http://localhost:8080/graphql

# GraphQL Mutation:
mutation {
  startSimulation(input: {
    scenarioId: "test-scenario-001"
    trackId: "test-track-001"
  }) {
    simulationId
    status
  }
}

# 2. Monitor simulation.events queue
# Visit: http://localhost:15672/#/queues/%2F/simulation.events

# 3. Check webhook processing
curl http://localhost:8084/api/v1/events?type=simulation
```

---

## 🚨 Troubleshooting URLs

### Check Service Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker logs -f message-queue-service
docker logs -f webhook-management-service
docker logs -f rabbitmq
```

### Dead Letter Queue Inspection
- **URL**: http://localhost:15672/#/queues/%2F/scenario.events.dlq
- **Purpose**: Check failed messages that couldn't be processed

### Database Query Tool
- **URL**: http://localhost:5050
- **Purpose**: Run SQL queries to verify data persistence

---

## 📝 Quick Reference

| Service | Port | Login | Purpose |
|---------|------|-------|---------|
| UI | 3000 | - | Frontend |
| Gateway | 8080 | - | GraphQL API |
| Scenario Service | 8081 | - | Scenarios |
| Tracks Service | 8082 | - | Tracks |
| Message Queue | 8083 | - | RabbitMQ Integration |
| Webhook Service | 8084 | - | Event Processing |
| RabbitMQ | 15672 | admin/admin123 | Queue Management |
| pgAdmin | 5050 | admin@sdv.com/admin123 | Database Admin |
| MinIO | 9001 | minioadmin/minioadmin | Object Storage |
| Prometheus | 9090 | - | Metrics |
| Grafana | 3001 | admin/admin | Dashboards |

---

## ✅ Current Status

All services are running and healthy. You can:
1. ✅ Test event publishing via http://localhost:8083
2. ✅ Monitor queues via http://localhost:15672
3. ✅ Check database via http://localhost:5050
4. ✅ View metrics via http://localhost:9090
5. ✅ Access UI via http://localhost:3000

**Happy Testing! 🚀**
