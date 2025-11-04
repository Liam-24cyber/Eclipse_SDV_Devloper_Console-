# 🎯 POSTMAN E2E - QUICK REFERENCE

## 🚀 QUICK START (3 COMMANDS)

```bash
./start-all-services.sh    # 1. Start all services
./start-e2e-api.sh         # 2. Start E2E API
# 3. Import E2E_Demo_API.postman_collection.json into Postman
```

---

## 🎬 ONE-CLICK E2E WORKFLOW

**In Postman:**
1. Open: `SDV E2E Demo API` collection
2. Select: `E2E Workflow` → `🎬 RUN COMPLETE E2E WORKFLOW`
3. Click: **Send**
4. Done! ✅

---

## 📋 API ENDPOINTS

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `POST` | `/api/e2e/run` | ⭐ **Run complete E2E** |
| `GET` | `/api/status` | System status |
| `POST` | `/api/scenario/create` | Create scenario |
| `GET` | `/api/tracks` | Get tracks |
| `POST` | `/api/event/publish` | Publish event |
| `GET` | `/api/webhooks/deliveries` | Get deliveries |

**Base URL:** http://localhost:9000

---

## 🔥 CURL ONE-LINER

```bash
# Complete E2E workflow
curl -X POST http://localhost:9000/api/e2e/run \
  -H "Content-Type: application/json" \
  -d '{"scenarioName": "Quick Test"}' | jq
```

---

## ✅ WHAT IT DOES

1. ✅ Creates scenario
2. ✅ Verifies in database
3. ✅ Gets track
4. ✅ Creates simulation
5. ✅ Publishes to RabbitMQ
6. ✅ Waits for webhooks
7. ✅ Returns summary

**Time:** ~5 seconds  
**Repeatable:** Yes ✅

---

## 🎥 FOR DEMOS

```bash
# Terminal 1
./start-all-services.sh

# Terminal 2
./start-e2e-api.sh

# Postman
Click "Send" → Show results → Repeat
```

---

## 🆘 QUICK FIXES

```bash
# API won't start
npm install express
node e2e-api-server.js

# Services not running
./start-all-services.sh

# Check health
curl http://localhost:9000/health
```

---

## 📊 PORTS

| Service | Port |
|---------|------|
| E2E API | 9000 |
| Gateway | 8080 |
| UI | 3000 |
| RabbitMQ | 15672 |
| pgAdmin | 5050 |

---

**Full Guide:** `POSTMAN_E2E_GUIDE.md`  
**Collection:** `E2E_Demo_API.postman_collection.json`
