# 🔧 FIX: DATABASE HAS 0 SCENARIOS

## 🎯 THE PROBLEM

Your database is empty because:
1. ✅ The database exists (`postgres`)
2. ✅ The tables exist (`scenario`, `track`, `webhooks`, etc.)
3. ❌ **BUT there's NO DATA** (0 scenarios, 0 tracks)

This is normal for a fresh installation! You need to **seed the database** with sample data.

---

## ⚡ QUICK FIX (2 commands)

### Step 1: Make seed script executable
```bash
chmod +x seed-database.sh
```

### Step 2: Run the seed script
```bash
./seed-database.sh
```

**That's it!** ✨

---

## ✅ WHAT GETS CREATED

The seed script will create:

### 📊 **16 Scenarios:**
- **Urban Scenarios:**
  - Urban Traffic Navigation
  - Pedestrian Crossing Detection
  - Parking Maneuvers
  - Roundabout Navigation

- **Highway Scenarios:**
  - Highway Lane Keeping
  - Adaptive Cruise Control
  - Lane Change Assist
  - Highway Merging

- **Safety Scenarios:**
  - Emergency Braking
  - Collision Avoidance
  - Vulnerable Road Users

- **Weather Scenarios:**
  - Rain Driving
  - Fog Navigation
  - Night Driving

- **Edge Cases:**
  - Construction Zone Navigation
  - School Zone Safety

### 🛣️ **10 Tracks:**
- Downtown City Circuit
- Highway Test Track
- Mixed Urban-Highway Route
- Rural Country Roads
- Mountain Pass Circuit
- And more...

### 🔔 **Test Webhooks:**
- Webhook event types
- Sample webhook configurations
- Delivery history

---

## 📋 STEP-BY-STEP GUIDE

### Step 1: Check Current State
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
```
Should show `0` right now.

### Step 2: Run Seed Script
```bash
./seed-database.sh
```

You'll see:
```
╔════════════════════════════════════════════════════════╗
║  SDV Developer Console - Database Seeding              ║
╚════════════════════════════════════════════════════════╝

📝 Loading seed data into database...

INSERT 0 16
INSERT 0 10
...

✓ Database seeded successfully!

📊 Verification:

Scenarios:
 total_scenarios 
-----------------
              16

Tracks:
 total_tracks 
--------------
           10
```

### Step 3: Verify in pgAdmin
```sql
SELECT id, name, description, status, created_at 
FROM scenario 
ORDER BY created_at DESC 
LIMIT 5;
```

Should now show 16 scenarios! ✅

---

## 🎬 FOR YOUR DEMO

This is actually **PERFECT** for your demo! Here's why:

### **Option 1: Seed BEFORE Demo (Recommended)**
```bash
./seed-database.sh
```

**Demo shows:**
- ✅ Database with existing scenarios
- ✅ More realistic system state
- ✅ You can still create a NEW scenario live
- ✅ Shows that the system works with multiple records

### **Option 2: Start Empty (More Dramatic)**
```bash
# Don't run seed script
```

**Demo shows:**
- ✅ Completely empty database at start
- ✅ Create scenario live (first one!)
- ✅ Watch it appear in database
- ✅ More dramatic "before/after"

**I recommend Option 1** - it looks more realistic and professional!

---

## 🔍 VERIFY SEED DATA

### Check Scenarios
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
```

### Check Tracks
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM track;"
```

### Check Webhooks
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM webhooks;"
```

### See Sample Data
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT id, name, type, status FROM scenario LIMIT 5;"
```

---

## 🆘 TROUBLESHOOTING

### Error: "Permission denied"
**Fix:**
```bash
chmod +x seed-database.sh
./seed-database.sh
```

### Error: "Seed file not found"
**Fix:** Make sure you're in the correct directory
```bash
cd "/Users/ivanshalin/SDV Phase 2 E2E/Eclipse_SDV_Devloper_Console-"
./seed-database.sh
```

### Error: "Database postgres does not exist"
**Fix:** Make sure PostgreSQL container is running
```bash
docker-compose ps | grep postgres
# If not running:
docker-compose up -d postgres
# Wait 10 seconds, then try seed script again
```

### Seed Script Runs But Still 0 Scenarios
**Fix:** Check for errors in the seed script output
```bash
./seed-database.sh 2>&1 | tee seed-output.log
cat seed-output.log
```

---

## 🔧 DATABASE CONNECTION INFO

**IMPORTANT:** The database name is `postgres`, NOT `sdv_db`!

### ✅ Correct Connection:
- Host: `postgres` (when connecting from pgAdmin inside Docker)
- Port: `5432`
- Database: **`postgres`** ✅
- Username: `postgres`
- Password: `postgres`

### ❌ Wrong Connection:
- Database: `sdv_db` ❌ (This database doesn't exist!)

---

## 📝 UPDATE YOUR DEMO GUIDE

I need to fix the pgAdmin connection info in your demo guides!

**Current (WRONG):**
```
Database: sdv_db ❌
```

**Should be:**
```
Database: postgres ✅
```

---

## 🎯 COMPLETE DEMO SETUP

Here's the **corrected** complete workflow:

```bash
# 1. Start services
./start-all-services.sh

# 2. Wait 2-3 minutes
sleep 180

# 3. Seed database with sample data
./seed-database.sh

# 4. Check readiness
./check-demo-readiness.sh

# 5. Open browser tabs
./open-demo-tabs.sh

# Now you're ready to record with a populated database! 🎬
```

---

## 🎬 UPDATED DEMO WORKFLOW

### **Before Recording:**

1. **Start services:**
   ```bash
   ./start-all-services.sh
   ```

2. **Seed database:**
   ```bash
   ./seed-database.sh
   ```

3. **Open tabs:**
   ```bash
   ./open-demo-tabs.sh
   ```

4. **In pgAdmin:**
   - Login: `admin@example.com / admin`
   - Connect to server:
     - Host: `postgres`
     - Port: `5432`
     - Database: **`postgres`** ✅ (NOT sdv_db!)
     - Username: `postgres`
     - Password: `postgres`

5. **Run query to see data:**
   ```sql
   SELECT id, name, description, status, created_at 
   FROM scenario 
   ORDER BY created_at DESC 
   LIMIT 5;
   ```
   
   Should show **16 scenarios**! ✅

---

## 💡 WHY THIS HAPPENED

The database schema (tables) was created automatically by the services using **Flyway migrations**, but the **data** must be manually seeded.

This is actually **best practice**:
- ✅ Schema migrations = automatic (via Flyway)
- ✅ Test data = manual (via seed scripts)
- ✅ Production data = from real users/API

---

## 🚀 BOTTOM LINE

**To fix the empty database:**
```bash
./seed-database.sh
```

**Then verify:**
```bash
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
```

Should show: **16** ✅

**Now you have data for your demo!** 🎉

---

**Created:** November 4, 2025  
**Issue:** Database has 0 scenarios  
**Solution:** Run seed-database.sh  
**Time to fix:** 30 seconds ⚡
