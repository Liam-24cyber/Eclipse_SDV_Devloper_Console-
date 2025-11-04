# 🎬 E2E DEMO WORKFLOW GUIDE

**Purpose:** Automate the complete end-to-end workflow for demonstration  
**Date:** November 4, 2025

---

## 🎯 WHAT THIS DOES

The `run-e2e-demo.sh` script automates the **COMPLETE** workflow:

1. ✅ **Creates** a new scenario via GraphQL API
2. ✅ **Persists** to PostgreSQL database
3. ✅ **Creates** a simulation
4. ✅ **Publishes** event to RabbitMQ
5. ✅ **Consumes** event via Webhook Management Service
6. ✅ **Delivers** webhook notifications
7. ✅ **Records** delivery attempts in database
8. ✅ **Verifies** complete data flow

**Perfect for live demos!** 🎥

---

## 🚀 HOW TO USE

### **One Command to Run Complete E2E Flow:**

```bash
./run-e2e-demo.sh
```

**That's it!** Watch the complete workflow execute automatically!

---

## 📊 WHAT YOU'LL SEE

### Complete Output:
```
╔════════════════════════════════════════════════════════╗
║  SDV Developer Console - E2E Demo Workflow            ║
║  Complete Flow: Create → Simulate → Events → Webhooks ║
╚════════════════════════════════════════════════════════╝

📋 Step 1: Verifying all services are running...
✅ Services are running

📋 Step 2: Creating a new scenario via API...
✅ Scenario created successfully!
   ID: 550e8400-e29b-41d4-a716-446655440017
   Name: E2E Demo Scenario 14:30:25

📋 Step 3: Verifying scenario in database...
✅ Scenario found in PostgreSQL database
                  id                  |         name          | status  |         created_at         
--------------------------------------+----------------------+---------+----------------------------
 550e8400-e29b-41d4-a716-446655440017 | E2E Demo Scenario... | CREATED | 2025-11-04 14:30:25.123456

📋 Step 4: Fetching available tracks...
✅ Track selected for simulation
   ID: 660e8400-e29b-41d4-a716-446655440001
   Name: Downtown City Circuit

📋 Step 5: Creating simulation...
✅ Simulation created successfully!
   ID: sim-1699112425
   Name: E2E Demo Simulation 14:30:25

📋 Step 6: Publishing scenario event to RabbitMQ...
✅ Event published to scenario.events queue

📋 Step 7: Checking RabbitMQ queue status...
✅ RabbitMQ Queue Status:
   Queue: scenario.events
   Messages: 0 (may be consumed quickly)

📋 Step 8: Waiting for webhook processing...
   Giving webhook service time to process event...

📋 Step 9: Checking webhook delivery attempts...
✅ Webhook Deliveries Found: 5

Recent webhook deliveries:
  id  |    event_type     |  status   |         created_at         
------+-------------------+-----------+----------------------------
  123 | SCENARIO_CREATED  | SUCCESS   | 2025-11-04 14:30:30.456789
  122 | TRACK_UPDATED     | SUCCESS   | 2025-11-04 14:25:15.123456
  121 | SCENARIO_CREATED  | SUCCESS   | 2025-11-04 14:20:10.789012

📋 Step 10: Verifying complete data flow...

✅ Total Scenarios in DB: 17
✅ Total Simulations in DB: 5
✅ Total Webhook Deliveries: 5

╔════════════════════════════════════════════════════════╗
║  E2E Demo Workflow Complete!                           ║
╚════════════════════════════════════════════════════════╝

✅ Workflow Summary:
   1. ✅ Created scenario via GraphQL API
   2. ✅ Verified persistence in PostgreSQL
   3. ✅ Created simulation (or event)
   4. ✅ Published event to RabbitMQ
   5. ✅ Webhook service consumed event
   6. ✅ Webhook deliveries recorded

🔍 To view in pgAdmin:
   Scenario:  SELECT * FROM scenario WHERE id='550e8400-e29b-41d4-a716-446655440017';
   Events:    SELECT * FROM webhook_deliveries ORDER BY created_at DESC LIMIT 10;

🌐 Check in UI:
   Scenarios: http://localhost:3000/scenarios
   RabbitMQ:  http://localhost:15672/#/queues

🎉 Your E2E flow is working perfectly!
```

---

## 🎬 COMPLETE DEMO WORKFLOW

### **Setup Once:**
```bash
# Start all services (auto-seeds database)
./start-all-services.sh
```

### **During Demo:**
```bash
# Run complete E2E workflow
./run-e2e-demo.sh

# Run it again to create more data
./run-e2e-demo.sh

# And again!
./run-e2e-demo.sh
```

### **Result:**
- ✅ New scenarios created each time
- ✅ Events flow through RabbitMQ
- ✅ Webhooks delivered
- ✅ Complete audit trail in database

---

## 🎯 USE CASES

### **1. Live Demo Recording**
```bash
# Start services
./start-all-services.sh

# Open browser tabs
./open-demo-tabs.sh

# Run E2E workflow DURING recording
./run-e2e-demo.sh
```

**Shows:**
- Real-time API call
- Database update
- Event flow
- Webhook delivery

### **2. Testing Complete Flow**
```bash
# Run multiple times to verify consistency
./run-e2e-demo.sh
./run-e2e-demo.sh
./run-e2e-demo.sh

# Check accumulated data
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
```

### **3. Generating Test Data**
```bash
# Create multiple scenarios quickly
for i in {1..5}; do
  ./run-e2e-demo.sh
  sleep 2
done
```

---

## 📝 WHAT THE SCRIPT DOES (STEP-BY-STEP)

### **Step 1: Verify Services**
- Checks Docker containers are running
- Ensures PostgreSQL is available

### **Step 2: Create Scenario**
- Makes GraphQL API call to create scenario
- Uses unique timestamp in name
- Returns scenario ID

### **Step 3: Verify Database**
- Queries PostgreSQL for new scenario
- Shows scenario details

### **Step 4: Fetch Track**
- Gets available tracks via API
- Selects first track for simulation

### **Step 5: Create Simulation**
- Creates simulation linked to scenario
- Associates with selected track

### **Step 6: Publish Event**
- Constructs proper event payload
- Publishes to RabbitMQ scenario.events queue

### **Step 7: Check Queue**
- Verifies message was published
- Shows queue status

### **Step 8: Wait for Processing**
- Gives webhook service time to consume
- Allows async processing

### **Step 9: Check Deliveries**
- Queries webhook_deliveries table
- Shows recent delivery attempts

### **Step 10: Verify Flow**
- Counts all scenarios
- Counts all simulations
- Counts all webhook deliveries
- Confirms complete data flow

---

## 🎥 DEMO RECORDING INTEGRATION

### **Updated Demo Script (Scene 5a - NEW!):**

**AFTER creating scenario manually in Scene 5, ADD THIS:**

### 🎯 SCENE 5a: Automated E2E Flow (OPTIONAL)
**Screen:** Terminal  
**Time:** 1-2 minutes

**Actions:**
1. Open terminal
2. Run: `./run-e2e-demo.sh`
3. Watch the complete workflow execute

**Script:**
> "To demonstrate the complete automation, I'll run our E2E demo script. This simulates what happens when a scenario is created programmatically via our API."
>
> [Run script]
>
> "You can see it creates the scenario via GraphQL, verifies persistence in Postgres, publishes events to RabbitMQ, and the webhook service immediately processes them. This is how integrators would use our platform programmatically."

---

## 🔍 VERIFICATION QUERIES

### **After running script, check in pgAdmin:**

**New Scenarios:**
```sql
SELECT id, name, description, status, created_at 
FROM scenario 
WHERE name LIKE 'E2E Demo Scenario%'
ORDER BY created_at DESC;
```

**Webhook Deliveries:**
```sql
SELECT id, event_type, status, created_at, response_status_code
FROM webhook_deliveries 
ORDER BY created_at DESC 
LIMIT 10;
```

**Recent Events:**
```sql
SELECT event_type, COUNT(*) as count
FROM webhook_deliveries 
WHERE created_at > NOW() - INTERVAL '1 hour'
GROUP BY event_type;
```

---

## 🎯 BENEFITS FOR YOUR DEMO

### ✅ **Professional**
- Shows real API integration
- Demonstrates programmatic usage
- Proves automation capabilities

### ✅ **Repeatable**
- Run multiple times
- Consistent results
- No manual errors

### ✅ **Complete**
- End-to-end flow
- All components working together
- Real-time verification

### ✅ **Interactive**
- Can run during live demo
- Shows immediate results
- Validates each step

---

## 🆘 TROUBLESHOOTING

### Script fails at Step 1:
```bash
# Start services first
./start-all-services.sh
```

### No scenario created:
```bash
# Check API Gateway is running
docker ps | grep dco-gateway

# Check logs
docker logs dco-gateway
```

### No webhook deliveries:
```bash
# Check webhook service
docker logs webhook-management-service

# Verify webhooks exist
docker exec postgres psql -U postgres -d postgres -c "SELECT * FROM webhooks;"
```

### RabbitMQ queue error:
```bash
# Restart RabbitMQ
docker-compose restart rabbitmq
sleep 30
./run-e2e-demo.sh
```

---

## 🔄 RUNNING MULTIPLE TIMES

### Each run creates:
- ✅ 1 new scenario (unique name with timestamp)
- ✅ 1 new simulation
- ✅ 1+ event in RabbitMQ (consumed quickly)
- ✅ 1+ webhook delivery record

### Safe to run:
- ✅ Multiple times in a row
- ✅ During live demo
- ✅ For testing purposes
- ✅ To generate sample data

---

## 📊 QUICK COMMANDS

```bash
# Complete demo setup
./start-all-services.sh        # Start + auto-seed
./open-demo-tabs.sh            # Open browser tabs
./run-e2e-demo.sh              # Run E2E workflow

# Check results
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM scenario;"
docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) FROM webhook_deliveries;"

# Run workflow 5 times
for i in {1..5}; do ./run-e2e-demo.sh; sleep 3; done

# View webhook service logs
docker logs -f webhook-management-service
```

---

## 🎉 SUMMARY

| Feature | Description |
|---------|-------------|
| **Command** | `./run-e2e-demo.sh` |
| **Duration** | ~30 seconds |
| **Creates** | Scenario + Simulation + Events + Webhooks |
| **Verifies** | Complete data flow |
| **Repeatable** | ✅ Yes |
| **Safe** | ✅ Yes |
| **Live Demo** | ✅ Perfect! |

---

## 🚀 BOTTOM LINE

**To run complete E2E workflow:**
```bash
./run-e2e-demo.sh
```

**Shows:**
- ✅ API integration
- ✅ Database persistence
- ✅ Event-driven architecture
- ✅ Webhook delivery
- ✅ Complete observability

**Perfect for demonstrating the ENTIRE platform in 30 seconds!** 🎊

---

**Created:** November 4, 2025  
**Purpose:** Automated E2E demo workflow  
**Impact:** Professional, repeatable, complete workflow demonstration ✅
