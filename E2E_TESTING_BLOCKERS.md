# E2E Testing Blockers - SDV Developer Console
**Analysis Date:** November 3, 2025

## 🔴 CRITICAL ISSUES BLOCKING E2E TESTING

### Issue #1: RabbitMQ Queues NOT Being Created ⚠️
**Status:** BLOCKING  
**Impact:** Webhook service cannot start - crashes in restart loop

**Root Cause:**
The `message-queue-service` has RabbitMQ queue/exchange/binding **definitions** in `RabbitMQConfig.java` but they are **NEVER CREATED** because:
1. ✅ `@Bean` annotations exist for all queues/exchanges/bindings
2. ✅ `RabbitAdmin` bean is defined 
3. ❌ **BUT Spring is NOT connecting to RabbitMQ to create these resources**
4. ❌ No connection is established during startup
5. ❌ No logs showing queue/exchange creation

**Evidence:**
```bash
# RabbitMQ has ZERO queues
$ docker exec rabbitmq rabbitmqctl list_queues
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
# (empty - no queues exist!)
```

**What Should Happen:**
When `message-queue-service` starts, Spring AMQP should:
1. Connect to RabbitMQ
2. Create exchanges: `sdv.events`, `sdv.dlx`
3. Create queues: `scenario.events`, `track.events`, `simulation.events`, `webhook.events` + DLQs
4. Create bindings between exchanges and queues

**What's Actually Happening:**
- Service starts successfully
- No RabbitMQ connection established
- No queues created
- Webhook service tries to connect to non-existent queues → CRASHES

---

### Issue #2: Webhook Service Cannot Start Without Queues
**Status:** CONSEQUENCE OF ISSUE #1  
**Impact:** Service crashes on startup

**Error:**
```
Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; 
protocol method: #method<channel.close>(reply-code=404, 
reply-text=NOT_FOUND - no queue 'scenario.events' in vhost '/', 
class-id=50, method-id=10)
```

**Why It Crashes:**
The webhook service has `@RabbitListener` annotations that expect queues to exist:
```java
@RabbitListener(queues = RabbitMQConfig.SCENARIO_EVENTS_QUEUE)  // "scenario.events"
@RabbitListener(queues = RabbitMQConfig.TRACK_EVENTS_QUEUE)      // "track.events"
@RabbitListener(queues = RabbitMQConfig.SIMULATION_EVENTS_QUEUE) // "simulation.events"
```

When Spring tries to register these listeners, it does a passive queue declaration to verify the queue exists. Since the queues don't exist, it throws an exception and the container restarts.

---

## ✅ WHAT'S WORKING

1. **Database Tables** - All webhook tables exist:
   ```
   webhooks
   webhook_deliveries
   webhook_delivery_attempts
   webhook_event_types
   webhook_headers
   ```

2. **RabbitMQ Server** - Running and healthy

3. **Postgres** - Running and healthy

4. **All Other Services** - Healthy and running

---

## 🔧 ROOT CAUSE ANALYSIS

### Why message-queue-service Isn't Creating Queues

**Possible Reasons:**

1. **Missing Spring AMQP Auto-Configuration**
   - The `@AutoConfigureBefore(RabbitAutoConfiguration.class)` might be preventing auto-config
   
2. **No RabbitMQ Connection Being Made**
   - Service starts but doesn't connect to RabbitMQ
   - Beans are created but `RabbitAdmin` isn't initializing them
   
3. **Lazy Initialization**
   - Beans might be created but not initialized until first use
   
4. **Missing @EnableRabbit Annotation**
   - Service might need explicit `@EnableRabbit` to trigger queue creation

5. **Connection Factory Not Properly Configured**
   - RabbitMQ connection might not be established at startup

---

## 🎯 REQUIRED FIXES

### Fix #1: Ensure message-queue-service Creates RabbitMQ Resources

**Option A - Add Initialization Component** (RECOMMENDED)
```java
@Component
public class RabbitMQInitializer implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Force initialization of all AMQP beans
        rabbitAdmin.initialize();
        log.info("RabbitMQ resources initialized");
    }
}
```

**Option B - Add @EnableRabbit**
```java
@Configuration
@EnableRabbit  // Add this
@AutoConfigureBefore(RabbitAutoConfiguration.class)
public class RabbitMQConfig {
    // ... existing code
}
```

**Option C - Add Connection Listener**
```java
@Bean
public ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory();
    factory.setHost(rabbitHost);
    factory.setPort(rabbitPort);
    factory.setUsername(rabbitUsername);
    factory.setPassword(rabbitPassword);
    
    // Add connection listener to log when connected
    factory.addConnectionListener(connection -> {
        log.info("RabbitMQ connection established: {}", connection);
    });
    
    return factory;
}
```

### Fix #2: Add Missing Dependencies to Webhook Service

The webhook service declares queues but they should already exist. Instead:

**Remove Queue Beans from webhook-management-service:**
```java
// DELETE THIS ENTIRE FILE - queues should be created by message-queue-service
// webhook-management-service/app/src/main/java/com/tsystems/dco/config/RabbitMQConfig.java
```

**Only keep constants:**
```java
public class RabbitMQConfig {
    // Keep only constants, no @Bean annotations
    public static final String SCENARIO_EVENTS_QUEUE = "scenario.events";
    public static final String TRACK_EVENTS_QUEUE = "track.events";
    public static final String SIMULATION_EVENTS_QUEUE = "simulation.events";
}
```

---

## 📋 E2E TESTING FLOW (WHAT SHOULD WORK)

1. **User Simulation Request** → `dco-gateway:8080`
2. **Gateway** → Routes to `scenario-library-service:8082` or `tracks-management-service:8081`
3. **Service** → Publishes event to RabbitMQ exchange `sdv.events`
4. **RabbitMQ** → Routes event to appropriate queue (`scenario.events`, `track.events`, etc.)
5. **message-queue-service** → Consumes events, processes, re-publishes
6. **webhook-management-service** → Consumes events from queues
7. **Webhook Delivery** → Calls external webhook URLs

**Currently Broken At:** Step 4 - Queues don't exist so events have nowhere to go

---

## 🚀 PRIORITY ACTIONS

### IMMEDIATE (Do First)
1. ✅ Fix `message-queue-service` to actually create RabbitMQ queues
2. ✅ Verify queues are created with `docker exec rabbitmq rabbitmqctl list_queues`
3. ✅ Remove duplicate queue declarations from webhook-management-service

### SECONDARY (Do After Queues Work)
4. Test event publishing from scenario/track services
5. Verify message-queue-service consumes and processes events
6. Verify webhook-management-service receives events
7. Test actual webhook delivery

---

## 📊 CURRENT STATE SUMMARY

| Component | Status | Notes |
|-----------|--------|-------|
| RabbitMQ Server | ✅ Healthy | Running, no queues |
| Postgres | ✅ Healthy | All tables exist |
| message-queue-service | ⚠️ Running | NOT creating queues |
| webhook-management-service | 🔴 Crash Loop | Waiting for queues |
| scenario-library-service | ✅ Healthy | Ready to publish |
| tracks-management-service | ✅ Healthy | Ready to publish |
| dco-gateway | ✅ Healthy | Ready to route |

**Blocking Issue:** RabbitMQ queues not being created by message-queue-service

---

## 🔍 DEBUG COMMANDS

```bash
# Check if queues exist
docker exec rabbitmq rabbitmqctl list_queues

# Check exchanges
docker exec rabbitmq rabbitmqctl list_exchanges

# Check bindings
docker exec rabbitmq rabbitmqctl list_bindings

# Check message-queue-service logs
docker-compose logs message-queue-service | grep -i "rabbit\|queue\|exchange"

# Check webhook service crash reason
docker-compose logs webhook-management-service | grep -i "error\|caused"

# Check database tables
docker-compose exec postgres psql -U postgres -d postgres -c '\dt'
```

---

## 💡 NEXT STEPS

1. Implement one of the fixes for message-queue-service queue creation
2. Rebuild and restart message-queue-service
3. Verify queues are created in RabbitMQ
4. Start webhook-management-service (should work now)
5. Test E2E flow from UI → Gateway → Services → RabbitMQ → Webhooks
