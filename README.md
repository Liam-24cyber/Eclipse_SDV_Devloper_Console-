# Eclipse SDV Developer Console

> Complete E2E Platform for Software-Defined Vehicle Development

## 🚀 Quick Start

```bash
# Start all services with auto-seeding
./start-all-services.sh

# Check if services are ready
./check-demo-readiness.sh

# Run complete E2E demo
./run-e2e-demo.sh
```

## 📋 Prerequisites

- Docker & Docker Compose
- Node.js (for E2E API server)
- Port availability: 3000, 5432, 5672, 8080-8087, 9090, 9191, 15672

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  Developer UI   │───▶│   API Gateway    │───▶│   PostgreSQL    │
│   (Port 3000)   │    │   (Port 8080)    │    │   (Port 5432)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌──────────────────┐
                       │    RabbitMQ      │
                       │  (Port 5672)     │
                       └──────────────────┘
                              │
                              ▼
                       ┌──────────────────┐
                       │ Webhook Service  │
                       │  (Port 8085)     │
                       └──────────────────┘
```

## 🔧 Services & Ports

| Service | Port | Purpose |
|---------|------|---------|
| Developer Console UI | 3000 | Frontend Application |
| DCO Gateway | 8080 | GraphQL API Gateway |
| Scenario Library Service | 8081 | Scenario Management |
| Tracks Management Service | 8082 | Track Management |
| Simulation Service | 8083 | Simulation Execution |
| Results Service | 8084 | Test Results |
| Webhook Service | 8085 | Webhook Delivery |
| Message Queue Service | 8087 | Event Routing |
| E2E API Server | 9191 | REST API for Automation |
| PostgreSQL | 5432 | Database |
| RabbitMQ | 5672 | Message Broker |
| RabbitMQ Management | 15672 | RabbitMQ Web UI |
| Prometheus | 9090 | Metrics |

## 🔐 Default Credentials

### Database (PostgreSQL)
```
Host: localhost:5432
Database: postgres
Username: postgres
Password: postgres
```

### RabbitMQ
```
Management UI: http://localhost:15672
Username: admin
Password: admin123
```

### Application
```
Default User: admin
Password: admin
```

## 📡 Key Endpoints

### GraphQL Gateway
```
http://localhost:8080/graphql
```

### REST API (E2E Automation)
```
http://localhost:9191

Health Check:     GET  /health
System Status:    GET  /api/status
Create Scenario:  POST /api/scenario/create
Get Tracks:       GET  /api/tracks
Publish Event:    POST /api/event/publish
Webhook Status:   GET  /api/webhooks/deliveries
Run E2E Workflow: POST /api/e2e/run
```

### RabbitMQ Management
```
http://localhost:15672
```

### Prometheus
```
http://localhost:9090
```

## 🎯 E2E Workflow

### Automated E2E Test (All Steps)
```bash
curl -X POST http://localhost:9191/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "Demo Scenario",
    "scenarioDescription": "E2E Test"
  }'
```

### Manual Steps

**1. Create Scenario**
```bash
curl -X POST http://localhost:9191/api/scenario/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Scenario", "description": "My test"}'
```

**2. Get Tracks**
```bash
curl http://localhost:9191/api/tracks
```

**3. Publish Event**
```bash
curl -X POST http://localhost:9191/api/event/publish \
  -H "Content-Type: application/json" \
  -d '{"scenarioId": "YOUR_SCENARIO_ID", "eventType": "SCENARIO_CREATED"}'
```

**4. Check Webhooks**
```bash
curl http://localhost:9191/api/webhooks/deliveries
```

## 🛠️ Management Scripts

### Startup/Shutdown
```bash
./start-all-services.sh      # Start all services + auto-seed DB
./check-demo-readiness.sh    # Verify all services are ready
./30-destroy-script.sh       # Stop and remove all containers
```

### Demo Automation
```bash
./open-demo-tabs.sh          # Open all UI tabs in browser
./run-e2e-demo.sh            # Execute complete E2E workflow
./start-e2e-api.sh           # Start REST API server
```

### Database
```bash
./seed-database.sh           # Manually seed database
./seed-default-webhook.sh    # Add demo webhook
```

### RabbitMQ
```bash
./fix-rabbitmq-queues.sh     # Fix queue configuration
./purge-dlqs.sh              # Clear dead-letter queues
```

### Monitoring
```bash
./check-status.sh            # Check service status
./show-urls.sh               # Display all service URLs
```

## 📊 Postman Collection

Import `E2E_Demo_API.postman_collection.json` for ready-to-use API tests.

## 🔍 Troubleshooting

### Services Not Starting
```bash
# Check Docker status
docker ps

# View logs
docker-compose logs -f SERVICE_NAME

# Restart specific service
docker-compose restart SERVICE_NAME
```

### Database Issues
```bash
# Check database connection
docker exec postgres psql -U postgres -c "SELECT version();"

# Re-seed database
./seed-database.sh
```

### RabbitMQ Issues
```bash
# Check queue status
curl http://localhost:9191/api/rabbitmq/queues

# Fix queues
./fix-rabbitmq-queues.sh
```

### Webhook Not Delivering
```bash
# Check webhook configuration
curl http://localhost:9191/api/webhooks/deliveries

# Seed demo webhook
./seed-default-webhook.sh
```

## 📈 Monitoring

### Health Checks
```bash
# All services status
curl http://localhost:9191/api/status

# Individual service health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### Prometheus Metrics
```
http://localhost:9090
```

### RabbitMQ Metrics
```
http://localhost:15672/#/queues
```

## 🗄️ Database Schema

Key tables:
- `scenario` - Test scenarios
- `track` - Test tracks
- `simulation` - Simulation runs
- `webhooks` - Webhook configurations
- `webhook_deliveries` - Delivery logs
- `webhook_event_subscriptions` - Event subscriptions

## 🔄 Data Persistence

All data persists across restarts via Docker volumes:
- `postgres_data` - Database
- `rabbitmq_data` - Message queue
- `prometheus_data` - Metrics

## 🧪 Testing

### Run E2E Test
```bash
./run-e2e-demo.sh
```

### Test with cURL
```bash
# Complete guide
cat COMPLETE_CURL_REFERENCE.md
```

### Test with Postman
```bash
# Import collection
E2E_Demo_API.postman_collection.json
```

## 📚 Documentation

- `QUICK_START.md` - Getting started guide
- `DEMO_RECORDING_GUIDE.md` - Recording demos
- `POSTMAN_E2E_GUIDE.md` - Postman automation
- `COMPLETE_CURL_REFERENCE.md` - All cURL commands
- `SERVICE_URLS.md` - All service endpoints

## 🛡️ Security Notes

⚠️ **Default credentials are for development only!**

Change in production:
- Database passwords
- RabbitMQ credentials  
- Application secrets


## 📝 License

See LICENSE.md



---

**Ready to start?** Run `./start-all-services.sh` and visit http://localhost:3000
