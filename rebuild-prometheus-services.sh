#!/bin/bash

# Rebuild services with Prometheus support
echo "Rebuilding services with Prometheus metrics support..."

cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"

# Stop the services
echo "Stopping services..."
docker-compose stop message-queue-service webhook-management-service tracks-management-service scenario-library-service

# Rebuild each service
echo "Rebuilding message-queue-service..."
docker-compose build message-queue-service

echo "Rebuilding webhook-management-service..."
docker-compose build webhook-management-service

echo "Rebuilding tracks-management-service..."
docker-compose build tracks-management-service

echo "Rebuilding scenario-library-service..."
docker-compose build scenario-library-service

# Start the services
echo "Starting services..."
docker-compose up -d message-queue-service webhook-management-service tracks-management-service scenario-library-service

echo "Waiting for services to start..."
sleep 15

echo "All services rebuilt and started!"
echo ""
echo "Check Prometheus targets at: http://localhost:9090/targets"
