#!/bin/bash
set -e

echo "🏥 Starting Comprehensive System Health Check..."

# 1. Check API Gateway
echo "👉 Checking API Gateway..."
for i in {1..30}; do
  if curl -s http://localhost:8080/actuator/health | grep "UP"; then
    echo "✅ API Gateway is UP"
    break
  fi
  echo "   Waiting for API Gateway..."
  sleep 2
done

# 2. Check Webhook Service
echo "👉 Checking Webhook Service..."
for i in {1..30}; do
  if curl -s http://localhost:8084/actuator/health | grep "UP"; then
    echo "✅ Webhook Service is UP"
    break
  fi
  echo "   Waiting for Webhook Service..."
  sleep 2
done

# 3. Verify Database (PostgreSQL)
echo "👉 Checking Database Content..."
# Wait for postgres to be ready to accept commands
sleep 5
SCENARIO_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenario;" 2>/dev/null | xargs || echo "0")
echo "   Found $SCENARIO_COUNT scenarios."

if [ "$SCENARIO_COUNT" -eq "0" ]; then
  echo "⚠️ Database is empty. Seeding required."
else
  echo "✅ Database has data."
fi

# 4. Check RabbitMQ Queues
echo "👉 Checking RabbitMQ Queues..."
# We use the management API (port 15672)
if curl -s -u admin:admin123 http://localhost:15672/api/queues > /dev/null; then
  echo "✅ RabbitMQ Management API is accessible"
else
  echo "❌ RabbitMQ Management API failed"
  exit 1
fi

echo "🎉 System Health Check Passed!"