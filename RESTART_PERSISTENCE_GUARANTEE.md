# 🔄 **RESTART PERSISTENCE GUARANTEE**

## ✅ **GUARANTEED: Everything Persists After Restart**

This document explains **EXACTLY** what happens when you:
```bash
docker compose down
docker compose up -d
```

---

## 📊 **DATA PERSISTENCE MATRIX**

| Component | Storage Location | Persists? | Volume Mount | Notes |
|-----------|-----------------|-----------|--------------|-------|
| **PostgreSQL Database** | `/var/lib/postgresql/data` | ✅ YES | `postgres-data` | All tables & data persist |
| **RabbitMQ Queues** | `/var/lib/rabbitmq` | ✅ YES | `rabbitmq-data` | Queues, bindings, exchanges persist |
| **Redis Cache** | `/data` | ✅ YES | `redis-data` | Cache entries persist (AOF enabled) |
| **MinIO Files** | `/data` | ✅ YES | `minio-data` | Uploaded scenario files persist |
| **PgAdmin Config** | `/var/lib/pgadmin` | ✅ YES | `pgadmin-data` | Server connections persist |
| **Prometheus Metrics** | `/prometheus` | ✅ YES | `prometheus-data` | Historical metrics persist |
| **Grafana Dashboards** | `/var/lib/grafana` | ✅ YES | `grafana-data` | Dashboards & settings persist |

---

## 🗄️ **POSTGRESQL DATABASE - WHAT PERSISTS**

### **After Restart, You Will See:**

#### ✅ **All Tables (Auto-created by JPA/Hibernate on first run):**
```sql
-- Scenario Library Service Tables
scenarios
tracks
simulations
scenario_files
track_segments

-- Webhook Management Service Tables
webhooks
webhook_headers
webhook_deliveries
webhook_delivery_attempts

-- Other Tables
schema_version (Flyway migrations)
```

#### ✅ **All Data Records:**
- **16 scenarios** (demo scenarios from seed data)
- **3+ active webhook subscriptions**
- **All webhook delivery records**
- **All tracks, simulations, and their relationships**

### **What You DON'T Need to Do:**
- ❌ Recreate tables (JPA creates them automatically on first startup)
- ❌ Re-seed scenarios (they're already in the database)
- ❌ Re-create webhook subscriptions
- ❌ Re-run database migrations

### **Verification After Restart:**
```bash
# Check database contents
docker exec -it postgres psql -U postgres -d postgres -c "\dt"
docker exec -it postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenarios;"
docker exec -it postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM webhooks;"
docker exec -it postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM webhook_deliveries;"
```

---

## 🐰 **RABBITMQ QUEUES - WHAT PERSISTS**

### **After Restart, You Will See:**

#### ✅ **All Queues:**
```
webhook.events              (Main queue for webhook events)
webhook.events.dlq          (Dead Letter Queue)
simulation.events           (If used)
track.events               (If used)
```

#### ✅ **All Exchanges:**
```
webhook.exchange           (Direct exchange for webhook routing)
amq.direct                 (Default exchange)
```

#### ✅ **All Bindings:**
```
webhook.exchange → webhook.events (routing key: webhook.events)
```

#### ✅ **All Messages:**
- Messages in queues persist (RabbitMQ uses disk storage)
- DLQ messages persist (need manual purging if undesired)

### **What You DON'T Need to Do:**
- ❌ Recreate queues (RabbitMQ restores them from `/var/lib/rabbitmq`)
- ❌ Recreate exchanges
- ❌ Recreate bindings
- ❌ Re-configure RabbitMQ users/permissions

### **Verification After Restart:**
```bash
# Check RabbitMQ queues
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[].name'

# Check message counts
curl -u admin:admin123 http://localhost:15672/api/queues/%2F/webhook.events | jq '{messages, consumers}'
```

---

## 📦 **MINIO FILES - WHAT PERSISTS**

### **After Restart, You Will See:**

#### ✅ **All Buckets:**
```
dco-scenario-library-service    (Main bucket for scenario files)
```

#### ✅ **All Uploaded Files:**
- Scenario definition files
- Software packages
- Track data files
- Any other uploaded artifacts

### **What You DON'T Need to Do:**
- ❌ Recreate buckets
- ❌ Re-upload files

### **Verification After Restart:**
```bash
# Check MinIO buckets and files
docker exec -it minio mc ls local/dco-scenario-library-service/
```

---

## 🔄 **RESTART SCENARIOS**

### **Scenario 1: Normal Restart (Data Persists)**
```bash
# Stop all services
docker compose down

# Start all services
docker compose up -d

# ✅ Result: All data, queues, files persist!
```

### **Scenario 2: Rebuild Services (Data Persists)**
```bash
# Rebuild and restart services
docker compose up -d --build

# ✅ Result: All data, queues, files persist!
# Note: Code changes applied, but data remains intact
```

### **Scenario 3: Nuclear Option (Data LOST)**
```bash
# DANGER: This deletes ALL data!
docker compose down -v

# ❌ Result: All volumes deleted, data lost!
# You'll need to re-seed scenarios and webhooks
```

---

## 🚀 **STARTUP SEQUENCE (Automatic)**

The `docker-compose.yml` has dependency management:

```yaml
1. postgres      → Starts first (healthcheck: pg_isready)
2. rabbitmq      → Starts second (healthcheck: rabbitmq-diagnostics)
3. redis         → Starts third
4. minio         → Starts fourth
5. pgadmin       → Depends on postgres

6. message-queue-service        → Depends on rabbitmq (healthy)
7. webhook-management-service   → Depends on postgres + rabbitmq (healthy)
8. scenario-library-service     → Depends on postgres + message-queue-service
9. tracks-management-service    → Depends on postgres
10. dco-gateway                 → Depends on redis
11. developer-console-ui        → Depends on dco-gateway
```

**All services will wait for dependencies before starting.**

---

## 🧪 **POST-RESTART VERIFICATION CHECKLIST**

### **1. Check All Services Are Running:**
```bash
docker compose ps
```

**Expected Output:**
```
✅ All services: Up (healthy)
```

### **2. Check Database Data:**
```bash
# Check scenarios
curl http://localhost:8082/scenarios | jq length
# Expected: 16

# Check webhooks
curl http://localhost:8084/webhooks | jq length
# Expected: 3+

# Check deliveries
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM webhook_deliveries;"
# Expected: Previous delivery count
```

### **3. Check RabbitMQ Queues:**
```bash
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[].name'
```

**Expected:**
```json
[
  "webhook.events",
  "webhook.events.dlq"
]
```

### **4. Check MinIO Files:**
```bash
docker exec -it minio mc ls local/dco-scenario-library-service/
```

**Expected:** All previously uploaded files

---

## 📋 **COMMON QUESTIONS**

### **Q1: Do I need to run seed scripts after restart?**
❌ **NO!** All data persists in PostgreSQL volume.

### **Q2: Will RabbitMQ queues be recreated automatically?**
✅ **YES!** RabbitMQ restores queues from `/var/lib/rabbitmq` volume.

### **Q3: Will webhook subscriptions disappear?**
❌ **NO!** They're stored in PostgreSQL and persist.

### **Q4: Will delivery history be lost?**
❌ **NO!** All `webhook_deliveries` records persist in PostgreSQL.

### **Q5: What if I rebuild a service (docker compose up --build)?**
✅ **Data persists!** Volumes are independent of container images.

### **Q6: What if I want to start fresh?**
```bash
# Delete all volumes and data
docker compose down -v

# Rebuild and restart
docker compose up -d --build

# Re-seed scenarios (if needed)
./seed-database.sh

# Re-create webhooks (if needed)
./seed-test-webhook.sh
```

---

## ⚠️ **ONLY WAY TO LOSE DATA**

You will **ONLY** lose data if you:

1. **Run `docker compose down -v`** (deletes volumes)
2. **Manually delete volumes:**
   ```bash
   docker volume rm eclipse_sdv_devloper_console-_postgres-data
   docker volume rm eclipse_sdv_devloper_console-_rabbitmq-data
   ```
3. **Delete the entire Docker data directory**

---

## 🎯 **PRODUCTION DEPLOYMENT NOTES**

### **Backup Strategy:**
```bash
# Backup PostgreSQL
docker exec -it postgres pg_dump -U postgres postgres > backup.sql

# Backup volumes
docker run --rm \
  -v eclipse_sdv_devloper_console-_postgres-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup.tar.gz /data
```

### **Restore Strategy:**
```bash
# Restore PostgreSQL
cat backup.sql | docker exec -i postgres psql -U postgres postgres

# Restore volumes
docker run --rm \
  -v eclipse_sdv_devloper_console-_postgres-data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/postgres-backup.tar.gz -C /
```

---

## ✅ **FINAL ANSWER**

### **After `docker compose down && docker compose up -d`:**

| Data Type | Status | Location |
|-----------|--------|----------|
| **Scenarios (16)** | ✅ Persists | PostgreSQL → `postgres-data` volume |
| **Tracks** | ✅ Persists | PostgreSQL → `postgres-data` volume |
| **Simulations** | ✅ Persists | PostgreSQL → `postgres-data` volume |
| **Webhooks (3+)** | ✅ Persists | PostgreSQL → `postgres-data` volume |
| **Webhook Deliveries** | ✅ Persists | PostgreSQL → `postgres-data` volume |
| **RabbitMQ Queues** | ✅ Persists | RabbitMQ → `rabbitmq-data` volume |
| **Queue Bindings** | ✅ Persists | RabbitMQ → `rabbitmq-data` volume |
| **Messages in Queues** | ✅ Persists | RabbitMQ → `rabbitmq-data` volume |
| **MinIO Files** | ✅ Persists | MinIO → `minio-data` volume |
| **Redis Cache** | ✅ Persists | Redis → `redis-data` volume |

### **YOU NEED TO DO:**
1. ✅ `docker compose up -d` (that's it!)

### **YOU DON'T NEED TO DO:**
- ❌ Recreate database tables
- ❌ Re-seed scenarios
- ❌ Re-create webhooks
- ❌ Recreate RabbitMQ queues
- ❌ Re-upload files to MinIO
- ❌ Any manual configuration

---

## 🎉 **GUARANTEE**

**Your system is production-ready with full data persistence!**

Every restart will:
- ✅ Preserve all database records
- ✅ Preserve all RabbitMQ queues and messages
- ✅ Preserve all uploaded files
- ✅ Restore all service configurations
- ✅ Maintain all webhooks and delivery history

**No manual intervention required!**
