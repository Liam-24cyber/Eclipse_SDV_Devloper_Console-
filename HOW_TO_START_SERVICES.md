# 🚀 HOW TO START ALL SERVICES

**Quick Guide for Starting the SDV Developer Console**

---

## ✅ EASIEST WAY (Recommended)

### Single Command:
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./start-all-services.sh
```

That's it! The script will:
1. ✅ Check Docker is running
2. ✅ Build the latest images
3. ✅ Start all services in the correct order
4. ✅ Wait for health checks
5. ✅ Show you when everything is ready

⏱️ **Wait time:** 2-3 minutes for all services to be fully ready

---

## 📋 STEP-BY-STEP (What the Script Does)

### Step 1: Make sure Docker Desktop is running
- Open Docker Desktop application on your Mac
- Wait for it to show "Docker Desktop is running"

### Step 2: Navigate to the project
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
```

### Step 3: Run the startup script
```bash
./start-all-services.sh
```

The script will start services in this order:
1. **Infrastructure Layer:** PostgreSQL, Redis, RabbitMQ, MinIO
2. **Support Layer:** pgAdmin, Prometheus
3. **Backend Services:** API Gateway, Scenario Service, Track Service, Message Queue Service
4. **Event Consumer:** Webhook Management Service
5. **Monitoring:** Grafana

---

## 🔍 VERIFY EVERYTHING STARTED

### Option 1: Check All Containers
```bash
docker-compose ps
```

All services should show "Up" status.

### Option 2: Use the Readiness Checker
```bash
./check-demo-readiness.sh
```

Should show all ✅ green checkmarks.

### Option 3: Manual Health Checks

Open these URLs in your browser:

| Service | URL | Expected |
|---------|-----|----------|
| UI | http://localhost:3000 | Login page |
| pgAdmin | http://localhost:5050 | Login page |
| MinIO | http://localhost:9001 | Login page |
| RabbitMQ | http://localhost:15672 | Login page |
| Prometheus | http://localhost:9090 | Prometheus UI |
| Grafana | http://localhost:3001 | Login page |
| API Gateway | http://localhost:8080/actuator/health | `{"status":"UP"}` |

---

## 🆘 TROUBLESHOOTING

### Problem: "Docker is not running"
**Solution:** Start Docker Desktop application

### Problem: "Permission denied" when running script
**Solution:** Make script executable
```bash
chmod +x start-all-services.sh
./start-all-services.sh
```

### Problem: "Port already in use"
**Solution:** Stop the existing services first
```bash
docker-compose down
./start-all-services.sh
```

### Problem: Some services show "unhealthy" or "exited"
**Solution:** Restart those specific services
```bash
docker-compose restart <service-name>
```

Or restart everything:
```bash
docker-compose restart
```

### Problem: Services start but don't respond
**Solution:** Wait a bit longer (some services take 2-3 minutes)
```bash
# Check logs for a specific service
docker-compose logs -f <service-name>

# Example:
docker-compose logs -f webhook-management-service
```

---

## 🛑 HOW TO STOP SERVICES

### Normal Shutdown (Keeps Data)
```bash
docker-compose down
```
✅ All your data persists (databases, queues, files)

### Complete Cleanup (Removes Everything)
```bash
docker-compose down -v
```
⚠️ This removes all data volumes! Use only if you want a fresh start.

---

## 🔄 RESTART SERVICES

### Restart Everything
```bash
docker-compose restart
```

### Restart One Service
```bash
docker-compose restart <service-name>

# Examples:
docker-compose restart webhook-management-service
docker-compose restart postgres
docker-compose restart rabbitmq
```

---

## 📊 MONITORING SERVICES

### View All Running Containers
```bash
docker-compose ps
```

### View Logs for All Services
```bash
docker-compose logs -f
```

### View Logs for One Service
```bash
docker-compose logs -f <service-name>

# Example:
docker-compose logs -f webhook-management-service
```

### Check Resource Usage
```bash
docker stats
```

---

## 🎯 QUICK DEMO WORKFLOW

### For Demo Recording:
```bash
# 1. Start everything
./start-all-services.sh

# 2. Wait 2-3 minutes ☕

# 3. Check readiness
./check-demo-readiness.sh

# 4. Open all browser tabs
./open-demo-tabs.sh

# 5. You're ready to record! 🎬
```

---

## 📝 ALTERNATIVE: MANUAL DOCKER COMPOSE

If you prefer not to use the script:

### Start All Services
```bash
docker-compose up -d
```

### Start with Rebuild
```bash
docker-compose up -d --build
```

### Start and Watch Logs
```bash
docker-compose up
```
(Press Ctrl+C to stop)

---

## 🔑 SERVICE PORTS REFERENCE

| Service | Port | Protocol |
|---------|------|----------|
| UI | 3000 | HTTP |
| API Gateway | 8080 | HTTP |
| Scenario Service | 8081 | HTTP |
| Track Service | 8082 | HTTP |
| Message Queue Service | 8083 | HTTP |
| Webhook Management Service | 8084 | HTTP |
| PostgreSQL | 5432 | PostgreSQL |
| pgAdmin | 5050 | HTTP |
| RabbitMQ | 5672 | AMQP |
| RabbitMQ Management | 15672 | HTTP |
| Redis | 6379 | Redis |
| MinIO API | 9000 | S3 |
| MinIO Console | 9001 | HTTP |
| Prometheus | 9090 | HTTP |
| Grafana | 3001 | HTTP |

---

## ✅ SUCCESS INDICATORS

You'll know everything is working when:

1. ✅ `docker-compose ps` shows all services "Up"
2. ✅ All web UIs respond (3000, 5050, 9001, 15672, 9090, 3001)
3. ✅ `./check-demo-readiness.sh` shows all green
4. ✅ No error messages in logs
5. ✅ You can login to the UI at http://localhost:3000

---

## 🎬 READY FOR YOUR DEMO!

Once all services are started and healthy:
1. ✅ Follow **DEMO_RECORDING_GUIDE.md** for the recording script
2. ✅ Use **DEMO_QUICK_START.md** for quick reference
3. ✅ All your data persists across restarts (Docker volumes)

---

## 💡 PRO TIPS

### Tip 1: Check Status Anytime
```bash
docker-compose ps
```

### Tip 2: View Real-Time Logs
```bash
docker-compose logs -f webhook-management-service
```

### Tip 3: Restart Problem Services Quickly
```bash
docker-compose restart <service-name>
```

### Tip 4: Fresh Start Occasionally
```bash
docker-compose down -v
docker-compose up -d
./start-all-services.sh
```

### Tip 5: Save Current State Before Big Changes
```bash
# Stop without removing volumes
docker-compose down

# Your data is safe!
```

---

## 🚀 BOTTOM LINE

**To start everything for your demo:**

```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./start-all-services.sh
```

**Then wait 2-3 minutes and you're ready!** ✨

---

**Last updated:** November 4, 2025
