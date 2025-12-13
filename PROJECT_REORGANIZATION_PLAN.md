# SDV Developer Console - Project Reorganization Plan

## 📋 Executive Summary

This document provides a comprehensive plan to reorganize the project root directory for better maintainability, clarity, and professional standards. The plan categorizes all files and proposes a clean, intuitive folder structure.

---

## 🎯 Goals

1. **Clarity**: Developers should immediately understand the project structure
2. **Maintainability**: Related files grouped logically
3. **Professionalism**: Industry-standard organization
4. **CI/CD Ready**: Clear separation of automation and scripts
5. **Documentation First**: Easy access to getting started guides

---

## 📊 Current State Analysis

### Root Directory Files (43 files/folders)
The root currently contains:
- 6 Java services (microservices)
- 33+ shell scripts (mixed purposes)
- 8 configuration files
- 6 documentation files
- Infrastructure folders (postgres, minio, grafana)
- Build artifacts (node_modules, logs, .m2)

**Problem**: Too many files at root level, unclear organization, hard to navigate

---

## 🗂️ Proposed Directory Structure

```
sdv-developer-console/
├── README.md                           # Main project README (keep at root)
├── LICENSE.md                          # License (keep at root)
├── CONTRIBUTING.md                     # Contribution guide (keep at root)
├── docker-compose.yml                  # Main orchestration (keep at root)
├── .gitignore                          # Git ignore (keep at root)
├── .github/                            # GitHub Actions workflows
│   └── workflows/
│       └── test.yml
│
├── docs/                               # 📚 All Documentation
│   ├── getting-started/
│   │   ├── QUICKSTART.md
│   │   └── SETUP.md
│   ├── architecture/
│   │   ├── ARCHITECTURE.md
│   │   └── SERVICE_DEPENDENCIES.md
│   ├── monitoring/
│   │   ├── MONITORING_GUIDE.md
│   │   └── MONITORING_VERIFICATION_REPORT.md
│   ├── testing/
│   │   ├── TESTING_README.md
│   │   ├── TESTING_QUICKSTART.md
│   │   └── TESTING_IMPLEMENTATION_GUIDE.md
│   ├── deployment/
│   │   └── DEPLOYMENT_GUIDE.md
│   ├── features/
│   │   └── WEBHOOK_EVENT_ENHANCEMENT.md
│   └── images/                         # Move from root
│       ├── SDV-DCO-Architecture.png
│       ├── *.png
│
├── services/                           # 🏗️ All Microservices
│   ├── dco-gateway/
│   ├── developer-console-ui/
│   ├── message-queue-service/
│   ├── scenario-library-service/
│   ├── tracks-management-service/
│   └── webhook-management-service/
│
├── infrastructure/                     # 🔧 Infrastructure Components
│   ├── postgres/
│   │   ├── Dockerfile.database
│   │   ├── dco-init.sql
│   │   └── seed-data.sql
│   ├── minio/
│   │   ├── Dockerfile.minio
│   │   └── minio_keys.env
│   ├── grafana/
│   │   └── provisioning/
│   ├── prometheus/
│   │   └── prometheus.yml
│   └── rabbitmq/                       # (if applicable)
│
├── scripts/                            # 🚀 Automation Scripts
│   ├── README.md
│   │
│   ├── lifecycle/                      # Service lifecycle management
│   │   ├── build.sh                    # (was 10-build-script.sh)
│   │   ├── deploy.sh                   # (was 20-deploy-script.sh)
│   │   ├── destroy.sh                  # (was 30-destroy-script.sh)
│   │   ├── rebuild-all.sh
│   │   ├── rebuild-fixed-services.sh
│   │   ├── start-all-services.sh
│   │   └── stop-all-services.sh
│   │
│   ├── database/                       # Database operations
│   │   ├── seed-database.sh
│   │   ├── seed-default-webhook.sh
│   │   └── seed-test-webhook.sh
│   │
│   ├── testing/                        # Testing automation
│   │   ├── run-all-tests.sh
│   │   ├── run-e2e-demo.sh
│   │   ├── check-demo-readiness.sh
│   │   └── publish-test-event.sh
│   │
│   ├── monitoring/                     # Monitoring & observability
│   │   ├── monitor-rabbitmq-live.sh
│   │   ├── monitor-webhook-activity.sh
│   │   ├── show-monitoring-help.sh
│   │   └── metrics-exporter.sh
│   │
│   ├── verification/                   # Health checks & verification
│   │   ├── check-status.sh
│   │   ├── ci-health-check.sh
│   │   ├── verify-dlq.sh
│   │   └── verify-restart-persistence.sh
│   │
│   ├── troubleshooting/                # Fix & repair scripts
│   │   ├── fix-rabbitmq-queues.sh
│   │   ├── fix-webhook-service.sh
│   │   └── purge-dlqs.sh
│   │
│   ├── utilities/                      # General utilities
│   │   ├── show-urls.sh
│   │   ├── open-demo-tabs.sh
│   │   ├── cleanup-for-github.sh
│   │   └── set-java-17.sh
│   │
│   └── demo/                           # Demo & presentation
│       ├── start-e2e-api.sh
│       └── mock-webhook-server.js
│
├── config/                             # 🔐 Configuration Files
│   ├── prometheus.yml                  # (move from root)
│   ├── grafana/
│   │   ├── grafana-dashboard-comprehensive.json
│   │   └── grafana-dashboard-e2e.json
│   └── postman/
│       └── SDV_E2E_Postman_Collection.json
│
├── tools/                              # 🛠️ Development Tools
│   ├── e2e-api-server.js
│   ├── mock-webhook-server.js
│   ├── package.json
│   └── package-lock.json
│
├── .archive/                           # 📦 Temporary/Historical Files
│   ├── TESTING_SETUP_COMPLETE.txt
│   └── e2e-server.log
│
└── build/                              # 🏭 Build Artifacts (gitignored)
    ├── .m2/
    └── node_modules/

```

---

## 📝 Detailed File Categorization

### ✅ KEEP AT ROOT (Essential Files)
These files should remain at root for standard conventions:

| File | Reason |
|------|--------|
| `README.md` | Main project entry point - industry standard |
| `LICENSE.md` | Legal requirement - must be visible |
| `CONTRIBUTING.md` | Contribution guidelines - expected at root |
| `docker-compose.yml` | Docker orchestration - standard location |
| `.gitignore` | Git configuration - must be at root |
| `.github/` | GitHub Actions - required location |
| `package.json` | Node.js dependencies for tools - standard |

### 🗂️ MOVE TO `docs/`
Documentation should be centralized:

| Current Location | New Location | Purpose |
|-----------------|--------------|---------|
| `WEBHOOK_EVENT_ENHANCEMENT.md` | `docs/features/WEBHOOK_EVENT_ENHANCEMENT.md` | Feature documentation |
| `MONITORING_VERIFICATION_REPORT.md` | `docs/monitoring/MONITORING_VERIFICATION_REPORT.md` | Monitoring guide |
| `images/` | `docs/images/` | Centralize all documentation assets |
| *(create new)* | `docs/getting-started/QUICKSTART.md` | Extract from README |
| *(create new)* | `docs/architecture/ARCHITECTURE.md` | Extract from README |

### 🏗️ MOVE TO `services/`
All microservices in one place:

- `dco-gateway/` → `services/dco-gateway/`
- `developer-console-ui/` → `services/developer-console-ui/`
- `message-queue-service/` → `services/message-queue-service/`
- `scenario-library-service/` → `services/scenario-library-service/`
- `tracks-management-service/` → `services/tracks-management-service/`
- `webhook-management-service/` → `services/webhook-management-service/`

### 🔧 MOVE TO `infrastructure/`
Infrastructure components:

| Current | New Location |
|---------|-------------|
| `postgres/` | `infrastructure/postgres/` |
| `minio/` | `infrastructure/minio/` |
| `grafana/` | `infrastructure/grafana/` |
| `prometheus.yml` | `infrastructure/prometheus/prometheus.yml` |

### 🚀 REORGANIZE `scripts/`
Group scripts by purpose:

#### Lifecycle Scripts → `scripts/lifecycle/`
- `10-build-script.sh` → `scripts/lifecycle/build.sh`
- `20-deploy-script.sh` → `scripts/lifecycle/deploy.sh`
- `30-destroy-script.sh` → `scripts/lifecycle/destroy.sh`
- `rebuild-all.sh` → `scripts/lifecycle/rebuild-all.sh`
- `rebuild-fixed-services.sh` → `scripts/lifecycle/rebuild-fixed-services.sh`
- `start-all-services.sh` → `scripts/lifecycle/start-all-services.sh`

#### Database Scripts → `scripts/database/`
- `seed-database.sh` → `scripts/database/seed-database.sh`
- `seed-default-webhook.sh` → `scripts/database/seed-default-webhook.sh`
- `seed-test-webhook.sh` → `scripts/database/seed-test-webhook.sh`

#### Testing Scripts → `scripts/testing/`
- `run-e2e-demo.sh` → `scripts/testing/run-e2e-demo.sh`
- `check-demo-readiness.sh` → `scripts/testing/check-demo-readiness.sh`
- `publish-test-event.sh` → `scripts/testing/publish-test-event.sh`
- `scripts/run-all-tests.sh` → `scripts/testing/run-all-tests.sh`

#### Monitoring Scripts → `scripts/monitoring/`
- `scripts/monitor-rabbitmq-live.sh` → `scripts/monitoring/monitor-rabbitmq-live.sh`
- `scripts/monitor-webhook-activity.sh` → `scripts/monitoring/monitor-webhook-activity.sh`
- `scripts/show-monitoring-help.sh` → `scripts/monitoring/show-monitoring-help.sh`
- `metrics-exporter.sh` → `scripts/monitoring/metrics-exporter.sh`

#### Verification Scripts → `scripts/verification/`
- `check-status.sh` → `scripts/verification/check-status.sh`
- `ci-health-check.sh` → `scripts/verification/ci-health-check.sh`
- `verify-dlq.sh` → `scripts/verification/verify-dlq.sh`
- `verify-restart-persistence.sh` → `scripts/verification/verify-restart-persistence.sh`
- `scripts/verify-dlq.sh` → *(merge with above)*

#### Troubleshooting Scripts → `scripts/troubleshooting/`
- `fix-rabbitmq-queues.sh` → `scripts/troubleshooting/fix-rabbitmq-queues.sh`
- `fix-webhook-service.sh` → `scripts/troubleshooting/fix-webhook-service.sh`
- `purge-dlqs.sh` → `scripts/troubleshooting/purge-dlqs.sh`

#### Utility Scripts → `scripts/utilities/`
- `show-urls.sh` → `scripts/utilities/show-urls.sh`
- `scripts/show-urls.sh` → *(merge with above)*
- `open-demo-tabs.sh` → `scripts/utilities/open-demo-tabs.sh`
- `cleanup-for-github.sh` → `scripts/utilities/cleanup-for-github.sh`
- `set-java-17.sh` → `scripts/utilities/set-java-17.sh`

#### Demo Scripts → `scripts/demo/`
- `start-e2e-api.sh` → `scripts/demo/start-e2e-api.sh`

### 🔐 MOVE TO `config/`
Configuration files:

| Current | New Location |
|---------|-------------|
| `prometheus.yml` | `config/prometheus.yml` |
| `grafana-dashboard-comprehensive.json` | `config/grafana/grafana-dashboard-comprehensive.json` |
| `grafana-dashboard-e2e.json` | `config/grafana/grafana-dashboard-e2e.json` |
| `SDV_E2E_Postman_Collection.json` | `config/postman/SDV_E2E_Postman_Collection.json` |

### 🛠️ MOVE TO `tools/`
Development tools:

- `e2e-api-server.js` → `tools/e2e-api-server.js`
- `mock-webhook-server.js` → `tools/mock-webhook-server.js`
- `package.json` → `tools/package.json` *(if only used for tools)*
- `package-lock.json` → `tools/package-lock.json`

### 📦 ARCHIVE OR REMOVE

#### Archive (`.archive/`)
Historical or temporary files that might be useful:

- `TESTING_SETUP_COMPLETE.txt` → `.archive/TESTING_SETUP_COMPLETE.txt`
- `e2e-server.log` → *(gitignored, remove from tracking)*

#### Remove/Gitignore
These should be in `.gitignore`:

- `node_modules/` → **DELETE** (regenerate with `npm install`)
- `.m2/` → **DELETE** (Maven local cache)
- `e2e-server.log` → **DELETE** (generated file)

---

## 🔄 Migration Steps

### Phase 1: Backup & Preparation
```bash
# 1. Create backup
cp -r "/Users/ivanshalin/SDV - Additonal Extension" "/Users/ivanshalin/SDV - Additonal Extension.backup"

# 2. Commit current state
git add -A
git commit -m "Pre-reorganization backup"

# 3. Create a new branch for reorganization
git checkout -b project-reorganization
```

### Phase 2: Create New Directory Structure
```bash
# Create new directories
mkdir -p docs/{getting-started,architecture,monitoring,testing,deployment,features}
mkdir -p services
mkdir -p infrastructure/{postgres,minio,grafana,prometheus}
mkdir -p scripts/{lifecycle,database,testing,monitoring,verification,troubleshooting,utilities,demo}
mkdir -p config/{grafana,postman}
mkdir -p tools
mkdir -p .archive
```

### Phase 3: Move Services
```bash
# Move all microservices
mv dco-gateway services/
mv developer-console-ui services/
mv message-queue-service services/
mv scenario-library-service services/
mv tracks-management-service services/
mv webhook-management-service services/
```

### Phase 4: Move Infrastructure
```bash
# Move infrastructure components
mv postgres infrastructure/
mv minio infrastructure/
mv grafana infrastructure/
mkdir -p infrastructure/prometheus
mv prometheus.yml infrastructure/prometheus/
```

### Phase 5: Reorganize Scripts
```bash
# Lifecycle
mv 10-build-script.sh scripts/lifecycle/build.sh
mv 20-deploy-script.sh scripts/lifecycle/deploy.sh
mv 30-destroy-script.sh scripts/lifecycle/destroy.sh
mv rebuild-all.sh scripts/lifecycle/
mv rebuild-fixed-services.sh scripts/lifecycle/
mv start-all-services.sh scripts/lifecycle/

# Database
mv seed-database.sh scripts/database/
mv seed-default-webhook.sh scripts/database/
mv seed-test-webhook.sh scripts/database/

# Testing
mv run-e2e-demo.sh scripts/testing/
mv check-demo-readiness.sh scripts/testing/
mv publish-test-event.sh scripts/testing/
mv scripts/run-all-tests.sh scripts/testing/

# Monitoring
mv scripts/monitor-rabbitmq-live.sh scripts/monitoring/
mv scripts/monitor-webhook-activity.sh scripts/monitoring/
mv scripts/show-monitoring-help.sh scripts/monitoring/
mv metrics-exporter.sh scripts/monitoring/

# Verification
mv check-status.sh scripts/verification/
mv ci-health-check.sh scripts/verification/
mv verify-dlq.sh scripts/verification/
mv verify-restart-persistence.sh scripts/verification/
rm scripts/verify-dlq.sh  # Duplicate

# Troubleshooting
mv fix-rabbitmq-queues.sh scripts/troubleshooting/
mv fix-webhook-service.sh scripts/troubleshooting/
mv purge-dlqs.sh scripts/troubleshooting/

# Utilities
mv show-urls.sh scripts/utilities/
rm scripts/show-urls.sh  # Duplicate
mv open-demo-tabs.sh scripts/utilities/
mv cleanup-for-github.sh scripts/utilities/
mv set-java-17.sh scripts/utilities/

# Demo
mv start-e2e-api.sh scripts/demo/
```

### Phase 6: Move Configuration
```bash
mv grafana-dashboard-comprehensive.json config/grafana/
mv grafana-dashboard-e2e.json config/grafana/
mv SDV_E2E_Postman_Collection.json config/postman/
```

### Phase 7: Move Documentation
```bash
mv WEBHOOK_EVENT_ENHANCEMENT.md docs/features/
mv MONITORING_VERIFICATION_REPORT.md docs/monitoring/
mv images docs/
mv TESTING_SETUP_COMPLETE.txt .archive/
```

### Phase 8: Move Tools
```bash
mv e2e-api-server.js tools/
mv mock-webhook-server.js tools/
# If package.json is only for tools:
mv package.json tools/
mv package-lock.json tools/
```

### Phase 9: Update References
After moving files, update references in:

1. **docker-compose.yml**
   - Update all service `build.context` paths to use `services/` prefix
   - Update volume mounts to use `infrastructure/` prefix

2. **All scripts**
   - Update relative paths to account for new locations
   - Update README references

3. **GitHub Actions** (`.github/workflows/test.yml`)
   - Update paths if needed

4. **Documentation**
   - Update image paths in markdown files
   - Update script references

### Phase 10: Clean Up
```bash
# Remove build artifacts
rm -rf node_modules
rm -rf .m2
rm -f e2e-server.log

# Test that everything still works
./scripts/lifecycle/build.sh
./scripts/lifecycle/deploy.sh
```

### Phase 11: Update .gitignore
Ensure these are ignored:
```
# Build artifacts
build/
node_modules/
.m2/

# Logs
*.log
logs/

# IDE
.vscode/
.idea/
```

### Phase 12: Test & Verify
```bash
# Run tests
./scripts/testing/run-all-tests.sh

# Start services
./scripts/lifecycle/start-all-services.sh

# Verify health
./scripts/verification/check-status.sh

# Run E2E demo
./scripts/testing/run-e2e-demo.sh
```

### Phase 13: Commit Changes
```bash
git add -A
git commit -m "refactor: reorganize project structure for better maintainability

- Move microservices to services/ directory
- Organize scripts by purpose (lifecycle, testing, monitoring, etc.)
- Centralize documentation in docs/ directory
- Separate infrastructure components
- Create config/ directory for configuration files
- Move development tools to tools/ directory
- Archive historical files
- Update all path references
"

# Create PR or merge
git push origin project-reorganization
```

---

## 📋 Updated Scripts Master Reference

After reorganization, here's the new script locations reference:

### Lifecycle Management
```bash
./scripts/lifecycle/build.sh              # Build all services
./scripts/lifecycle/deploy.sh             # Deploy services
./scripts/lifecycle/destroy.sh            # Tear down environment
./scripts/lifecycle/start-all-services.sh # Start all services
./scripts/lifecycle/rebuild-all.sh        # Rebuild everything
```

### Testing & Demo
```bash
./scripts/testing/run-e2e-demo.sh        # Run E2E demonstration
./scripts/testing/run-all-tests.sh       # Execute all tests
./scripts/testing/check-demo-readiness.sh # Verify demo readiness
./scripts/testing/publish-test-event.sh   # Publish test event
```

### Monitoring
```bash
./scripts/monitoring/monitor-rabbitmq-live.sh    # Monitor RabbitMQ
./scripts/monitoring/monitor-webhook-activity.sh # Monitor webhooks
./scripts/monitoring/show-monitoring-help.sh     # Monitoring guide
```

### Verification
```bash
./scripts/verification/check-status.sh            # Check service status
./scripts/verification/ci-health-check.sh         # CI health checks
./scripts/verification/verify-dlq.sh              # Verify DLQ
./scripts/verification/verify-restart-persistence.sh # Test persistence
```

### Troubleshooting
```bash
./scripts/troubleshooting/fix-rabbitmq-queues.sh # Fix RabbitMQ issues
./scripts/troubleshooting/fix-webhook-service.sh # Fix webhook service
./scripts/troubleshooting/purge-dlqs.sh          # Purge DLQs
```

### Database
```bash
./scripts/database/seed-database.sh          # Seed main database
./scripts/database/seed-default-webhook.sh   # Seed default webhook
./scripts/database/seed-test-webhook.sh      # Seed test webhook
```

### Utilities
```bash
./scripts/utilities/show-urls.sh           # Show all service URLs
./scripts/utilities/open-demo-tabs.sh      # Open demo in browser
./scripts/utilities/cleanup-for-github.sh  # Clean up for GitHub
./scripts/utilities/set-java-17.sh         # Set Java 17
```

---

## 🎯 Benefits of New Structure

### For New Developers
- **Clear Entry Point**: README.md at root with immediate links to getting started
- **Logical Grouping**: Scripts organized by purpose, easy to find
- **Centralized Docs**: All documentation in one place

### For Maintenance
- **Easier Navigation**: Less clutter at root level
- **Better IDE Experience**: Services and infrastructure clearly separated
- **Consistent Patterns**: Scripts follow predictable naming and location

### For CI/CD
- **Clear Paths**: Automated systems know exactly where to find things
- **Separation of Concerns**: Build, test, and deployment scripts clearly separated
- **Configuration Management**: All config files in dedicated directory

### For Operations
- **Monitoring Scripts**: Easy to find monitoring and troubleshooting tools
- **Infrastructure as Code**: Clear separation of infrastructure components
- **Environment Management**: Configuration files organized and versionable

---

## 📚 Updated README Structure

After reorganization, update the main README.md to include:

```markdown
# SDV Developer Console

## 📁 Project Structure
```
sdv-developer-console/
├── services/          # All microservices
├── infrastructure/    # Postgres, MinIO, Grafana, etc.
├── scripts/           # Automation scripts (see scripts/README.md)
├── docs/              # Documentation
├── config/            # Configuration files
└── tools/             # Development tools
```

## 🚀 Quick Start
See [docs/getting-started/QUICKSTART.md](docs/getting-started/QUICKSTART.md)

## 📖 Documentation
- [Architecture](docs/architecture/ARCHITECTURE.md)
- [Testing Guide](docs/testing/TESTING_README.md)
- [Monitoring Guide](docs/monitoring/MONITORING_GUIDE.md)
- [Deployment Guide](docs/deployment/DEPLOYMENT_GUIDE.md)

## 🛠️ Common Commands
```bash
# Build and start
./scripts/lifecycle/build.sh
./scripts/lifecycle/start-all-services.sh

# Run E2E demo
./scripts/testing/run-e2e-demo.sh

# Monitor services
./scripts/monitoring/monitor-webhook-activity.sh
./scripts/verification/check-status.sh

# View all URLs
./scripts/utilities/show-urls.sh
```
```

---

## ✅ Validation Checklist

Before considering the reorganization complete, verify:

- [ ] All services build successfully
- [ ] docker-compose.yml works with updated paths
- [ ] All scripts execute from their new locations
- [ ] Documentation links are updated
- [ ] GitHub Actions still pass
- [ ] E2E demo runs successfully
- [ ] Monitoring and metrics work
- [ ] All path references are updated
- [ ] .gitignore is comprehensive
- [ ] README reflects new structure

---

## 🔮 Future Improvements

After reorganization, consider:

1. **Makefile**: Add a Makefile for common commands
   ```makefile
   build:
       ./scripts/lifecycle/build.sh
   
   start:
       ./scripts/lifecycle/start-all-services.sh
   
   test:
       ./scripts/testing/run-all-tests.sh
   
   e2e:
       ./scripts/testing/run-e2e-demo.sh
   ```

2. **Development Containers**: Add `.devcontainer/` for VS Code
3. **Documentation Site**: Use MkDocs or similar for docs/
4. **Pre-commit Hooks**: Add `.pre-commit-config.yaml`
5. **Docker Registry**: Organize service images with tags

---

## 📞 Questions & Support

If you have questions about this reorganization plan:
1. Check the [FAQ](docs/getting-started/FAQ.md)
2. Review the [Architecture Documentation](docs/architecture/ARCHITECTURE.md)
3. Contact the maintainers

---

**Document Version**: 1.0  
**Last Updated**: 2025-01-XX  
**Author**: SDV Developer Console Team
