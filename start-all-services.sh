#!/bin/bash
# 🚀 SDV Developer Console - Complete Startup Script
# This script ensures all services start in the correct order with proper health checks

set -e  # Exit on error

echo "🚀 Starting SDV Developer Console Stack"
echo "========================================="
echo ""

# Color codes for output
GREEN='\echo ""
echo "📚 Database Status:"
FINAL_SCENARIO_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenario;" 2>/dev/null | xargs || echo "0")
FINAL_TRACK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM track;" 2>/dev/null | xargs || echo "0")
FINAL_WEBHOOK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhooks;" 2>/dev/null | xargs || echo "0")
echo "   📊 Scenarios: $FINAL_SCENARIO_COUNT"
echo "   🛣️  Tracks: $FINAL_TRACK_COUNT"
echo "   🪝 Webhooks: $FINAL_WEBHOOK_COUNT"
echo ""2m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored status
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Step 1: Check if Docker is running
echo "📋 Step 1: Checking prerequisites..."
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker Desktop first."
    exit 1
fi
print_status "Docker is running"

# Step 2: Clean up old containers (optional - uncomment if needed)
# echo ""
# echo "📋 Step 2: Cleaning up old containers..."
# docker-compose down
# print_status "Old containers removed"

# Step 3: Build all images (ensures latest code is used)
echo ""
echo "📋 Step 2: Building Docker images..."
docker-compose build --no-cache webhook-management-service
print_status "Webhook service image built with latest fixes"

# Step 4: Start infrastructure services first (in order)
echo ""
echo "📋 Step 3: Starting infrastructure services..."
echo "   Starting PostgreSQL..."
docker-compose up -d postgres
sleep 5

echo "   Waiting for PostgreSQL to be healthy..."
until docker exec postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo -n "."
    sleep 1
done
print_status "PostgreSQL is healthy"

echo ""
echo "   Starting RabbitMQ..."
docker-compose up -d rabbitmq
sleep 5

echo "   Waiting for RabbitMQ to be healthy..."
until docker exec rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; do
    echo -n "."
    sleep 1
done
print_status "RabbitMQ is healthy"

echo ""
echo "   Starting Redis..."
docker-compose up -d redis
sleep 3
print_status "Redis is running"

echo ""
echo "   Starting MinIO..."
docker-compose up -d minio
sleep 3
print_status "MinIO is running"

# Step 5: Start middleware services
echo ""
echo "📋 Step 4: Starting middleware services..."
echo "   Starting Message Queue Service..."
docker-compose up -d message-queue-service
sleep 5

echo "   Waiting for Message Queue Service to be healthy..."
COUNTER=0
until docker exec message-queue-service curl -f http://localhost:8083/actuator/health > /dev/null 2>&1 || [ $COUNTER -eq 30 ]; do
    echo -n "."
    sleep 2
    COUNTER=$((COUNTER+1))
done
if [ $COUNTER -eq 30 ]; then
    print_warning "Message Queue Service health check timed out (may still be starting)"
else
    print_status "Message Queue Service is healthy"
fi

# Step 6: Start application services
echo ""
echo "📋 Step 5: Starting application services..."
echo "   Starting Scenario Library Service..."
docker-compose up -d scenario-library-service
sleep 5
print_status "Scenario Library Service started"

echo ""
echo "   Starting Tracks Management Service..."
docker-compose up -d tracks-management-service
sleep 5
print_status "Tracks Management Service started"

echo ""
echo "   Starting Webhook Management Service..."
docker-compose up -d webhook-management-service
sleep 8
print_status "Webhook Management Service started (with latest fixes)"

echo ""
echo "   Starting Evaluation Service..."
docker-compose up -d evaluation-service
sleep 5
print_status "Evaluation Service started"

# Step 7: Start gateway and UI
echo ""
echo "📋 Step 6: Starting gateway and UI..."
echo "   Starting API Gateway..."
docker-compose up -d dco-gateway
sleep 5
print_status "API Gateway started"

echo ""
echo "   Starting Developer Console UI..."
docker-compose up -d developer-console-ui
sleep 3
print_status "Developer Console UI started"

# Step 8: Start monitoring services
echo ""
echo "📋 Step 7: Starting monitoring services..."
docker-compose up -d pgadmin prometheus pushgateway grafana
sleep 3
print_status "Monitoring services started (pgAdmin, Prometheus, Pushgateway, Grafana)"

# Step 9: Verify all services are running
echo ""
echo "📋 Step 8: Verifying all services..."
echo ""

SERVICES=(
    "postgres:5432:PostgreSQL"
    "rabbitmq:5672:RabbitMQ"
    "redis:6379:Redis"
    "minio:9000:MinIO"
    "message-queue-service:8083:Message Queue Service"
    "scenario-library-service:8082:Scenario Library Service"
    "tracks-management-service:8081:Tracks Management Service"
    "webhook-management-service:8084:Webhook Management Service"
    "evaluation-service:8085:Evaluation Service"
    "dco-gateway:8080:API Gateway"
    "developer-console-ui:3000:Developer Console UI"
    "prometheus:9090:Prometheus"
    "pushgateway:9091:Prometheus Pushgateway"
    "grafana:3001:Grafana"
    "pgadmin:5050:pgAdmin"
)

ALL_RUNNING=true

for SERVICE_INFO in "${SERVICES[@]}"; do
    IFS=':' read -r CONTAINER PORT NAME <<< "$SERVICE_INFO"
    if docker ps | grep -q "$CONTAINER"; then
        print_status "$NAME is running (port $PORT)"
    else
        print_error "$NAME is NOT running"
        ALL_RUNNING=false
    fi
done

echo ""
echo "========================================="
if [ "$ALL_RUNNING" = true ]; then
    echo -e "${GREEN}🎉 All services started successfully!${NC}"
else
    print_warning "Some services failed to start. Check docker logs for details."
fi

# Step 10: Display service URLs
echo ""
echo "📋 Service Endpoints:"
echo "========================================="
echo "🌐 Developer Console UI:     http://localhost:3000"
echo "🔌 API Gateway (GraphQL):    http://localhost:8080"
echo "📚 Scenario Library Service: http://localhost:8082"
echo "🛤️  Tracks Management:       http://localhost:8081"
echo "📬 Message Queue Service:    http://localhost:8083"
echo "🪝 Webhook Management:       http://localhost:8084"
echo "⚙️  Evaluation Service:      http://localhost:8085"
echo "🐰 RabbitMQ Management UI:   http://localhost:15672 (admin/admin123)"
echo "💾 pgAdmin:                  http://localhost:5050 (admin@default.com/admin)"
echo "📊 Prometheus:               http://localhost:9090"
echo "📤 Prometheus Pushgateway:   http://localhost:9091"
echo "📈 Grafana:                  http://localhost:3001 (admin/admin)"
echo "🗄️  MinIO Console:            http://localhost:9001 (minioadmin/minioadmin)"
echo ""

# Step 11: Check webhook service logs for any errors
echo "📋 Step 9: Checking webhook service health..."
if docker logs webhook-management-service --tail 20 2>&1 | grep -q "Started WebhookManagementServiceApplication"; then
    print_status "Webhook service started successfully with latest fixes"
else
    print_warning "Webhook service may still be initializing, check logs if needed"
fi

# Step 12: Display RabbitMQ queue status
echo ""
echo "📋 Step 10: Checking RabbitMQ queues..."
sleep 2
if curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F > /dev/null 2>&1; then
    QUEUE_COUNT=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F | jq '. | length')
    print_status "RabbitMQ has $QUEUE_COUNT queues configured"
else
    print_warning "Could not verify RabbitMQ queues (may still be initializing)"
fi

# Step 13: Check and seed database if empty
echo ""
echo "📋 Step 11: Checking database..."
if docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;" > /dev/null 2>&1; then
    SCENARIO_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenario;" | xargs)
    
    if [ "$SCENARIO_COUNT" -eq "0" ]; then
        print_warning "Database is empty - seeding with sample data..."
        echo ""
        
        # Run seed script
        if [ -f "./scripts/database/seed-database.sh" ]; then
            chmod +x ./scripts/database/seed-database.sh
            ./scripts/database/seed-database.sh
            
            # Verify seeding was successful
            NEW_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenario;" | xargs)
            if [ "$NEW_COUNT" -gt "0" ]; then
                print_status "Database seeded successfully! ($NEW_COUNT scenarios created)"
            else
                print_error "Database seeding failed"
            fi
        else
            print_error "Seed script not found at ./scripts/database/seed-database.sh"
        fi
    else
        print_status "Database has $SCENARIO_COUNT scenarios (already populated)"
    fi
else
    print_warning "Could not verify database (may still be initializing)"
fi

# Step 14: Check and seed webhooks if empty
echo ""
echo "📋 Step 12: Checking webhooks..."
if docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM webhooks;" > /dev/null 2>&1; then
    WEBHOOK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhooks;" | xargs)
    
    if [ "$WEBHOOK_COUNT" -eq "0" ]; then
        print_warning "No webhooks configured - seeding default webhook..."
        echo ""
        
        # Insert default webhook directly
        docker exec -i postgres psql -U postgres <<'WEBHOOKSQL'
INSERT INTO webhooks (id, name, description, url, is_active) 
VALUES ('12345678-1234-1234-1234-123456789012'::uuid, 'E2E Test Webhook', 'Default webhook for E2E testing', 'https://webhook.site/475629c1-a5f1-40f5-a10a-9b18d18a8ea4', true) 
ON CONFLICT (name) DO NOTHING;

INSERT INTO webhook_event_types (webhook_id, event_type) VALUES
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.started'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.completed'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.failed'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'simulation.created'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'scenario.created'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'track.selected'),
    ('12345678-1234-1234-1234-123456789012'::uuid, 'track.created')
ON CONFLICT DO NOTHING;
WEBHOOKSQL
        
        # Verify webhook was created
        NEW_WEBHOOK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhooks;" 2>/dev/null | xargs || echo "0")
        if [ "$NEW_WEBHOOK_COUNT" -gt "0" ]; then
            print_status "Default webhook configured successfully!"
        else
            print_warning "Webhook configuration may have failed"
        fi
    else
        print_status "Webhooks configured ($WEBHOOK_COUNT active)"
    fi
else
    print_warning "Could not verify webhooks (may still be initializing)"
fi

echo ""
echo "========================================="
echo -e "${GREEN}✅ Startup Complete!${NC}"
echo ""

# Step 15: Run E2E Demo with Evaluation
echo "========================================="
echo -e "${GREEN}🎬 Running E2E Demo with Evaluation...${NC}"
echo "========================================="
echo ""
echo "This will:"
echo "   1. Create a test scenario"
echo "   2. Create a simulation"
echo "   3. Publish events to RabbitMQ"
echo "   4. Deliver webhooks"
echo "   5. ⭐ Automatically trigger evaluation"
echo "   6. Display evaluation results"
echo ""
sleep 3

if [ -f "./run-e2e-demo.sh" ]; then
    chmod +x ./run-e2e-demo.sh
    ./run-e2e-demo.sh
    
    echo ""
    echo "========================================="
    echo -e "${GREEN}✅ E2E Demo Complete!${NC}"
    echo "========================================="
else
    print_error "E2E demo script not found at ./run-e2e-demo.sh"
    echo ""
    echo "💡 You can manually run the E2E demo later with:"
    echo "   ./run-e2e-demo.sh"
fi

echo ""
echo "💡 Next Steps:"
echo "   1. Visit http://localhost:3000 to access the UI"
echo "   2. Login with any role (developer, team-lead, manager)"
echo "   3. View evaluation reports at http://localhost:3000/dco/reports"
echo "   4. Create custom rules at http://localhost:3000/dco/evaluationRules"
echo "   5. Monitor RabbitMQ: http://localhost:15672"
echo ""
echo "📚 Database Status:"
FINAL_SCENARIO_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM scenario;" 2>/dev/null | xargs || echo "0")
FINAL_TRACK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM track;" 2>/dev/null | xargs || echo "0")
FINAL_WEBHOOK_COUNT=$(docker exec postgres psql -U postgres -d postgres -t -c "SELECT COUNT(*) FROM webhooks;" 2>/dev/null | xargs || echo "0")
echo "   📊 Scenarios: $FINAL_SCENARIO_COUNT"
echo "   🛣️  Tracks: $FINAL_TRACK_COUNT"
echo "   🪝 Webhooks: $FINAL_WEBHOOK_COUNT"
echo ""
echo "📚 Documentation:"
echo "   - README.md - Main project documentation"
echo "   - LICENSE.md - Project license"
echo ""
echo "🛑 To stop all services: docker-compose down"
echo "🔄 To restart a service: docker-compose restart [service-name]"
echo ""
