# Simulation Event Flow Documentation

## Event Publishing Architecture

### Event Flow Diagram
```
Simulation Launch
       ↓
SimulationServiceImpl.launchSimulation()
       ↓
SimulationEventPublisher.publishSimulationStarted()
       ↓
MessageQueueClient → Message Queue Service
       ↓
RabbitMQ (simulation.events queue)
       ↓
WebhookEventConsumer.handleSimulationEvent()
       ↓
WebhookDeliveryService.deliverEventToWebhooks()
       ↓
HTTP POST to registered webhooks
```

### Event Types

#### 1. simulation.started
**When:** Immediately after a simulation is launched
**Publisher:** SimulationServiceImpl.launchSimulation()
**Payload:**
```json
{
  "eventType": "simulation.started",
  "source": "scenario-library-service",
  "correlationId": "<simulationId>",
  "data": {
    "simulationId": "<uuid>",
    "simulationName": "Test Simulation",
    "status": "Running",
    "campaignId": "<uuid>",
    "platform": "CARLA",
    "environment": "Urban",
    "scenarioCount": 5,
    "trackCount": 3
  }
}
```

#### 2. simulation.completed
**When:** When a simulation finishes successfully (status = "Done")
**Publisher:** CampaignService.generateSampleResults()
**Payload:**
```json
{
  "eventType": "simulation.completed",
  "source": "scenario-library-service",
  "correlationId": "<simulationId>",
  "data": {
    "simulationId": "<uuid>",
    "simulationName": "Test Simulation",
    "status": "Done",
    "completedAt": "2025-11-02T10:30:00Z"
  }
}
```

#### 3. simulation.failed
**When:** When a simulation encounters an error (status = "Error")
**Publisher:** CampaignService.generateSampleResults()
**Payload:**
```json
{
  "eventType": "simulation.failed",
  "source": "scenario-library-service",
  "correlationId": "<simulationId>",
  "data": {
    "simulationId": "<uuid>",
    "simulationName": "Test Simulation",
    "status": "Error",
    "errorMessage": "Configuration validation failed",
    "failedAt": "2025-11-02T10:35:00Z"
  }
}
```

## Message Queue Architecture

### RabbitMQ Setup
- **Exchange:** `sdv.events` (topic exchange)
- **Queue:** `simulation.events`
- **Routing Key:** `simulation.*`
- **Dead Letter Queue:** `simulation.events.dlq`
- **Message TTL:** 1 hour

### Services Involved

1. **scenario-library-service**
   - Publishes simulation events
   - Uses MessageQueueClient (Feign)
   - Connects to: message-queue-service:8083

2. **message-queue-service**
   - Receives event publish requests via REST API
   - Publishes to RabbitMQ exchange
   - Port: 8083

3. **webhook-management-service**
   - Consumes from simulation.events queue
   - Delivers events to registered webhooks
   - Port: 8084

## Testing Events

### 1. Check RabbitMQ Management UI
```
URL: http://localhost:15672
Username: admin
Password: admin123

Navigate to Queues → simulation.events
Check message rates and content
```

### 2. Monitor Service Logs
```bash
# Watch for event publishing
docker logs -f scenario-library-service | grep -i "event"

# Watch for event consumption
docker logs -f webhook-management-service | grep -i "simulation event"

# Watch for webhook delivery
docker logs -f webhook-management-service | grep -i "deliver"
```

### 3. Verify Event Delivery
```bash
# Create a test webhook
curl -X POST http://localhost:8084/api/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://webhook.site/your-unique-id",
    "eventTypes": ["simulation.started", "simulation.completed", "simulation.failed"],
    "active": true
  }'

# Launch a simulation and watch webhook.site for events
```

## Troubleshooting

### Events Not Being Published
1. Check scenario-library-service can reach message-queue-service:
   ```bash
   docker exec scenario-library-service curl http://message-queue-service:8083/actuator/health
   ```

2. Check message-queue-service logs:
   ```bash
   docker logs message-queue-service | tail -50
   ```

3. Verify RabbitMQ connection:
   ```bash
   docker logs message-queue-service | grep -i rabbitmq
   ```

### Events Not Being Consumed
1. Check webhook-management-service is connected to RabbitMQ:
   ```bash
   docker logs webhook-management-service | grep -i "rabbit"
   ```

2. Verify queue has messages:
   - Go to RabbitMQ UI → Queues → simulation.events
   - Check "Messages" count

3. Check for consumer errors:
   ```bash
   docker logs webhook-management-service | grep -i error
   ```

### Webhooks Not Receiving Events
1. Check webhook is active and has correct event types
2. Verify webhook URL is accessible
3. Check delivery attempts in database:
   ```sql
   SELECT * FROM webhook_delivery 
   WHERE webhook_id = '<your-webhook-id>' 
   ORDER BY created_at DESC;
   ```

## Configuration

### Environment Variables

**scenario-library-service:**
- `MESSAGE_QUEUE_SERVICE_URL`: URL of message queue service (default: http://message-queue-service:8083)

**message-queue-service:**
- `SPRING_RABBITMQ_HOST`: RabbitMQ host (default: rabbitmq)
- `SPRING_RABBITMQ_PORT`: RabbitMQ port (default: 5672)
- `SPRING_RABBITMQ_USERNAME`: RabbitMQ username (default: admin)
- `SPRING_RABBITMQ_PASSWORD`: RabbitMQ password (default: admin123)

**webhook-management-service:**
- `SPRING_RABBITMQ_HOST`: RabbitMQ host (default: rabbitmq)
- `SPRING_RABBITMQ_PORT`: RabbitMQ port (default: 5672)
- `SPRING_RABBITMQ_USERNAME`: RabbitMQ username (default: admin)
- `SPRING_RABBITMQ_PASSWORD`: RabbitMQ password (default: admin123)
- `MESSAGE_QUEUE_SERVICE_URL`: URL of message queue service (default: message-queue-service:8083)
