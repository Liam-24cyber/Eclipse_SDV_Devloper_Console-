# 🎥 DEMO RECORDING GUIDE

**Purpose### Step 2: Verify System Health
```bash
./verify-system-health.sh
```

✅ All services should show GREEN.

### Step 3: Open All Browser Tabs (in this order) step-by-step guide for recor### Step 5: Prepare Your SQL Query

Open SQL Tool in pgAdmin and prepare:
```sql
SELECT id, name, description, status, created_at, updated_at 
FROM scenario 
ORDER BY created_at DESC 
LIMIT 5;
```

Don't run yet—just have it ready!

**💡 Note:** You should see 16 scenarios when you run this query (auto-seeded by the startup script).DV Developer Console demo video  
**Duration:** ~15-20 minute demo  
**Date:** November 4, 2025

---

## 🚀 PART 1: PRE-RECORDING SETUP (5 minutes)

### Step 1: Start Everything (Auto-Seeds Database!)
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./start-all-services.sh
```

⏱️ **Wait 2-3 minutes** for all services to start.

✅ **The script automatically:**
- Starts all 15 services
- Checks if database is empty
- Seeds with 16 scenarios and 13 tracks if needed
- Verifies everything is ready

### Step 2: Verify System Health
```bash
./verify-system-health.sh
```

✅ All services should show GREEN.

### Step 3: Open All Browser Tabs (in this order)

**Tab 1 - UI (Main Demo):**
- http://localhost:3000
- Login: `admin / admin123`

**Tab 2 - pgAdmin (Database):**
- http://localhost:5050
- Login: `admin@example.com / admin`
- Pre-connect to server (see below)

**Tab 3 - MinIO (Object Storage):**
- http://localhost:9001
- Login: `minioadmin / minioadmin`

**Tab 4 - RabbitMQ (Message Queue):**
- http://localhost:15672
- Login: `guest / guest`
- Click "Queues" tab

**Tab 5 - Prometheus (Metrics):**
- http://localhost:9090/targets
- Should show all green targets

**Tab 6 - Grafana (Dashboards):**
- http://localhost:3001
- Login: `admin / admin`
- Open pre-configured dashboard

### Step 4: pgAdmin Database Connection (ONE-TIME)

1. In pgAdmin, right-click "Servers" → Register → Server
2. **General Tab:** Name: `SDV Database`
3. **Connection Tab:**
   - Host: `postgres`
   - Port: `5432`
   - Database: `postgres`
   - Username: `postgres`
   - Password: `postgres`
4. Click Save

### Step 5: Prepare Your SQL Query

Open SQL Tool in pgAdmin and prepare:
```sql
SELECT id, name, description, status, created_at, updated_at 
FROM scenarios 
ORDER BY created_at DESC 
LIMIT 5;
```

Don't run yet—just have it ready!

---

## 🎬 PART 2: RECORDING SCRIPT (Screen-by-Screen)

### 🎯 SCENE 1: Introduction (Architecture Overview)
**Screen:** Show architecture diagram or whiteboard  
**Time:** 1 minute

**Script:**
> "I'll walk through the SDV Developer Console end-to-end—from a developer creating content, to events flowing through RabbitMQ, to webhook notifications, and finally monitoring everything."
>
> "Key architecture: UI and API Gateway talk to Scenario and Track microservices. They persist to PostgreSQL and MinIO. Every change emits events via the Message Queue Service into RabbitMQ. The Webhook Management Service consumes those events and pushes notifications to integrators. Redis powers gateway throttling, Prometheus scrapes metrics, Grafana visualizes them."

---

### 🎯 SCENE 2: pgAdmin – Persistent System-of-Record
**Screen:** pgAdmin query tool  
**Time:** 2 minutes

**Actions:**
1. Switch to pgAdmin tab
2. Show the prepared SQL query
3. Click Execute (▶️ button)
4. Show the results table

**Script:**
> "First, here's pgAdmin connected to our PostgreSQL instance. I'm running SELECT * FROM scenarios. Each row is a scenario created through the UI or API. This proves that every user action is committed to a durable store—not just memory."
>
> "Postgres matters because simulations rely on consistent metadata—tracks, scenarios, and webhook stats live here. After a restart the data stays because we mount named Docker volumes."

**💡 Tip:** Highlight a row with the mouse to show the data fields clearly.

---

### 🎯 SCENE 3: MinIO Console – Binary Artifacts
**Screen:** MinIO console  
**Time:** 1-2 minutes

**Actions:**
1. Switch to MinIO tab
2. Click "Buckets" in left menu
3. Click into a bucket (e.g., `scenarios` or `tracks`)
4. Show some files/objects

**Script:**
> "Binary assets—track files, scenario bundles, simulation outputs—aren't stored in Postgres. Instead we use MinIO, an S3-compatible object store. This UI shows our buckets and stored objects."
>
> "When I upload a scenario file, the Scenario Service writes it directly to MinIO. The service layer then saves only the metadata reference in Postgres."

---

### 🎯 SCENE 4: RabbitMQ – Event Backbone
**Screen:** RabbitMQ Queues tab  
**Time:** 2-3 minutes

**Actions:**
1. Switch to RabbitMQ tab
2. Already on "Queues" tab
3. Point out key queues:
   - `scenario.events`
   - `track.events`
   - `simulation.events`
   - Dead-letter queues
4. Show "Consumers" column (should show 1+ for webhook service)

**Script:**
> "Every state change emits an event. On RabbitMQ you can see the topology: scenario.events, track.events, simulation.events, and associated dead-letter queues. The Consumers column shows the Webhook service listening."
>
> "When I trigger a new scenario, one scenario.events message is published, then immediately consumed. If I pause the consumer, queue depth grows—this is how we isolate producers from downstream slowdowns."

---

### 🎯 SCENE 5: Create a Scenario (LIVE ACTION!)
**Screen:** UI (localhost:3000)  
**Time:** 2-3 minutes

**Actions:**
1. Switch to UI tab
2. Navigate to Scenarios page
3. Click "Create New Scenario"
4. Fill in:
   - Name: `Demo Scenario for Recording`
   - Description: `Live demo test`
   - Select a track (if required)
5. Click Save/Create

**Script:**
> "Now let's create a scenario in real-time. I'm using the Developer Console UI to create 'Demo Scenario for Recording.'"
>
> [Fill in form]
>
> "When I click Create, several things happen: the API Gateway validates and routes the request, the Scenario Service persists to Postgres and MinIO, and an event is published to RabbitMQ."

---

### 🎯 SCENE 5a: Automated E2E Workflow (OPTIONAL - BONUS!)
**Screen:** Terminal  
**Time:** 1-2 minutes

**Actions:**
1. Open a new terminal window
2. Run: `./run-e2e-demo.sh`
3. Show the automated workflow execution
4. Point out each step as it executes

**Script:**
> "To show how developers integrate with our platform programmatically, I'll run our automated E2E demo script. This creates a scenario via the GraphQL API, publishes events, and triggers webhook deliveries—all automatically."
>
> [Run script and watch output]
>
> "Notice it creates the scenario, verifies it in Postgres, publishes to RabbitMQ, and the webhook service immediately processes it. This complete automation is what makes our platform production-ready for integrators."

**💡 Note:** This is optional but impressive! Shows both UI and API workflows.

---

### 🎯 SCENE 6: Verify Event Flow in RabbitMQ
**Screen:** Back to RabbitMQ Queues  
**Time:** 1 minute

**Actions:**
1. Refresh RabbitMQ page
2. Look for message spike in `scenario.events` queue
3. Show "Total" messages went up and down (consumed)

**Script:**
> "Switching back to RabbitMQ, you can see the message counter spiked and was immediately consumed by the Webhook Management Service. This proves our event pipeline is working in real-time."

---

### 🎯 SCENE 7: Verify Database Update in pgAdmin
**Screen:** Back to pgAdmin  
**Time:** 1 minute

**Actions:**
1. Switch to pgAdmin
2. Re-run the SQL query (▶️ button)
3. Show the new scenario at the top of results

**Script:**
> "Back in pgAdmin, I'll re-run the same query. There's our new scenario at the top—proof that the data was persisted to PostgreSQL. Notice the created_at timestamp matches when we just created it."

---

### 🎯 SCENE 8: Webhook Delivery Evidence
**Screen:** pgAdmin (new query)  
**Time:** 1-2 minutes

**Actions:**
1. In pgAdmin, run new query:
```sql
SELECT id, webhook_id, event_type, status, created_at 
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 10;
```

**Script:**
> "The Webhook Management Service consumes those queue messages. It looks up active webhooks in Postgres and posts to external URLs. Here we can see webhook_deliveries showing recent delivery attempts."
>
> "Each row records the event type, delivery status, and timestamp. This audit trail is critical for integrators to debug their webhooks."

---

### 🎯 SCENE 9: Redis – Gateway Reliability
**Screen:** Terminal showing Redis CLI  
**Time:** 1 minute

**Actions:**
1. Open terminal
2. Run:
```bash
docker exec -it redis redis-cli
KEYS *rate*
exit
```

**Script:**
> "Redis runs invisibly but protects the API Gateway. Spring Cloud Gateway uses it to store rate-limiting counters. These keys show rate-limit buckets per client."
>
> "If a client bursts traffic, Redis-backed throttling smooths it out instead of overwhelming the scenario service."

---

### 🎯 SCENE 10: Prometheus – Scraping Metrics
**Screen:** Prometheus Targets  
**Time:** 1-2 minutes

**Actions:**
1. Switch to Prometheus tab (already on /targets)
2. Show all targets are UP (green)
3. Point out:
   - API Gateway endpoint
   - Scenario Service endpoint
   - Track Service endpoint
   - RabbitMQ exporter

**Script:**
> "For observability, Prometheus scrapes every service's actuator endpoint plus RabbitMQ's built-in exporter. All targets show green—they're responding within the scrape interval."
>
> "A red status would tell us immediately when a service is unreachable."

---

### 🎯 SCENE 11: Grafana – Operational Dashboards
**Screen:** Grafana dashboard  
**Time:** 2-3 minutes

**Actions:**
1. Switch to Grafana tab
2. Show dashboard panels:
   - JVM metrics (heap, threads)
   - HTTP request counters
   - RabbitMQ queue depth
   - Request latency

**Script:**
> "Prometheus feeds Grafana dashboards. This panel shows JVM threads and HTTP request counters for the gateway and backend services. The bottom chart tracks RabbitMQ unacked messages—verifying our event pipeline isn't backing up."
>
> "Another panel charts request volume per service and webhook timings. Spikes here correlate with events we generate, giving operators real-time feedback."
>
> "Grafana is critical during load tests or UAT scenarios; we can prove latency, throughput, and queue health meet our non-functional requirements."

---

### 🎯 SCENE 12: Closing Summary
**Screen:** Back to UI or architecture diagram  
**Time:** 1-2 minutes

**Script:**
> "Putting it together: a developer uses the UI → API Gateway handles it with Redis rate limiting → Scenario/Track services persist to Postgres/MinIO and publish events via the Message Queue Service → RabbitMQ routes those events → Webhook Management Service delivers to integrators and records results → Prometheus + Grafana and RabbitMQ UI let operators confirm everything is healthy."
>
> "Because all state—including RabbitMQ definitions and database data—sits on Docker volumes, a simple restart preserves the entire environment. That's production-grade infrastructure running locally."

---

## 📋 PART 3: POST-RECORDING CLEANUP (Optional)

### If You Want to Reset Everything:
```bash
docker-compose down -v
docker-compose up -d
./start-all-services.sh
```

### If You Want to Keep Current State:
```bash
docker-compose down
# Data persists, ready for next demo!
```

---

## 🎯 QUICK REFERENCE: ALL URLs & CREDENTIALS

| Service | URL | Username | Password |
|---------|-----|----------|----------|
| **UI** | http://localhost:3000 | admin | admin123 |
| **pgAdmin** | http://localhost:5050 | admin@example.com | admin |
| **MinIO** | http://localhost:9001 | minioadmin | minioadmin |
| **RabbitMQ** | http://localhost:15672 | guest | guest |
| **Prometheus** | http://localhost:9090 | — | — |
| **Grafana** | http://localhost:3001 | admin | admin |
| **API Gateway** | http://localhost:8080 | — | — |

---

## 💡 RECORDING TIPS

### Before You Start:
- ✅ Close unnecessary applications (Slack, email, etc.)
- ✅ Turn off notifications
- ✅ Clear browser history/cache if needed
- ✅ Use incognito/private windows for clean UI
- ✅ Zoom browser to 100% (or 125% for visibility)
- ✅ Have all tabs ready in order
- ✅ Test your microphone
- ✅ Practice the flow once

### During Recording:
- 🎤 Speak clearly and not too fast
- 🖱️ Move mouse slowly and deliberately
- ⏸️ Pause briefly between scenes for editing
- 📝 Follow the script but don't memorize—be natural
- 🔍 Zoom in on important details (Cmd +)
- ⏱️ Don't rush—15-20 minutes is perfect

### Screen Recording Tools (Mac):
- **QuickTime Player** (built-in, free)
- **OBS Studio** (free, professional)
- **Loom** (easy, web-based)
- **ScreenFlow** (paid, professional)

---

## 🎬 SCENE ORDER CHECKLIST

Use this during recording to stay on track:

- [ ] Scene 1: Architecture introduction (1 min)
- [ ] Scene 2: pgAdmin - show scenarios table (2 min)
- [ ] Scene 3: MinIO - show buckets and objects (1-2 min)
- [ ] Scene 4: RabbitMQ - show queues and consumers (2-3 min)
- [ ] Scene 5: UI - create a new scenario LIVE (2-3 min)
- [ ] Scene 6: RabbitMQ - verify event was processed (1 min)
- [ ] Scene 7: pgAdmin - verify new scenario in DB (1 min)
- [ ] Scene 8: pgAdmin - show webhook deliveries (1-2 min)
- [ ] Scene 9: Terminal - show Redis keys (1 min)
- [ ] Scene 10: Prometheus - show targets (1-2 min)
- [ ] Scene 11: Grafana - show dashboards (2-3 min)
- [ ] Scene 12: Closing summary (1-2 min)

**Total: ~15-20 minutes**

---

## 🆘 TROUBLESHOOTING DURING RECORDING

### If a service is down:
```bash
docker-compose ps
docker-compose restart <service-name>
```

### If pgAdmin won't connect:
- Check postgres container: `docker ps | grep postgres`
- Verify credentials: `postgres / postgres / sdv_db`

### If RabbitMQ shows no queues:
- Restart: `docker-compose restart rabbitmq`
- Wait 30 seconds for queue setup

### If Grafana has no dashboards:
- Import dashboard manually: Upload JSON from `/monitoring/grafana/dashboards/`

### If you mess up:
- **Pause recording, fix the issue, resume**
- Or cut the video in post-production
- Or just keep going—demos with real issues are authentic!

---

## 🎥 FINAL CHECKLIST BEFORE HITTING RECORD

- [ ] All services are running (`docker-compose ps` shows all UP)
- [ ] All 6 browser tabs are open and logged in
- [ ] pgAdmin is connected to database
- [ ] SQL query is prepared in pgAdmin
- [ ] Screen recording software is ready
- [ ] Microphone is working
- [ ] Notifications are OFF
- [ ] Desktop is clean (hide personal files/folders)
- [ ] You've practiced the flow once
- [ ] You have water nearby (stay hydrated! 💧)

---

## 🚀 YOU'RE READY!

**Hit record and follow the script. You got this!** 🎬

**Good luck with your demo!** 🌟

---

**Quick Start Command:**
```bash
# One command to start everything (auto-seeds database!)
./start-all-services.sh && echo "Wait 2-3 minutes, then open browser tabs!"
```

**Last updated:** November 4, 2025
