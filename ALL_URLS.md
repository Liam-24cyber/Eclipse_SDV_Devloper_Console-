# 🌐 All Service URLs - Quick Reference

## Open in Browser (Click Links)

### 🎯 **Main Application**
| Service | URL | Description |
|---------|-----|-------------|
| **Developer Console UI** | http://localhost:3000 | React frontend application |
| **GraphQL Playground** | http://localhost:8080/graphql | API Gateway GraphQL endpoint |

---

### 🔧 **Microservices (Swagger/OpenAPI)**
| Service | URL | Purpose |
|---------|-----|---------|
| **Scenario Service** | http://localhost:8081/swagger-ui.html | Manage test scenarios |
| **Tracks Service** | http://localhost:8082/swagger-ui.html | Manage test tracks |
| **Message Queue Service** | http://localhost:8083/swagger-ui.html | RabbitMQ integration & event publishing |
| **Webhook Service** | http://localhost:8084/swagger-ui.html | Process events and store to DB |

---

### 📊 **Infrastructure & Monitoring**
| Service | URL | Login | Purpose |
|---------|-----|-------|---------|
| **RabbitMQ Management** | http://localhost:15672 | admin / admin123 | Monitor queues, exchanges, messages |
| **pgAdmin** | http://localhost:5050 | admin@sdv.com / admin123 | PostgreSQL database admin |
| **MinIO Console** | http://localhost:9001 | minioadmin / minioadmin | Object storage management |
| **Prometheus** | http://localhost:9090 | - | Metrics collection |
| **Grafana** | http://localhost:3001 | admin / admin | Metrics dashboards |

---

## 🧪 Test Complete Flow

### 1️⃣ Publish Event to RabbitMQ

```bash
curl -X POST http://localhost:8083/api/v1/events/publish \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "scenario.created",
    "payload": {
      "scenarioId": "test-001",
      "name": "Highway Test",
      "timestamp": "2025-11-03T19:00:00Z"
    }
  }'
```

### 2️⃣ Monitor in RabbitMQ
- **Open**: http://localhost:15672
- **Go to**: Queues tab
- **Check**: `scenario.events` queue

### 3️⃣ Verify in Database
- **Open**: http://localhost:5050
- **Connect to DB**: postgres / sdv_dco
- **Query**: `SELECT * FROM webhook_events ORDER BY created_at DESC;`

---

## 📋 Quick Health Check

```bash
# Run this to see all URLs
./show-urls.sh

# Or run E2E test
./test-e2e-flow.sh
```

---

## 🔗 Direct Queue Links

- **scenario.events**: http://localhost:15672/#/queues/%2F/scenario.events
- **track.events**: http://localhost:15672/#/queues/%2F/track.events  
- **simulation.events**: http://localhost:15672/#/queues/%2F/simulation.events
- **webhook.events**: http://localhost:15672/#/queues/%2F/webhook.events

---

## ✅ All Services Running

```bash
✓ RabbitMQ         http://localhost:15672
✓ Message Queue    http://localhost:8083
✓ Webhook Service  http://localhost:8084
✓ pgAdmin          http://localhost:5050
```

**Everything is ready for testing! 🚀**
