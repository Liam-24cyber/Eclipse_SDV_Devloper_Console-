#!/bin/bash

# RabbitMQ Queue Fix - Rebuild Script
# This script rebuilds the services with the permanent RabbitMQ queue creation fix

set -e

echo "=========================================="
echo "🔧 Applying RabbitMQ Queue Creation Fix"
echo "=========================================="
echo ""

echo "📋 Fix Summary:"
echo "  ✅ Added @EnableRabbit to message-queue-service"
echo "  ✅ Created RabbitMQInitializer for automatic queue creation"
echo "  ✅ Removed duplicate queue declarations from webhook-management-service"
echo "  ✅ Added health check to message-queue-service"
echo "  ✅ Updated service dependencies in docker-compose.yml"
echo ""

# Stop all services
echo "🛑 Stopping all services..."
docker-compose down
echo ""

# Clean up old images (optional - comment out if you want to keep them)
echo "🧹 Cleaning up old images..."
docker rmi -f message-queue-service:1.0 webhook-management-service:1.0 2>/dev/null || true
echo ""

# Rebuild the affected services
echo "🔨 Rebuilding message-queue-service..."
docker-compose build message-queue-service
echo ""

echo "🔨 Rebuilding webhook-management-service..."
docker-compose build webhook-management-service
echo ""

# Start all services
echo "🚀 Starting all services..."
docker-compose up -d
echo ""

# Wait for message-queue-service to initialize
echo "⏳ Waiting for message-queue-service to initialize RabbitMQ resources..."
echo "   (This may take up to 30 seconds...)"
sleep 35
echo ""

# Check if queues were created
echo "🔍 Verifying RabbitMQ queues were created..."
echo ""
docker exec rabbitmq rabbitmqctl list_queues
echo ""

# Check service statuses
echo "📊 Service Status:"
docker-compose ps
echo ""

# Show message-queue-service logs
echo "📝 message-queue-service initialization logs:"
docker-compose logs message-queue-service | grep -i "rabbitmq\|queue\|exchange" | tail -20
echo ""

# Check webhook service status
echo "🔍 Checking webhook-management-service status..."
WEBHOOK_STATUS=$(docker inspect --format='{{.State.Status}}' webhook-management-service 2>/dev/null || echo "not found")
WEBHOOK_RESTARTS=$(docker inspect --format='{{.RestartCount}}' webhook-management-service 2>/dev/null || echo "0")

if [ "$WEBHOOK_STATUS" = "running" ] && [ "$WEBHOOK_RESTARTS" -lt 2 ]; then
    echo "✅ webhook-management-service is RUNNING (Restarts: $WEBHOOK_RESTARTS)"
else
    echo "⚠️  webhook-management-service status: $WEBHOOK_STATUS (Restarts: $WEBHOOK_RESTARTS)"
    echo "    Check logs: docker-compose logs webhook-management-service"
fi
echo ""

# Verify exchanges
echo "🔍 Verifying RabbitMQ exchanges..."
docker exec rabbitmq rabbitmqctl list_exchanges | grep sdv
echo ""

echo "=========================================="
echo "✅ FIX APPLIED SUCCESSFULLY!"
echo "=========================================="
echo ""
echo "📋 Next Steps:"
echo "  1. Verify queues exist: docker exec rabbitmq rabbitmqctl list_queues"
echo "  2. Check all services: docker-compose ps"
echo "  3. Test E2E flow from UI"
echo ""
echo "📄 For details, see: RABBITMQ_PERMANENT_FIX.md"
echo ""
