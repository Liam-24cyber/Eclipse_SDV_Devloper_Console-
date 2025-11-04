# ⚡ Quick Reference - Final Deployment Steps

## Current Status
✅ **GraphQL schema fixed** - Changed `type: TypeEnum` to `type: String`  
✅ **UI mock data removed** - Real API calls implemented  
✅ **Database status updated** - All scenarios set to 'CREATED'  
✅ **UI rebuilt and deployed** - New container running  
⏳ **Maven build in progress** - Building gateway with updated schema  
⏳ **Gateway rebuild pending** - Waiting for Maven to finish  

---

## Next Steps (In Order)

### 1️⃣ Wait for Maven Build
```bash
# Check if Maven build is still running
ps aux | grep maven
```

### 2️⃣ Rebuild Gateway Docker Image
```bash
cd /Users/ivanshalin/SDV\ Phase\ 2\ E2E/Eclipse_SDV_Devloper_Console-
docker-compose build dco-gateway
```

### 3️⃣ Restart Gateway Container
```bash
docker-compose up -d dco-gateway
```

### 4️⃣ Verify Gateway Logs (No TypeEnum Errors)
```bash
# Check for TypeEnum errors (should be NONE)
docker logs dco-gateway-app 2>&1 | grep -i "typeenum"

# Check for general errors
docker logs dco-gateway-app 2>&1 | grep -i "error" | tail -20

# Check startup success
docker logs dco-gateway-app --tail 30
```

### 5️⃣ Test GraphQL API
```bash
# Should return 16 scenarios with all types preserved
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { getScenarioLib { id name type status } }"}' | jq
```

### 6️⃣ Test UI in Browser
1. Open: http://localhost:3000/dco/scenario
2. Hard refresh: `Cmd+Shift+R` (clear cache)
3. **Expected:** 16 scenarios total (10 on page 1, 6 on page 2)
4. Navigate to: http://localhost:3000/dco/track
5. **Expected:** 13 tracks visible

---

## Success Indicators

### ✅ Gateway Logs Should Show:
```
Started DcoGatewayApplication in X.XXX seconds
GraphQL schema loaded successfully
No TypeEnum errors
No ValueInstantiationException errors
```

### ✅ API Response Should Show:
```json
{
  "data": {
    "getScenarioLib": [
      {
        "id": "1",
        "name": "Highway Overtake",
        "type": "URBAN_DRIVING",
        "status": "CREATED"
      },
      // ... 15 more scenarios
    ]
  }
}
```

### ✅ UI Should Show:
- **Scenario List Page:** "Showing 1-10 of 16"
- **Page 2:** "Showing 11-16 of 16"
- **Track List Page:** All 13 tracks visible
- **No errors in browser console**

---

## Troubleshooting

### If Maven Build Fails:
```bash
# Check Java version
java -version  # Should be Java 17

# Set Java 17
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

# Retry build
cd dco-gateway
mvn clean package -DskipTests
```

### If Gateway Container Won't Start:
```bash
# Check container status
docker ps -a | grep gateway

# View full logs
docker logs dco-gateway-app --tail 100

# Restart all services
docker-compose down
docker-compose up -d
```

### If API Still Returns Empty Data:
```bash
# Verify schema change made it into the image
docker exec -it dco-gateway-app cat /app/resources/graphql/schema.graphqls | grep "type:"

# Should show: type: String (not type: TypeEnum)
```

### If UI Still Shows No Data:
1. Check browser console for errors (F12 → Console tab)
2. Check Network tab - look for /graphql requests
3. Verify response data in Network tab
4. Clear browser cache completely
5. Try incognito/private window

---

## File Locations Reference

| Component | File Path |
|-----------|-----------|
| GraphQL Schema | `/dco-gateway/app/src/main/resources/graphql/schema.graphqls` |
| UI Scenario Service | `/developer-console-ui/app/services/functionScenario.services.ts` |
| UI Track Service | `/developer-console-ui/app/services/functionTrack.service.ts` |
| Database Init | `/postgres/dco-init.sql` |
| Docker Compose | `/docker-compose.yml` |

---

## Documentation Files Created

1. **COMPLETE_FIX_SUMMARY.md** - Full technical breakdown
2. **GRAPHQL_SCHEMA_FIX.md** - GraphQL TypeEnum fix details
3. **ROOT_CAUSE_ANALYSIS.md** - Root cause investigation
4. **API_VERIFICATION_REPORT.md** - API testing results
5. **UI_DATA_FIX_SUMMARY.md** - UI changes summary
6. **QUICK_REFERENCE.md** - This file (deployment checklist)

---

## Expected Timeline

- Maven build: ~5-10 minutes ⏳
- Docker rebuild: ~2-3 minutes
- Container restart: ~30 seconds
- Verification: ~5 minutes

**Total:** ~15-20 minutes from Maven completion to full verification

---

## The Fix in One Sentence

**Changed GraphQL schema from restrictive `type: TypeEnum` to flexible `type: String`, removing the Jackson serialization error that was dropping all scenario data at the API layer.**

---

**Ready to deploy?** Wait for Maven to finish, then run steps 2-6 above! 🚀
