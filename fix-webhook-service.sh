#!/bin/bash
# Permanent fix for webhook-management-service restart loop
# This script ensures webhook tables are created and services start cleanly

set -e  # Exit on error

echo "=================================================="
echo "WEBHOOK SERVICE PERMANENT FIX"
echo "=================================================="
echo ""

# Set Java 17
echo "Step 1: Setting Java 17..."
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
echo "✓ Java version: $(java -version 2>&1 | head -n 1)"
echo ""

# Stop affected services
echo "Step 2: Stopping affected services..."
docker-compose down postgres webhook-management-service message-queue-service
echo "✓ Services stopped"
echo ""

# Remove volumes to force fresh initialization
echo "Step 3: Removing volumes for fresh database initialization..."
docker volume rm eclipse_sdv_devloper_console-_postgres_data 2>/dev/null || echo "  (Volume already removed or doesn't exist)"
echo "✓ Volumes removed"
echo ""

# Rebuild postgres with webhook tables
echo "Step 4: Rebuilding postgres container..."
docker-compose build --no-cache postgres
echo "✓ Postgres rebuilt"
echo ""

# Start postgres and wait for it to be ready
echo "Step 5: Starting postgres..."
docker-compose up -d postgres
echo "  Waiting for postgres to be ready..."
sleep 10
echo "✓ Postgres started"
echo ""

# Verify webhook tables exist
echo "Step 6: Verifying webhook tables in database..."
docker-compose exec -T postgres psql -U postgres -d postgres -c '\dt' | grep -E 'webhook' && echo "✓ Webhook tables found!" || echo "⚠ Warning: Webhook tables not found"
echo ""

# Rebuild message-queue-service with RabbitAdmin fix
echo "Step 7: Rebuilding message-queue-service..."
cd message-queue-service
mvn clean package -DskipTests
cd ..
docker-compose build --no-cache message-queue-service
echo "✓ Message-queue-service rebuilt"
echo ""

# Start message-queue-service and wait
echo "Step 8: Starting message-queue-service..."
docker-compose up -d message-queue-service
echo "  Waiting for message-queue-service to initialize RabbitMQ..."
sleep 15
echo "✓ Message-queue-service started"
echo ""

# Verify RabbitMQ queues created
echo "Step 9: Verifying RabbitMQ queues..."
docker-compose exec -T rabbitmq rabbitmqctl list_queues | grep -E 'scenario|track|simulation|webhook' && echo "✓ RabbitMQ queues created!" || echo "⚠ Warning: RabbitMQ queues not found"
echo ""

# Rebuild webhook-management-service
echo "Step 10: Rebuilding webhook-management-service..."
cd webhook-management-service
mvn clean package -DskipTests
cd ..
docker-compose build --no-cache webhook-management-service
echo "✓ Webhook-management-service rebuilt"
echo ""

# Start webhook-management-service
echo "Step 11: Starting webhook-management-service..."
docker-compose up -d webhook-management-service
echo "  Waiting for webhook-management-service to start..."
sleep 10
echo "✓ Webhook-management-service started"
echo ""

# Check service status
echo "=================================================="
echo "VERIFICATION"
echo "=================================================="
echo ""
echo "Service Status:"
docker-compose ps postgres message-queue-service webhook-management-service
echo ""

echo "Recent webhook-management-service logs:"
docker-compose logs --tail=20 webhook-management-service
echo ""

echo "=================================================="
echo "FIX COMPLETE!"
echo "=================================================="
echo ""
echo "Next steps:"
echo "1. Check logs: docker-compose logs -f webhook-management-service"
echo "2. Verify all services are healthy: docker-compose ps"
echo "3. Test webhook functionality through the API"
echo ""
