#!/bin/bash

echo "🗑️  Purging dead letter queues..."

# List of DLQs to purge
DLQS=(
  "scenario.events.dlq"
  "track.events.dlq"
  "simulation.events.dlq"
)

for DLQ in "${DLQS[@]}"; do
  echo "Purging: $DLQ"
  
  # Get message count before
  BEFORE=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/$DLQ | jq '.messages')
  echo "  Messages before: $BEFORE"
  
  # Purge queue
  curl -s -u admin:admin123 -X DELETE \
    "http://localhost:15672/api/queues/%2F/$DLQ/contents"
  
  # Verify purge
  sleep 1
  AFTER=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/$DLQ | jq '.messages')
  echo "  Messages after: $AFTER"
  echo "  ✅ Purged $(($BEFORE - $AFTER)) messages"
  echo ""
done

echo "✅ All DLQs purged!"
