# 🎯 **RESTART QUICK REFERENCE CARD**

## 🚀 **Normal Restart (Data Persists)**

```bash
# Stop all services
docker compose down

# Start all services
docker compose up -d

# Verify everything persisted
./verify-restart-persistence.sh
```

**Result:** ✅ All data, queues, files persist!

---

## 🔨 **Rebuild Services (Data Still Persists)**

```bash
# Rebuild with code changes + start
docker compose up -d --build

# Verify
./verify-restart-persistence.sh
```

**Result:** ✅ Code updated, data still intact!

---

## 💣 **Nuclear Reset (Deletes ALL Data)**

```bash
# ⚠️ DANGER: This deletes EVERYTHING!
docker compose down -v

# Rebuild + Start fresh
docker compose up -d --build

# Re-seed data
./seed-database.sh          # Optional: scenarios
./seed-test-webhook.sh      # Recreate test webhook
```

**Result:** ❌ All data lost, fresh start!

---

## 📦 **What Persists After Normal Restart**

| Data | Persists? | Volume |
|------|-----------|--------|
| Scenarios (16) | ✅ YES | `postgres-data` |
| Webhooks | ✅ YES | `postgres-data` |
| Deliveries | ✅ YES | `postgres-data` |
| Tracks | ✅ YES | `postgres-data` |
| Simulations | ✅ YES | `postgres-data` |
| RabbitMQ Queues | ✅ YES | `rabbitmq-data` |
| MinIO Files | ✅ YES | `minio-data` |
| Redis Cache | ✅ YES | `redis-data` |

---

## 🔍 **Quick Checks**

```bash
# Check all services
docker compose ps

# Check database data
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM scenarios;"
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM webhooks;"

# Check RabbitMQ queues
curl -u admin:admin123 http://localhost:15672/api/queues | jq '.[].name'

# Run full verification
./verify-restart-persistence.sh
```

---

## 🛑 **IMPORTANT: Only Lose Data If You...**

1. Run `docker compose down -v` (deletes volumes)
2. Manually delete volumes: `docker volume rm <volume-name>`
3. Delete `/var/lib/docker/volumes/` directory

**Otherwise, ALL data persists automatically!**

---

## 📋 **Typical Workflow**

### **Development:**
```bash
# Make code changes
# Rebuild affected service
docker compose up -d --build scenario-library-service

# ✅ Data persists!
```

### **Production Restart:**
```bash
docker compose down
docker compose up -d

# ✅ Zero downtime, all data intact
```

### **Fresh Start:**
```bash
docker compose down -v
docker compose up -d --build
./seed-database.sh
./seed-test-webhook.sh

# ✅ Clean slate
```

---

## ✅ **YOU'RE SAFE!**

Your configuration guarantees:
- ✅ Database survives restarts
- ✅ Queues survive restarts
- ✅ Files survive restarts
- ✅ No manual steps needed
- ✅ Production-ready persistence

**Just run `docker compose up -d` and you're good! 🎉**
