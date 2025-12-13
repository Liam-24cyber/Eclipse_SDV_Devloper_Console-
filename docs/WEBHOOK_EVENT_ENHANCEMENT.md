# Webhook Event Enhancement Guide

## Current Event Structure

The `scenario.created` event currently includes:
```json
{
  "eventId": "event-123",
  "eventType": "scenario.created",
  "timestamp": "2025-12-13T...",
  "source": "scenario-library-service",
  "data": {
    "scenarioId": "uuid",
    "name": "Scenario Name",
    "description": "Description",
    "status": "CREATED",
    "type": "CAN"
  }
}
```

## Option 1: Include Track & Simulation in scenario.created Event

To include track and simulation data in the webhook, modify Step 6b in `run-e2e-demo.sh`:

```bash
# Enhanced payload with track and simulation data:
ENHANCED_PAYLOAD=$(cat <<EOF
{
  "eventId": "$EVENT_ID",
  "eventType": "scenario.created",
  "timestamp": "$EVENT_TIMESTAMP",
  "source": "scenario-library-service",
  "data": {
    "scenario": {
      "id": "$SCENARIO_ID",
      "name": "$SCENARIO_NAME",
      "description": "$SCENARIO_DESCRIPTION",
      "status": "CREATED",
      "type": "CAN"
    },
    "track": {
      "id": "${TRACK_ID:-null}",
      "name": "${TRACK_NAME:-null}"
    },
    "simulation": {
      "id": "${SIMULATION_ID:-null}",
      "name": "${SIMULATION_NAME:-null}"
    }
  }
}
EOF
)

# Publish with enhanced payload
curl -s -u admin:admin123 -X POST http://localhost:15672/api/exchanges/%2F/sdv.events/publish \
  -H "Content-Type: application/json" \
  -d "{\"properties\":{},\"routing_key\":\"scenario.created\",\"payload\":\"$(echo $ENHANCED_PAYLOAD | jq -c . | sed 's/"/\\"/g')\",\"payload_encoding\":\"string\"}"
```

## Option 2: Publish Separate Events (Recommended)

Better approach - publish different event types:

1. **scenario.created** - when scenario is created (current)
2. **simulation.created** - when simulation is created
3. **track.selected** - when track is selected

This follows event-driven architecture best practices.

### Example:

```bash
# Publish simulation.created event
curl -s -u admin:admin123 -X POST http://localhost:15672/api/exchanges/%2F/sdv.events/publish \
  -H "Content-Type: application/json" \
  -d '{
    "properties":{},
    "routing_key":"simulation.created",
    "payload":"{\"eventId\":\"sim-event-001\",\"eventType\":\"simulation.created\",\"timestamp\":\"2025-12-13T...\",\"source\":\"simulation-service\",\"data\":{\"simulationId\":\"...\",\"name\":\"...\",\"scenarioIds\":[\"...\"],\"trackId\":\"...\",\"status\":\"PENDING\"}}",
    "payload_encoding":"string"
  }'
```

## Option 3: Use Event Aggregation

Create a composite event that combines multiple entities:

```json
{
  "eventId": "composite-001",
  "eventType": "workflow.completed",
  "timestamp": "2025-12-13T...",
  "source": "e2e-demo-script",
  "data": {
    "workflowId": "e2e-demo-001",
    "entities": {
      "scenario": { "id": "...", "name": "..." },
      "track": { "id": "...", "name": "..." },
      "simulation": { "id": "...", "name": "..." }
    }
  }
}
```

## Recommendation

For the E2E demo, **Option 2 (separate events)** is best because:
- ✅ Each event has a clear purpose
- ✅ Subscribers can choose which events to listen to
- ✅ Follows event-driven architecture patterns
- ✅ Each service publishes its own events
- ✅ Events are focused and maintainable

The current implementation correctly publishes only scenario data in the `scenario.created` event, which is the expected behavior!
