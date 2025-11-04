# 🚀 START ALL SERVICES - SUPER SIMPLE GUIDE

---

## ⚡ THE FASTEST WAY

### Copy and paste this into your terminal:

```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-" && ./start-all-services.sh
```

**That's it!** ✨

---

## 📖 STEP-BY-STEP (If You Prefer)

### Step 1: Open Terminal
- Press `Cmd + Space`
- Type "Terminal"
- Press Enter

### Step 2: Go to the Project Folder
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
```

### Step 3: Start Everything
```bash
./start-all-services.sh
```

### Step 4: Wait 2-3 Minutes ☕
You'll see services starting up with green checkmarks.

### Step 5: Verify Everything is Ready
```bash
./check-demo-readiness.sh
```

Should show all green ✅ checkmarks!

---

## 🌐 WHAT GETS STARTED

The script automatically starts **all 15 services** in the right order:

### Infrastructure (starts first):
- ✅ PostgreSQL (database)
- ✅ Redis (cache)
- ✅ RabbitMQ (message queue)
- ✅ MinIO (file storage)

### Support Tools:
- ✅ pgAdmin (database UI)
- ✅ Prometheus (metrics)

### Your Application:
- ✅ API Gateway
- ✅ Scenario Service
- ✅ Track Service  
- ✅ Message Queue Service
- ✅ Webhook Management Service
- ✅ UI (Developer Console)

### Monitoring:
- ✅ Grafana (dashboards)
- ✅ RabbitMQ Exporter

---

## ✅ HOW TO KNOW IT WORKED

### You'll See:
```
🚀 Starting SDV Developer Console Stack
=========================================

📋 Step 1: Checking prerequisites...
✅ Docker is running

📋 Step 2: Building Docker images...
✅ Webhook service image built with latest fixes

📋 Step 3: Starting infrastructure services...
✅ PostgreSQL started
✅ Redis started
✅ RabbitMQ started
✅ MinIO started

... (more green checkmarks)

🎉 ALL SERVICES STARTED SUCCESSFULLY!
```

### Quick Check:
Open http://localhost:3000 in your browser - you should see the login page!

---

## 🔍 VERIFY WITH THE CHECK SCRIPT

After starting, run:
```bash
./check-demo-readiness.sh
```

**Expected output:**
```
🎬 Demo Readiness Check
=======================

Checking services...

✅ UI (Developer Console) - Ready
✅ pgAdmin - Ready
✅ MinIO Console - Ready
✅ RabbitMQ Management - Ready
✅ Prometheus - Ready
✅ Grafana - Ready
✅ API Gateway - Ready

=======================
🎉 ALL SERVICES READY FOR DEMO!
```

---

## 🌐 OPEN ALL BROWSER TABS

After everything is ready:
```bash
./open-demo-tabs.sh
```

This automatically opens all 6 browser tabs you need for the demo!

---

## 🆘 IF SOMETHING GOES WRONG

### Error: "Docker is not running"
**Fix:** Open Docker Desktop app and wait for it to start
```bash
open -a Docker
# Wait 30 seconds, then try again
./start-all-services.sh
```

### Error: "Permission denied"
**Fix:** Scripts are now executable, but if you get this error:
```bash
chmod +x start-all-services.sh
./start-all-services.sh
```

### Error: "Port already in use"
**Fix:** Stop existing services first
```bash
docker-compose down
./start-all-services.sh
```

### Some services show errors
**Fix:** Check which service failed
```bash
docker-compose ps
# Look for any service that says "Exit 1" or similar

# Restart that specific service
docker-compose restart <service-name>
```

---

## 🛑 HOW TO STOP

### When you're done with the demo:
```bash
docker-compose down
```

✅ This stops all services but **keeps your data** (scenarios, webhooks, etc.)

### If you want to start completely fresh:
```bash
docker-compose down -v
```

⚠️ This removes **everything** including data!

---

## 🔄 HOW TO RESTART

### If services are already running and you want to restart:
```bash
docker-compose restart
```

### If you stopped with `docker-compose down`:
```bash
./start-all-services.sh
```

---

## 💡 COMMON SCENARIOS

### Scenario 1: First time starting
```bash
./start-all-services.sh
# Wait 2-3 minutes
./check-demo-readiness.sh
```

### Scenario 2: Starting after shutdown
```bash
./start-all-services.sh
# Your data is still there!
```

### Scenario 3: Something seems broken
```bash
docker-compose down
docker-compose up -d
# Wait 2-3 minutes
```

### Scenario 4: Fresh start (clean slate)
```bash
docker-compose down -v
./start-all-services.sh
# Everything is brand new
```

---

## 🎬 FOR DEMO RECORDING

Complete workflow:
```bash
# 1. Start services
./start-all-services.sh

# 2. Wait 2-3 minutes ☕

# 3. Check everything is ready
./check-demo-readiness.sh

# 4. Open all browser tabs
./open-demo-tabs.sh

# 5. Follow DEMO_RECORDING_GUIDE.md
```

---

## 📊 CHECK WHAT'S RUNNING

### See all containers:
```bash
docker-compose ps
```

### See logs:
```bash
docker-compose logs -f
```

### See logs for one service:
```bash
docker-compose logs -f webhook-management-service
```

---

## 🎯 QUICK REFERENCE

| What | Command |
|------|---------|
| **Start** | `./start-all-services.sh` |
| **Check** | `./check-demo-readiness.sh` |
| **Open tabs** | `./open-demo-tabs.sh` |
| **Stop** | `docker-compose down` |
| **Restart** | `docker-compose restart` |
| **Logs** | `docker-compose logs -f` |
| **Status** | `docker-compose ps` |

---

## 🚀 BOTTOM LINE

**Just run this:**
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./start-all-services.sh
```

**Wait 2-3 minutes, then you're ready to go!** ✨

---

## 🎉 THAT'S IT!

Everything is automated. The script handles:
- ✅ Building latest code
- ✅ Starting services in the right order
- ✅ Waiting for health checks
- ✅ Showing you progress

**You just run one command and wait!** 🎊

---

**Need help?** Check these files:
- `HOW_TO_START_SERVICES.md` - Detailed guide
- `DEMO_RECORDING_GUIDE.md` - Complete demo script
- `DEMO_QUICK_START.md` - Quick demo setup

**Last updated:** November 4, 2025
