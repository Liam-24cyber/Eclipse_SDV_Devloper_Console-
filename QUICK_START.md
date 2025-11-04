# Quick Start Guide - SDV Developer Console E2E Testing

## Current Status

✅ **Infrastructure Fixed:**
- Database health checks added
- RabbitMQ health checks added  
- Service dependencies properly configured

✅ **Event Publishing Code Added:**
- MessageQueueClient created
- SimulationEventPublisher service created
- Duplicate event prevention implemented

⚠️ **Build Status:**
- Most services have existing Docker images
- scenario-library-service needs rebuild with new event code

---

## Quick Start (3 Steps)

### Step 1: Deploy Services

```bash
# Stop any running containers
docker-compose down

# Start all services with health checks
./20-deploy-script.sh

# OR manually:
docker-compose up -d
```

### Step 2: Wait for Services (2-3 minutes)

```bash
# Check service health
./check-status.sh

# OR manually check:
docker ps
docker logs scenario-library-service
docker logs webhook-management-service
```

### Step 3: Run End-to-End Test

```bash
./test-e2e.sh
```

---

## Manual Testing Steps

### 1. Access the UI
- URL: http://localhost:3000
- Create scenarios and tracks if needed

### 2. Create a Webhook
```bash
curl -X POST http://localhost:8084/api/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Webhook",
    "url": "https://webhook.site/YOUR-UNIQUE-ID",
    "eventTypes": ["simulation.started", "simulation.completed", "simulation.failed"],
    "active": true
  }'
```

Get your unique webhook.site URL from: https://webhook.site

### 3. Launch a Simulation
- Via UI: http://localhost:3000
- Via API:
```bash
curl -X POST http://localhost:8082/api/simulation/launch \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Simulation",
    "description": "Testing webhook events",
    "platform": "CARLA",
    "environment": "Urban",
    "scenarioType": "Test",
    "hardware": "Cloud",
    "scenarios": ["SCENARIO-ID-HERE"],
    "tracks": ["TRACK-ID-HERE"]
  }'
```

### 4. Monitor Events

**RabbitMQ Management UI:**
- URL: http://localhost:15672
- Username: admin
- Password: admin123
- Navigate to: Queues → simulation.events

**Check Webhook Deliveries:**
```bash
# Get webhook ID from creation response
WEBHOOK_ID="your-webhook-id"

curl http://localhost:8084/api/webhooks/$WEBHOOK_ID/deliveries
```

**Check Logs:**
```bash
# Scenario service (event publishing)
docker logs scenario-library-service | grep -i "event"

# Webhook service (event consumption)
docker logs webhook-management-service | grep -i "simulation"

# Message queue service
docker logs message-queue-service | grep -i "publish"
```

---

## Troubleshooting

### Services Not Starting
```bash
# Check health
docker ps
docker logs postgres
docker logs rabbitmq

# Restart specific service
docker-compose restart scenario-library-service
```

### No Events Published
```bash
# Check if scenario service can reach message queue
docker exec scenario-library-service curl http://message-queue-service:8083/api/v1/health

# Check RabbitMQ connection
docker logs message-queue-service | grep -i "rabbit"
```

### Webhooks Not Receiving Events
```bash
# Check webhook is active
curl http://localhost:8084/api/webhooks

# Check delivery attempts
curl http://localhost:8084/api/webhooks/WEBHOOK-ID/deliveries

# Check webhook consumer logs
docker logs webhook-management-service | tail -100
```

---

## Rebuilding scenario-library-service

If the Docker build completes successfully:

```bash
# Check if build finished
docker images | grep scenario-library-service

# If image shows recent timestamp, restart the service
docker-compose restart scenario-library-service

# Verify new code is running
docker logs scenario-library-service | grep "SimulationEventPublisher"
```

---

## Service URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Developer Console UI | http://localhost:3000 | - |
| DCO Gateway | http://localhost:8080 | - |
| Tracks Management | http://localhost:8081 | - |
| Scenario Library | http://localhost:8082 | - |
| Message Queue | http://localhost:8083 | - |
| Webhook Management | http://localhost:8084 | - |
| RabbitMQ Management | http://localhost:15672 | admin/admin123 |
| PostgreSQL | localhost:5432 | postgres/postgres |
| PgAdmin | http://localhost:5050 | admin@default.com/admin |
| Minio | http://localhost:9000 | minioadmin/minioadmin |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3001 | admin/admin |

---

## Expected Event Flow

1. **User launches simulation** → POST /api/simulation/launch
2. **Simulation saved to DB** → simulation.started event published
3. **Message Queue Service** → receives event → publishes to RabbitMQ
4. **RabbitMQ** → routes to simulation.events queue
5. **Webhook Service** → consumes from queue → delivers to webhooks
6. **Campaign status changes** → simulation.completed or simulation.failed published
7. **Webhooks receive all events**

---

## Next Steps

1. ✅ Deploy services: `./20-deploy-script.sh`
2. ✅ Check status: `./check-status.sh`  
3. ✅ Run E2E test: `./test-e2e.sh`
4. ⏳ Wait for scenario-library-service rebuild to complete
5. ✅ Verify event publishing in logs
6. ✅ Test complete workflow with real webhooks

---

## Success Criteria

- [ ] All services healthy
- [ ] RabbitMQ queues created
- [ ] Webhook can be created
- [ ] Simulation can be launched
- [ ] simulation.started event published
- [ ] simulation.completed/failed events published
- [ ] Webhooks receive all events
- [ ] No duplicate events

---

For detailed issue resolution, see: `COMPLETE_ISSUE_RESOLUTION_REPORT.md`
For event flow documentation, see: `EVENT_FLOW_DOCUMENTATION.md`
