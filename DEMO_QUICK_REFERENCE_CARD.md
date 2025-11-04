# 🎬 DEMO QUICK REFERENCE CARD

**Print this or keep it on a second screen during recording!**

---

## ⚡ 3 COMMANDS TO START DEMO

```bash
./start-all-services.sh     # Start + auto-seed (2-3 min)
./open-demo-tabs.sh         # Open tabs (5 sec)
./run-e2e-demo.sh          # Run workflow (30 sec) ⭐
```

---

## 🌐 6 BROWSER TABS

| # | Service | URL | Login |
|---|---------|-----|-------|
| 1 | **UI** | localhost:3000 | admin / admin123 |
| 2 | **pgAdmin** | localhost:5050 | admin@example.com / admin |
| 3 | **MinIO** | localhost:9001 | minioadmin / minioadmin |
| 4 | **RabbitMQ** | localhost:15672 | guest / guest |
| 5 | **Prometheus** | localhost:9090 | (no login) |
| 6 | **Grafana** | localhost:3001 | admin / admin |

---

## 🔧 PGADMIN CONNECTION

- Host: `postgres`
- Port: `5432`
- Database: **`postgres`** (NOT sdv_db!)
- User: `postgres`
- Pass: `postgres`

---

## 📝 SQL QUERIES

### Show Scenarios:
```sql
SELECT id, name, status, created_at 
FROM scenario 
ORDER BY created_at DESC 
LIMIT 5;
```

### Show Webhooks:
```sql
SELECT id, event_type, status, created_at 
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 10;
```

---

## 🎬 SCENE ORDER (20 min)

1. Intro (1 min)
2. pgAdmin (2 min)
3. MinIO (1-2 min)
4. RabbitMQ (2-3 min)
5. Create Scenario UI (2-3 min)
6. **RUN: ./run-e2e-demo.sh** ⭐ (1-2 min)
7. Verify RabbitMQ (1 min)
8. Verify pgAdmin (1 min)
9. Show Webhooks (1-2 min)
10. Redis (1 min)
11. Prometheus (1-2 min)
12. Grafana (2-3 min)
13. Closing (1-2 min)

---

## 🆘 EMERGENCY FIXES

### Service down:
```bash
docker-compose restart <service>
```

### Start fresh:
```bash
docker-compose down
./start-all-services.sh
```

### Check status:
```bash
docker-compose ps
```

### View logs:
```bash
docker logs <service-name>
```

---

## ✅ PRE-RECORD CHECKLIST

- [ ] All services running
- [ ] All tabs open & logged in
- [ ] pgAdmin connected
- [ ] SQL queries ready
- [ ] Tested ./run-e2e-demo.sh
- [ ] Notifications OFF
- [ ] Desktop clean
- [ ] Mic tested
- [ ] Recording started

---

## 💡 KEY TALKING POINTS

- ✅ Production-grade infrastructure
- ✅ Event-driven architecture
- ✅ Complete data persistence
- ✅ Real-time monitoring
- ✅ API-first design
- ✅ Docker-based deployment
- ✅ Scalable & resilient

---

## 🎯 TERMINAL COMMANDS

### Show Redis:
```bash
docker exec -it redis redis-cli
KEYS *rate*
exit
```

### Run E2E:
```bash
./run-e2e-demo.sh
```

### Check DB:
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
```

---

**Good luck!** 🌟 **You got this!** 🚀
