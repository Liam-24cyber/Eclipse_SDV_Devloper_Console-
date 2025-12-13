# 📋 SDV Developer Console - Project Reorganization Guide

## 🎯 Executive Summary

This document provides a **comprehensive, production-ready reorganization plan** for the SDV Developer Console project. The plan ensures:
- ✅ **Zero Breaking Changes**: Both `./start-all-services.sh` and `./run-e2e-demo.sh` will work without modification
- ✅ **Professional Structure**: Clear separation of concerns
- ✅ **Maintainability**: Easy to understand and navigate
- ✅ **Documentation**: All essential files preserved and organized

---

## 📊 Current State Analysis

### Critical Files (MUST KEEP - Required for Project to Work)

#### **1. Core Service Directories** (8 services)
```
✅ dco-gateway/                    # API Gateway (GraphQL)
✅ developer-console-ui/           # Next.js Frontend
✅ message-queue-service/          # RabbitMQ Service
✅ scenario-library-service/       # Scenario Management
✅ tracks-management-service/      # Tracks Management
✅ webhook-management-service/     # Webhook Delivery (with metrics)
✅ minio/                          # Object Storage Config
✅ postgres/                       # Database Init & Seeds
```

**Dependencies:**
- Each service has `Dockerfile.app` or similar → referenced by `docker-compose.yml`
- Contains source code, Maven/Gradle configs, and application resources
- **REQUIRED** for `docker-compose build` and `docker-compose up`

---

#### **2. Docker & Orchestration**
```
✅ docker-compose.yml              # Main orchestration (319 lines)
✅ .m2/                            # Maven local repo for builds
```

**Dependencies:**
- `docker-compose.yml` references all Dockerfiles
- Used by `start-all-services.sh` (lines 48, 54, 65, 73, 89, 113, 121, 129, 138, 146, 155)
- Used by `run-e2e-demo.sh` (line 28: checks if services running)

---

#### **3. Monitoring Stack**
```
✅ prometheus.yml                  # Prometheus scrape config (57 lines)
✅ grafana/                        # Grafana provisioning
   ├── provisioning/
   │   ├── dashboards/
   │   │   ├── comprehensive-dashboard.json
   │   │   ├── e2e-dashboard.json
   │   │   └── dashboards.yml
   │   └── datasources/
   │       └── prometheus.yml
```

**Dependencies:**
- `prometheus.yml` mounted in `docker-compose.yml` (line 278)
- `grafana/provisioning/` mounted in `docker-compose.yml` (line 297)
- **REQUIRED** for monitoring to work

---

#### **4. Main Operational Scripts**
```
✅ start-all-services.sh           # Service startup (279 lines) - CRITICAL
✅ run-e2e-demo.sh                 # E2E testing (416 lines) - CRITICAL
```

**Dependencies:**
- `start-all-services.sh`:
  - Calls `docker-compose` commands
  - Checks service health via Docker
  - Seeds database
  
- `run-e2e-demo.sh`:
  - Checks Docker services
  - Inserts into Postgres
  - Publishes to RabbitMQ
  - Pushes metrics to Pushgateway

---

#### **5. Database & Configuration**
```
✅ minio/minio_keys.env            # MinIO credentials (referenced in docker-compose.yml)
✅ postgres/dco-init.sql           # Database schema
✅ postgres/seed-data.sql          # Sample data
```

**Dependencies:**
- `minio_keys.env` referenced in `docker-compose.yml` (line 80)
- SQL files run during Postgres container build

---

### Semi-Critical Files (SHOULD KEEP - Useful but Not Required)

```
⚠️ README.md                      # Project documentation
⚠️ CONTRIBUTING.md                # Contribution guidelines
⚠️ LICENSE.md                     # License information
⚠️ .gitignore                     # Git ignore rules
⚠️ package.json                   # Node.js dependencies (for e2e-api-server)
⚠️ e2e-api-server.js              # Mock webhook receiver
⚠️ SDV_E2E_Postman_Collection.json # API testing
⚠️ scripts/                       # Utility scripts
   ├── monitor-webhook-activity.sh
   ├── monitor-rabbitmq-live.sh
   ├── show-urls.sh
   ├── verify-dlq.sh
   └── etc.
```

---

### Non-Critical Files (CAN DELETE - Duplicates or Obsolete)

```
❌ grafana-dashboard-comprehensive.json  # DUPLICATE (exists in grafana/provisioning/)
❌ grafana-dashboard-e2e.json            # DUPLICATE (exists in grafana/provisioning/)
❌ 10-build-script.sh                    # Superseded by docker-compose build
❌ 20-deploy-script.sh                   # Deployment script (not local dev)
❌ 30-destroy-script.sh                  # Cleanup script (can use docker-compose down)
❌ rebuild-all.sh                        # Duplicate functionality
❌ rebuild-fixed-services.sh             # Duplicate functionality
❌ fix-webhook-service.sh                # One-time fix (already applied)
❌ fix-rabbitmq-queues.sh                # One-time fix (already applied)
❌ seed-database.sh                      # Handled by start-all-services.sh
❌ seed-default-webhook.sh               # One-time setup
❌ seed-test-webhook.sh                  # One-time setup
❌ set-java-17.sh                        # Environment setup (one-time)
❌ start-e2e-api.sh                      # Deprecated
❌ mock-webhook-server.js                # Duplicate of e2e-api-server.js
❌ metrics-exporter.sh                   # Not used
❌ publish-test-event.sh                 # Superseded by run-e2e-demo.sh
❌ purge-dlqs.sh                         # Maintenance script
❌ check-demo-readiness.sh               # One-time check
❌ check-status.sh                       # Duplicate functionality
❌ verify-dlq.sh                         # Duplicate in scripts/
❌ verify-restart-persistence.sh         # Duplicate in scripts/
❌ show-urls.sh                          # Duplicate in scripts/
❌ open-demo-tabs.sh                     # Optional utility
❌ cleanup-for-github.sh                 # One-time cleanup
❌ e2e-server.log                        # Log file (should be gitignored)
❌ node_modules/                         # Should be in .gitignore
❌ TESTING_SETUP_COMPLETE.txt            # Status file
❌ MONITORING_VERIFICATION_REPORT.md     # Historical report
❌ WEBHOOK_EVENT_ENHANCEMENT.md          # Historical documentation
❌ PROJECT_REORGANIZATION_PLAN.md        # Superseded by this guide
```

---

## 🎯 Recommended New Structure

```
SDV-Developer-Console/
│
├── 📄 README.md                          # Main documentation
├── 📄 CONTRIBUTING.md                    # Contribution guide
├── 📄 LICENSE.md                         # License
├── 📄 .gitignore                         # Git ignore
├── 📄 docker-compose.yml                 # Main orchestration
│
├── 📂 services/                          # All microservices
│   ├── dco-gateway/
│   ├── developer-console-ui/
│   ├── message-queue-service/
│   ├── scenario-library-service/
│   ├── tracks-management-service/
│   └── webhook-management-service/
│
├── 📂 infrastructure/                    # Infrastructure services
│   ├── postgres/
│   │   ├── Dockerfile.database
│   │   ├── dco-init.sql
│   │   └── seed-data.sql
│   └── minio/
│       ├── Dockerfile.minio
│       └── minio_keys.env
│
├── 📂 monitoring/                        # Monitoring & observability
│   ├── prometheus/
│   │   └── prometheus.yml
│   └── grafana/
│       └── provisioning/
│           ├── dashboards/
│           │   ├── comprehensive-dashboard.json
│           │   ├── e2e-dashboard.json
│           │   └── dashboards.yml
│           └── datasources/
│               └── prometheus.yml
│
├── 📂 scripts/                           # Utility scripts
│   ├── startup/
│   │   ├── start-all-services.sh        # Main startup
│   │   └── ci-health-check.sh           # CI health check
│   ├── testing/
│   │   ├── run-e2e-demo.sh              # E2E demo
│   │   ├── e2e-api-server.js            # Mock webhook receiver
│   │   └── package.json                 # Node dependencies
│   ├── monitoring/
│   │   ├── monitor-webhook-activity.sh
│   │   ├── monitor-rabbitmq-live.sh
│   │   └── show-monitoring-help.sh
│   └── utilities/
│       ├── show-urls.sh
│       ├── verify-dlq.sh
│       └── verify-restart-persistence.sh
│
├── 📂 docs/                              # Documentation
│   ├── architecture/
│   │   └── SDV-DCO-Architecture.png
│   ├── setup/
│   │   └── SETUP_GUIDE.md
│   ├── api/
│   │   └── SDV_E2E_Postman_Collection.json
│   └── images/                           # Screenshots
│       ├── developer-console-ui.png
│       ├── dco-gateway-playground.png
│       └── ...
│
├── 📂 .m2/                               # Maven local repository
└── 📂 .github/                           # GitHub workflows (if any)
```

---

## 🔧 Implementation Plan - Step-by-Step (No Breaking Changes)

### ✅ Phase 1: Preparation (Risk-Free)

```bash
# 1. Create backup
cd "/Users/ivanshalin/SDV - Additonal Extension"
git add -A
git commit -m "Pre-reorganization backup"
git tag pre-reorganization-$(date +%Y%m%d-%H%M%S)

# 2. Create new directory structure (empty folders)
mkdir -p services infrastructure/{postgres,minio}
mkdir -p monitoring/{prometheus,grafana}
mkdir -p scripts/{startup,testing,monitoring,utilities}
mkdir -p docs/{architecture,setup,api,images}
```

---

### ✅ Phase 2: Move Files (Preserving Git History)

**CRITICAL: Use `git mv` to preserve history!**

#### **Step 2.1: Move Services (Docker Compose Paths Change)**
```bash
# Move services
git mv dco-gateway services/
git mv developer-console-ui services/
git mv message-queue-service services/
git mv scenario-library-service services/
git mv tracks-management-service services/
git mv webhook-management-service services/
```

#### **Step 2.2: Move Infrastructure**
```bash
# Move infrastructure
git mv postgres infrastructure/
git mv minio infrastructure/
```

#### **Step 2.3: Move Monitoring**
```bash
# Move monitoring configs
git mv prometheus.yml monitoring/prometheus/
git mv grafana monitoring/
```

#### **Step 2.4: Move Scripts**
```bash
# Startup scripts
git mv start-all-services.sh scripts/startup/
git mv ci-health-check.sh scripts/startup/

# Testing scripts
git mv run-e2e-demo.sh scripts/testing/
git mv e2e-api-server.js scripts/testing/
git mv package.json scripts/testing/
git mv package-lock.json scripts/testing/

# Monitoring scripts
git mv scripts/monitor-webhook-activity.sh scripts/monitoring/
git mv scripts/monitor-rabbitmq-live.sh scripts/monitoring/
git mv scripts/show-monitoring-help.sh scripts/monitoring/

# Utilities
git mv scripts/show-urls.sh scripts/utilities/
git mv scripts/verify-dlq.sh scripts/utilities/
git mv scripts/verify-restart-persistence.sh scripts/utilities/

# Remove old scripts directory
rmdir scripts
```

#### **Step 2.5: Move Documentation**
```bash
# Move documentation
git mv images docs/
git mv SDV_E2E_Postman_Collection.json docs/api/
```

---

### ✅ Phase 3: Update File References (CRITICAL)

#### **File 1: docker-compose.yml**

Update all service build contexts and file references:

```yaml
# OLD:
services:
  dco-gateway:
    build:
      context: .
      dockerfile: dco-gateway/Dockerfile.app

# NEW:
services:
  dco-gateway:
    build:
      context: .
      dockerfile: services/dco-gateway/Dockerfile.app
```

**Search & Replace Patterns:**
```bash
# In docker-compose.yml
dco-gateway/Dockerfile.app           → services/dco-gateway/Dockerfile.app
developer-console-ui/Dockerfile      → services/developer-console-ui/Dockerfile
message-queue-service/Dockerfile.app → services/message-queue-service/Dockerfile.app
scenario-library-service/Dockerfile.app → services/scenario-library-service/Dockerfile.app
tracks-management-service/Dockerfile.app → services/tracks-management-service/Dockerfile.app
webhook-management-service/Dockerfile.app → services/webhook-management-service/Dockerfile.app
postgres/Dockerfile.database         → infrastructure/postgres/Dockerfile.database
minio/Dockerfile.minio               → infrastructure/minio/Dockerfile.minio
minio/minio_keys.env                 → infrastructure/minio/minio_keys.env
./prometheus.yml                     → ./monitoring/prometheus/prometheus.yml
./grafana/provisioning               → ./monitoring/grafana/provisioning
```

---

#### **File 2: Create Wrapper Scripts (Root Directory)**

Create **simple wrapper scripts** in the root so existing commands still work:

**`start-all-services.sh` (root)**
```bash
#!/bin/bash
# Wrapper script - delegates to actual implementation
exec "$(dirname "$0")/scripts/startup/start-all-services.sh" "$@"
```

**`run-e2e-demo.sh` (root)**
```bash
#!/bin/bash
# Wrapper script - delegates to actual implementation
exec "$(dirname "$0")/scripts/testing/run-e2e-demo.sh" "$@"
```

```bash
chmod +x start-all-services.sh run-e2e-demo.sh
```

**Result:** Users can still run `./start-all-services.sh` and `./run-e2e-demo.sh` from root!

---

#### **File 3: Update scripts/startup/start-all-services.sh**

No changes needed if wrapper approach is used! The script will work as-is because:
- It calls `docker-compose` commands (which read `docker-compose.yml` from current directory)
- All Docker commands use container names (not file paths)
- Database and service checks use Docker commands

---

#### **File 4: Update scripts/testing/run-e2e-demo.sh**

Minimal changes needed:
```bash
# Line 28: Check if services running
# OLD: if ! docker ps | grep -q postgres; then
# NEW: (no change - checks Docker container, not file path)

# All other commands are Docker/Postgres/RabbitMQ operations
# No file path dependencies!
```

---

### ✅ Phase 4: Update README.md

Update documentation to reflect new structure:

```markdown
## 🚀 Quick Start

### Start All Services
```bash
./start-all-services.sh
```

### Run E2E Demo
```bash
./run-e2e-demo.sh
```

## 📁 Project Structure

See [PROJECT_STRUCTURE.md](./docs/PROJECT_STRUCTURE.md) for details.

## 📊 Monitoring

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin)
- **Dashboards**: Available in `monitoring/grafana/provisioning/dashboards/`
```

---

### ✅ Phase 5: Clean Up

```bash
# Remove duplicate and obsolete files
rm -f grafana-dashboard-*.json
rm -f 10-build-script.sh 20-deploy-script.sh 30-destroy-script.sh
rm -f rebuild-*.sh fix-*.sh seed-*.sh set-java-17.sh
rm -f check-*.sh verify-*.sh show-urls.sh open-demo-tabs.sh
rm -f start-e2e-api.sh publish-test-event.sh purge-dlqs.sh
rm -f metrics-exporter.sh mock-webhook-server.js cleanup-for-github.sh
rm -f e2e-server.log TESTING_SETUP_COMPLETE.txt
rm -f MONITORING_VERIFICATION_REPORT.md WEBHOOK_EVENT_ENHANCEMENT.md
rm -f PROJECT_REORGANIZATION_PLAN.md

# Remove node_modules (will be reinstalled in scripts/testing/)
rm -rf node_modules

# Update .gitignore
cat >> .gitignore << 'EOF'

# Logs
*.log

# Dependencies
node_modules/
scripts/testing/node_modules/

# Temporary files
*.tmp
.DS_Store
EOF
```

---

### ✅ Phase 6: Verification

```bash
# 1. Verify Docker Compose syntax
docker-compose config

# 2. Test startup script
./start-all-services.sh

# 3. Wait for services to be healthy (2-3 minutes)

# 4. Test E2E demo
./run-e2e-demo.sh

# 5. Verify monitoring
curl http://localhost:9090/api/v1/query?query=up
curl http://localhost:3001/api/health

# 6. Check all services
docker-compose ps
```

---

## 📝 Final Checklist

Before considering reorganization complete:

- [ ] All services start successfully with `./start-all-services.sh`
- [ ] E2E demo runs successfully with `./run-e2e-demo.sh`
- [ ] Prometheus scrapes all service metrics
- [ ] Grafana dashboards load correctly
- [ ] All Docker builds succeed
- [ ] Database seeds correctly
- [ ] RabbitMQ queues are created
- [ ] Webhook deliveries are recorded
- [ ] No broken symbolic links or file references
- [ ] Git history preserved (use `git log --follow`)
- [ ] `.gitignore` updated to exclude generated files
- [ ] README.md updated with new structure

---

## 🔄 Rollback Plan

If anything breaks:

```bash
# Option 1: Revert to backup tag
git reset --hard pre-reorganization-YYYYMMDD-HHMMSS

# Option 2: Revert specific commit
git revert <commit-hash>

# Option 3: Restore from backup
git checkout pre-reorganization-YYYYMMDD-HHMMSS .
```

---

## 📈 Benefits of New Structure

### 1. **Clarity**
- Clear separation: services, infrastructure, monitoring, scripts, docs
- Easy to find files
- New developers onboard faster

### 2. **Maintainability**
- Related files grouped together
- Scripts organized by purpose
- Monitoring configs in one place

### 3. **Scalability**
- Easy to add new services
- Easy to add new scripts
- Easy to add new documentation

### 4. **Professional**
- Industry-standard layout
- Clean root directory
- Well-documented

### 5. **CI/CD Ready**
- Scripts organized by lifecycle stage
- Easy to integrate with GitHub Actions
- Clear build/test/deploy separation

---

## 🎯 Migration Commands (Copy-Paste Ready)

Here's the complete migration in one script:

```bash
#!/bin/bash
set -e

echo "🚀 Starting SDV Project Reorganization"
cd "/Users/ivanshalin/SDV - Additonal Extension"

# Backup
git add -A
git commit -m "Pre-reorganization backup" || true
git tag pre-reorganization-$(date +%Y%m%d-%H%M%S)

# Create structure
mkdir -p services infrastructure/{postgres,minio}
mkdir -p monitoring/{prometheus,grafana}
mkdir -p scripts/{startup,testing,monitoring,utilities}
mkdir -p docs/{architecture,setup,api}

# Move services
git mv dco-gateway services/
git mv developer-console-ui services/
git mv message-queue-service services/
git mv scenario-library-service services/
git mv tracks-management-service services/
git mv webhook-management-service services/

# Move infrastructure
git mv postgres infrastructure/
git mv minio infrastructure/

# Move monitoring
mkdir -p monitoring/prometheus
git mv prometheus.yml monitoring/prometheus/
git mv grafana monitoring/

# Move scripts (startup)
mkdir -p scripts/startup scripts/testing scripts/monitoring scripts/utilities
cp start-all-services.sh scripts/startup/
cp ci-health-check.sh scripts/startup/

# Move scripts (testing)
cp run-e2e-demo.sh scripts/testing/
cp e2e-api-server.js scripts/testing/
cp package.json scripts/testing/ || true
cp package-lock.json scripts/testing/ || true

# Move docs
git mv images docs/
cp SDV_E2E_Postman_Collection.json docs/api/ || true

# Update docker-compose.yml (you'll need to do this manually or with sed)
echo "⚠️  MANUAL STEP: Update docker-compose.yml paths"
echo "   Change all 'services/*' and 'infrastructure/*' paths"

# Create wrapper scripts
cat > start-all-services.sh << 'EOF'
#!/bin/bash
exec "$(dirname "$0")/scripts/startup/start-all-services.sh" "$@"
EOF

cat > run-e2e-demo.sh << 'EOF'
#!/bin/bash
exec "$(dirname "$0")/scripts/testing/run-e2e-demo.sh" "$@"
EOF

chmod +x start-all-services.sh run-e2e-demo.sh

echo "✅ Reorganization complete!"
echo "📋 Next: Update docker-compose.yml manually, then test"
```

---

## 🎓 Best Practices Applied

1. **Git History Preservation**: Use `git mv` for all moves
2. **Backward Compatibility**: Wrapper scripts maintain existing commands
3. **Clear Separation**: services / infrastructure / monitoring / scripts / docs
4. **Documentation**: Updated README and new guides
5. **Verification**: Comprehensive testing checklist
6. **Rollback Plan**: Multiple recovery options

---

## 📞 Support

If issues arise during reorganization:
1. Check Docker Compose syntax: `docker-compose config`
2. Review service logs: `docker-compose logs [service]`
3. Verify file paths in docker-compose.yml
4. Check wrapper scripts are executable
5. Consult rollback plan if needed

---

**Generated**: 2025
**Version**: 1.0
**Status**: Ready for Implementation ✅
