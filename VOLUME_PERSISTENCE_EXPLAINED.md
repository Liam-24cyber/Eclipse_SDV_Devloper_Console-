# 💾 **DOCKER VOLUMES - PERSISTENCE GUARANTEE**

## 🎯 **QUICK ANSWER**

### **Volumes will NOT be deleted when you:**
- ❌ Close VS Code / your IDE
- ❌ Run `docker compose down`
- ❌ Shutdown your Mac
- ❌ Restart your Mac
- ❌ Restart Docker Desktop
- ❌ Close the terminal
- ❌ End your session

### **Volumes WILL be deleted ONLY if you:**
- ✅ Run `docker compose down -v` (notice the `-v` flag)
- ✅ Run `docker volume rm <volume-name>`
- ✅ Run `docker volume prune` (and confirm deletion)
- ✅ Manually delete `/var/lib/docker/volumes/` directory

---

## 📊 **Understanding Docker Volumes**

### **What are Docker Volumes?**

Docker volumes are **persistent storage** that lives **outside** of containers.

```
Your Mac's Filesystem:
└── /var/lib/docker/volumes/
    ├── eclipse_sdv_devloper_console-_postgres-data/
    │   └── _data/
    │       └── [All PostgreSQL database files]
    ├── eclipse_sdv_devloper_console-_rabbitmq-data/
    │   └── _data/
    │       └── [All RabbitMQ queue data]
    ├── eclipse_sdv_devloper_console-_minio-data/
    │   └── _data/
    │       └── [All uploaded files]
    └── ... (other volumes)
```

**These are real directories on your Mac's hard drive!**

---

## 🔄 **Container vs Volume Lifecycle**

### **Container Lifecycle (Temporary):**
```bash
docker compose up -d     # Create + Start containers
docker compose down      # Stop + Delete containers ← Containers DELETED
docker compose up -d     # Create NEW containers ← New containers created
```

**Result:** Containers are recreated, but...

### **Volume Lifecycle (Permanent):**
```bash
docker compose up -d     # Mount volumes to containers
docker compose down      # Unmount volumes ← Volumes REMAIN on disk
docker compose up -d     # Mount SAME volumes to new containers
```

**Result:** ✅ Same data persists across container lifecycles!

---

## 🧪 **PROOF - Let's Test It**

### **Test 1: Normal Shutdown**

```bash
# 1. Check current data
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM scenarios;"
# Result: 16

# 2. Shutdown containers
docker compose down

# 3. Check volumes still exist
docker volume ls | grep postgres-data
# Result: ✅ Volume still exists!

# 4. Restart containers
docker compose up -d

# 5. Wait 30 seconds, then check data again
sleep 30
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM scenarios;"
# Result: 16 ← SAME DATA!
```

**✅ Proof: Data persists!**

---

### **Test 2: Rebuild Containers**

```bash
# 1. Rebuild + Restart
docker compose up -d --build

# 2. Check data
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM scenarios;"
# Result: 16 ← SAME DATA!
```

**✅ Proof: Code changes don't affect data!**

---

### **Test 3: Nuclear Option (Data Loss)**

```bash
# ⚠️ WARNING: This DELETES volumes!
docker compose down -v

# Check volumes
docker volume ls | grep postgres-data
# Result: ❌ Volume DELETED!

# Restart
docker compose up -d

# Check data
docker exec -it postgres psql -U postgres -c "SELECT COUNT(*) FROM scenarios;"
# Result: 0 ← NO DATA! Fresh database!
```

**❌ Proof: Only `-v` flag deletes data!**

---

## 📋 **Your Current Configuration**

### **docker-compose.yml Volume Declarations:**

```yaml
volumes:
  postgres-data:      # ← Persistent volume
    driver: local     # ← Stored on local disk
  rabbitmq-data:      # ← Persistent volume
    driver: local     # ← Stored on local disk
  redis-data:         # ← Persistent volume
    driver: local     # ← Stored on local disk
  minio-data:         # ← Persistent volume
    driver: local     # ← Stored on local disk
  pgadmin-data:       # ← Persistent volume
    driver: local     # ← Stored on local disk
  prometheus-data:    # ← Persistent volume
    driver: local     # ← Stored on local disk
  grafana-data:       # ← Persistent volume
    driver: local     # ← Stored on local disk
```

**All volumes use `driver: local` = stored on your Mac's disk permanently!**

---

## 🗂️ **Where Volumes Are Actually Stored**

### **On macOS (Docker Desktop):**

```bash
# Volumes are stored in Docker's VM
# Accessible via Docker Desktop or docker commands

# List all volumes
docker volume ls

# Inspect a specific volume
docker volume inspect eclipse_sdv_devloper_console-_postgres-data

# View volume location
docker volume inspect eclipse_sdv_devloper_console-_postgres-data | jq -r '.[0].Mountpoint'
```

**Output example:**
```json
{
  "Name": "eclipse_sdv_devloper_console-_postgres-data",
  "Driver": "local",
  "Mountpoint": "/var/lib/docker/volumes/eclipse_sdv_devloper_console-_postgres-data/_data",
  "Created": "2024-12-XX",
  "Scope": "local"
}
```

---

## 🔒 **Volume Persistence Guarantees**

### **✅ Volumes persist across:**

| Action | Volumes Deleted? | Data Lost? |
|--------|-----------------|------------|
| `docker compose down` | ❌ NO | ❌ NO |
| `docker compose stop` | ❌ NO | ❌ NO |
| `docker compose restart` | ❌ NO | ❌ NO |
| `docker compose up -d --build` | ❌ NO | ❌ NO |
| Close project/IDE | ❌ NO | ❌ NO |
| Shutdown Mac | ❌ NO | ❌ NO |
| Restart Mac | ❌ NO | ❌ NO |
| Restart Docker Desktop | ❌ NO | ❌ NO |
| Container crashes | ❌ NO | ❌ NO |
| Service rebuilds | ❌ NO | ❌ NO |

### **❌ Volumes deleted ONLY by:**

| Action | Volumes Deleted? | Data Lost? |
|--------|-----------------|------------|
| `docker compose down -v` | ✅ YES | ✅ YES |
| `docker volume rm <name>` | ✅ YES | ✅ YES |
| `docker volume prune` | ✅ YES | ✅ YES |
| `docker system prune -a --volumes` | ✅ YES | ✅ YES |
| Manual deletion of Docker VM | ✅ YES | ✅ YES |

---

## 🎬 **Common Scenarios**

### **Scenario 1: End of Day**
```bash
# 5 PM - You're leaving
docker compose down

# Your Mac:
# - Containers: Stopped & Removed
# - Volumes: ✅ Still on disk
# - Data: ✅ Safe and sound
```

### **Scenario 2: Next Morning**
```bash
# 9 AM - You're back
docker compose up -d

# Docker:
# - Creates NEW containers
# - Mounts EXISTING volumes
# - Result: ✅ All your data is back!
```

### **Scenario 3: Mac Restart**
```bash
# Restart Mac
# (Docker Desktop stops)

# After Mac boots:
# - Docker Desktop starts
# - Volumes: ✅ Still there
# - Run: docker compose up -d
# - Result: ✅ Everything back!
```

### **Scenario 4: Code Changes**
```bash
# You changed Java code
docker compose up -d --build

# Docker:
# - Rebuilds service images
# - Recreates containers
# - Mounts EXISTING volumes
# - Result: ✅ Code updated, data intact!
```

---

## 🔍 **How to Check Volume Status**

### **List All Volumes:**
```bash
docker volume ls
```

**Expected output:**
```
DRIVER    VOLUME NAME
local     eclipse_sdv_devloper_console-_postgres-data
local     eclipse_sdv_devloper_console-_rabbitmq-data
local     eclipse_sdv_devloper_console-_redis-data
local     eclipse_sdv_devloper_console-_minio-data
local     eclipse_sdv_devloper_console-_pgadmin-data
local     eclipse_sdv_devloper_console-_prometheus-data
local     eclipse_sdv_devloper_console-_grafana-data
```

### **Check Volume Size:**
```bash
docker system df -v
```

### **Inspect Specific Volume:**
```bash
docker volume inspect eclipse_sdv_devloper_console-_postgres-data
```

### **Check if Volume is Mounted:**
```bash
docker ps --format "{{.Names}}: {{.Mounts}}"
```

---

## 💾 **Backup Strategy (Optional)**

### **Backup Volumes:**
```bash
# Backup PostgreSQL volume
docker run --rm \
  -v eclipse_sdv_devloper_console-_postgres-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup-$(date +%Y%m%d).tar.gz /data

# Backup RabbitMQ volume
docker run --rm \
  -v eclipse_sdv_devloper_console-_rabbitmq-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/rabbitmq-backup-$(date +%Y%m%d).tar.gz /data
```

### **Restore Volumes:**
```bash
# Stop services first
docker compose down

# Restore PostgreSQL volume
docker run --rm \
  -v eclipse_sdv_devloper_console-_postgres-data:/data \
  -v $(pwd):/backup \
  alpine sh -c "cd / && tar xzf /backup/postgres-backup-20241204.tar.gz"

# Restart services
docker compose up -d
```

---

## 🛡️ **Safety Checklist**

### **✅ DO:**
- Use `docker compose down` for normal shutdown
- Use `docker compose up -d` for normal startup
- Use `docker compose up -d --build` to rebuild with code changes
- Keep volumes for data persistence

### **❌ DON'T:**
- Use `docker compose down -v` unless you want to DELETE all data
- Use `docker volume prune` without understanding consequences
- Use `docker system prune --volumes` unless starting fresh

---

## 🎯 **Your Exact Workflow**

### **Tonight (Leaving):**
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
docker compose down
# ✅ Containers stopped and removed
# ✅ Volumes remain on disk
# ✅ Data is safe
```

### **Tomorrow (Arriving):**
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
docker compose up -d
# ✅ New containers created
# ✅ Existing volumes mounted
# ✅ All data restored automatically
```

### **Verification:**
```bash
./verify-restart-persistence.sh
# ✅ Check all data persisted correctly
```

---

## 📊 **Final Comparison Table**

| Aspect | Containers | Volumes |
|--------|-----------|---------|
| **Lifecycle** | Temporary | Permanent |
| **Deleted by `docker compose down`** | ✅ YES | ❌ NO |
| **Survives Mac restart** | ❌ NO | ✅ YES |
| **Survives Docker restart** | ❌ NO | ✅ YES |
| **Survives `--build`** | ❌ NO | ✅ YES |
| **Stores data** | ❌ NO | ✅ YES |
| **Requires `-v` to delete** | ❌ N/A | ✅ YES |

---

## ✅ **ABSOLUTE GUARANTEE**

### **Your volumes are safe as long as you DON'T use:**

1. `docker compose down -v` ← Notice the `-v`
2. `docker volume rm <volume-name>`
3. `docker volume prune`
4. `docker system prune --volumes`

### **Your volumes WILL persist when you:**

1. ✅ `docker compose down` ← Safe!
2. ✅ Close project ← Safe!
3. ✅ Shutdown Mac ← Safe!
4. ✅ Restart Mac ← Safe!
5. ✅ Restart Docker ← Safe!
6. ✅ Rebuild services ← Safe!

---

## 🎉 **Bottom Line**

**Volumes are designed to be permanent!**

Think of volumes like files on your hard drive:
- Closing an app doesn't delete the files
- Restarting your Mac doesn't delete the files
- Only **explicitly deleting** removes the files

**Same with Docker volumes:**
- `docker compose down` doesn't delete volumes
- Restarting Mac doesn't delete volumes
- Only `docker compose down -v` deletes volumes

**Your data is 100% safe with normal usage!** 🎉

---

## 📞 **Quick Reference**

**Safe command (use this):**
```bash
docker compose down  # ← No -v flag = volumes persist
```

**Dangerous command (avoid unless intentional):**
```bash
docker compose down -v  # ← -v flag = volumes DELETED
```

**Remember:** No `-v` = No volume deletion! ✅
