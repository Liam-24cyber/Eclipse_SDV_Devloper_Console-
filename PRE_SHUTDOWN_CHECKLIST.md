# 🎯 PRE-SHUTDOWN CHECKLIST - DO THIS NOW!

**Current Time:** Right now, before you leave  
**Purpose:** Ensure clean shutdown and perfect restart tomorrow

---

## ✅ **Step 1: Verify Current State (2 minutes)**

### **1.1 Check All Services Running**
```bash
docker-compose ps
```

**Expected:** All 13 services showing "Up" or "Up (healthy)" ✅

**Current Status:** 
```
✅ dco-gateway                  - Up 4 hours
✅ developer-console-ui         - Up 11 hours  
✅ grafana                      - Up 11 hours
✅ message-queue-service        - Up 3 hours (healthy)
✅ minio                        - Up 11 hours
✅ pgadmin                      - Up 11 hours
✅ postgres                     - Up 53 minutes (healthy)
✅ prometheus                   - Up 7 hours
✅ rabbitmq                     - Up 11 hours (healthy)
✅ redis                        - Up 11 hours
✅ scenario-library-service     - Up 4 hours
✅ tracks-management-service    - Up 7 hours
✅ webhook-management-service   - Up 48 minutes (healthy)
```

**VERDICT:** 🟢 ALL SYSTEMS OPERATIONAL

---

### **1.2 Check Docker Volumes Exist**
```bash
docker volume ls | grep eclipse_sdv
```

**Expected:** You should see ALL 7 volumes:
- ✅ `postgres-data` - Database persistence
- ✅ `rabbitmq-data` - Message queue persistence  
- ✅ `redis-data` - Cache persistence
- ✅ `minio-data` - File storage persistence
- ✅ `pgadmin-data` - Database UI persistence
- ✅ `prometheus-data` - Metrics persistence
- ✅ `grafana-data` - Dashboard persistence

**Run this to verify:**
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
docker volume ls | grep eclipse_sdv
```

---

### **1.3 Quick E2E Smoke Test (Optional - 30 seconds)**

If you want to be 100% sure webhooks still work:

```bash
# Test webhook delivery
./publish-test-event.sh

# Check logs for successful delivery
docker-compose logs --tail=20 webhook-management-service | grep "Webhook delivery successful"
```

**Expected:** You see "Webhook delivery successful with status code: 200"

---

## 🛑 **Step 2: Shutdown Procedure (30 seconds)**

### **Option A: Clean Shutdown (RECOMMENDED)**
This stops all services but keeps ALL data:

```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
docker-compose down
```

**What happens:**
- ✅ All containers stop gracefully
- ✅ Networks removed
- ✅ **ALL DATA PERSISTS** in volumes
- ✅ Docker images stay cached (no rebuild tomorrow)

---

### **Option B: Keep Everything Running (Alternative)**
If you prefer to leave everything running overnight:

```bash
# Do nothing - just close your laptop
```

**Pros:**
- Zero startup time tomorrow
- Services keep running

**Cons:**
- Uses system resources overnight
- Logs grow larger

**Recommendation:** Go with Option A (shutdown) - it's cleaner and startup is only 2-3 minutes tomorrow.

---

## 📋 **Step 3: Verify Volumes After Shutdown**

After running `docker-compose down`, verify data is preserved:

```bash
# Should still see all 7 volumes
docker volume ls | grep eclipse_sdv
```

**Expected output:**
```
local     eclipse_sdv_devloper_console-_grafana-data
local     eclipse_sdv_devloper_console-_minio-data
local     eclipse_sdv_devloper_console-_pgadmin-data
local     eclipse_sdv_devloper_console-_postgres-data
local     eclipse_sdv_devloper_console-_prometheus-data
local     eclipse_sdv_devloper_console-_rabbitmq-data
local     eclipse_sdv_devloper_console-_redis-data
```

**If you see all 7:** 🟢 Perfect! All data will be there tomorrow.

---

## 🌅 **Tomorrow Morning - One Command**

```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./start-all-services.sh
```

**Wait 2-3 minutes, then:**
- Open http://localhost:3000 (Developer Console UI)
- Open http://localhost:15672 (RabbitMQ - admin/admin123)
- Open http://localhost:5050 (PgAdmin - admin@default.com/admin)

**Everything will be exactly as you left it!** ✨

---

## 🔍 **What to Verify Tomorrow Morning**

After startup, run these quick checks (1 minute):

```bash
# 1. All services up
docker-compose ps

# 2. Volumes still exist
docker volume ls | grep eclipse_sdv

# 3. Database has your data
docker-compose exec postgres psql -U postgres -c "SELECT COUNT(*) FROM webhook_subscriptions;"

# 4. RabbitMQ queues exist
curl -s -u admin:admin123 http://localhost:15672/api/queues | grep -o '"name":"[^"]*"' | head -10
```

**Expected results:**
1. All 13 services "Up" or "Up (healthy)"
2. All 7 volumes listed
3. Webhook count > 0 (your test webhook)
4. Queues: scenario.events, track.events, simulation.events

---

## 🚨 **Emergency Contact Info**

If something doesn't work tomorrow:

### **Scenario 1: Service won't start**
```bash
docker-compose logs [service-name]
docker-compose restart [service-name]
```

### **Scenario 2: Data seems missing**
```bash
# Check volumes exist
docker volume ls | grep eclipse_sdv

# Inspect specific volume
docker volume inspect eclipse_sdv_devloper_console-_postgres-data
```

### **Scenario 3: Complete fresh start needed**
```bash
# Nuclear option - deletes all data!
docker-compose down -v
./start-all-services.sh
```

---

## 📦 **What's Preserved vs What's Recreated**

### **PRESERVED (Survives shutdown):**
- ✅ PostgreSQL database (all tables, data)
- ✅ RabbitMQ queues and messages
- ✅ Redis cached data
- ✅ MinIO uploaded files
- ✅ PgAdmin connections and queries
- ✅ Prometheus metrics history
- ✅ Grafana dashboards
- ✅ Docker images (no rebuild needed)

### **RECREATED (Fresh on startup):**
- 🔄 Container processes
- 🔄 Container networks
- 🔄 Container logs (reset to empty)

---

## ✅ **Final Pre-Shutdown Checklist**

Run through this list:

- [ ] All 13 services are "Up" (`docker-compose ps`)
- [ ] All 7 volumes exist (`docker volume ls | grep eclipse_sdv`)
- [ ] You have the updated `docker-compose.yml` (with volume mounts)
- [ ] You have `start-all-services.sh` script ready
- [ ] You've read `READY_FOR_TOMORROW.md`
- [ ] You're ready to run `docker-compose down`

**If all checked:** You're good to go! 🎉

---

## 🎬 **Execute Shutdown Now**

When ready, run:

```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
docker-compose down
```

**Expected output:**
```
[+] Running 14/14
 ✔ Container developer-console-ui       Removed
 ✔ Container dco-gateway                Removed  
 ✔ Container webhook-management-service Removed
 ✔ Container scenario-library-service   Removed
 ✔ Container tracks-management-service  Removed
 ✔ Container message-queue-service      Removed
 ✔ Container grafana                    Removed
 ✔ Container prometheus                 Removed
 ✔ Container pgadmin                    Removed
 ✔ Container rabbitmq                   Removed
 ✔ Container postgres                   Removed
 ✔ Container redis                      Removed
 ✔ Container minio                      Removed
 ✔ Network services                     Removed
```

**Duration:** ~30 seconds

---

## 🌟 **You're All Set!**

Tomorrow morning:
1. ☕ Get your coffee
2. 💻 Open terminal
3. 🚀 Run `./start-all-services.sh`
4. ⏱️ Wait 2-3 minutes
5. 🎉 Everything works!

**See you in the morning! 👋**

---

**P.S.** Don't forget to check `READY_FOR_TOMORROW.md` for the complete guide!
