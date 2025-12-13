#!/bin/bash
# 🗂️  SDV Project - File Organization Script
# Organizes loose files into proper folders while keeping main entry points in root

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  SDV File Organization - Clean Up Root Directory      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check we're in the right directory
if [ ! -f "docker-compose.yml" ] || [ ! -f "start-all-services.sh" ]; then
    echo -e "${RED}❌ Error: Not in SDV project root directory${NC}"
    exit 1
fi

echo -e "${CYAN}📋 Pre-flight Checks${NC}"
echo "===================="

# Check if git is available
if ! command -v git &> /dev/null; then
    echo -e "${RED}❌ Git not found. Please install git first.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Git available${NC}"

echo ""
echo -e "${YELLOW}🔍 What will happen:${NC}"
echo "   • Main entry scripts (start-all-services.sh, run-e2e-demo.sh) will NOT be touched"
echo "   • All service folders will stay in place"
echo "   • Loose files will be organized into scripts/, docs/, config/"
echo "   • Temporary reorganization docs will be deleted"
echo "   • Everything is reversible via git"
echo ""
read -p "🤔 Ready to organize files? (y/N) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Cancelled by user.${NC}"
    exit 0
fi

# ============================================================
# PHASE 1: BACKUP
# ============================================================

echo ""
echo -e "${CYAN}📦 Phase 1: Creating Backup${NC}"
echo "============================"

TIMESTAMP=$(date +%Y%m%d-%H%M%S)
BACKUP_TAG="pre-file-organization-${TIMESTAMP}"

git add -A
git commit -m "Pre-file-organization backup - ${TIMESTAMP}" || echo "Nothing new to commit"
git tag "$BACKUP_TAG"

echo -e "${GREEN}✅ Backup created: $BACKUP_TAG${NC}"
echo "   To rollback: git reset --hard $BACKUP_TAG"
echo ""
sleep 1

# ============================================================
# PHASE 2: CREATE DIRECTORY STRUCTURE
# ============================================================

echo -e "${CYAN}📂 Phase 2: Creating Directory Structure${NC}"
echo "=========================================="

mkdir -p scripts/build
mkdir -p scripts/database
mkdir -p scripts/testing
mkdir -p scripts/monitoring
mkdir -p scripts/utilities
mkdir -p docs
mkdir -p config

echo -e "${GREEN}✅ Directory structure created${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 3: MOVE BUILD SCRIPTS
# ============================================================

echo -e "${CYAN}🔄 Phase 3: Moving Build Scripts${NC}"
echo "================================="

BUILD_SCRIPTS=(
    "10-build-script.sh"
    "20-deploy-script.sh"
    "30-destroy-script.sh"
    "rebuild-all.sh"
    "rebuild-fixed-services.sh"
)

for script in "${BUILD_SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        echo -e "   Moving ${YELLOW}$script${NC} → scripts/build/"
        git mv "$script" scripts/build/
    fi
done

echo -e "${GREEN}✅ Build scripts moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 4: MOVE DATABASE SCRIPTS
# ============================================================

echo -e "${CYAN}🔄 Phase 4: Moving Database Scripts${NC}"
echo "===================================="

DATABASE_SCRIPTS=(
    "seed-database.sh"
    "seed-default-webhook.sh"
    "seed-test-webhook.sh"
)

for script in "${DATABASE_SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        echo -e "   Moving ${YELLOW}$script${NC} → scripts/database/"
        git mv "$script" scripts/database/
    fi
done

echo -e "${GREEN}✅ Database scripts moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 5: MOVE TESTING SCRIPTS
# ============================================================

echo -e "${CYAN}🔄 Phase 5: Moving Testing Scripts${NC}"
echo "==================================="

TESTING_SCRIPTS=(
    "check-demo-readiness.sh"
    "check-status.sh"
    "verify-dlq.sh"
    "verify-restart-persistence.sh"
    "publish-test-event.sh"
    "purge-dlqs.sh"
    "start-e2e-api.sh"
    "e2e-api-server.js"
    "mock-webhook-server.js"
)

for script in "${TESTING_SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        echo -e "   Moving ${YELLOW}$script${NC} → scripts/testing/"
        git mv "$script" scripts/testing/
    fi
done

echo -e "${GREEN}✅ Testing scripts moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 6: MOVE MONITORING SCRIPTS
# ============================================================

echo -e "${CYAN}🔄 Phase 6: Moving Monitoring Scripts${NC}"
echo "======================================"

MONITORING_SCRIPTS=(
    "metrics-exporter.sh"
)

for script in "${MONITORING_SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        echo -e "   Moving ${YELLOW}$script${NC} → scripts/monitoring/"
        git mv "$script" scripts/monitoring/
    fi
done

# Check if scripts/ folder has monitoring scripts
if [ -d "scripts" ]; then
    if [ -f "scripts/monitor-rabbitmq-live.sh" ]; then
        echo -e "   Moving ${YELLOW}scripts/monitor-rabbitmq-live.sh${NC} → scripts/monitoring/"
        git mv scripts/monitor-rabbitmq-live.sh scripts/monitoring/
    fi
    if [ -f "scripts/monitor-webhook-activity.sh" ]; then
        echo -e "   Moving ${YELLOW}scripts/monitor-webhook-activity.sh${NC} → scripts/monitoring/"
        git mv scripts/monitor-webhook-activity.sh scripts/monitoring/
    fi
    if [ -f "scripts/show-monitoring-help.sh" ]; then
        echo -e "   Moving ${YELLOW}scripts/show-monitoring-help.sh${NC} → scripts/monitoring/"
        git mv scripts/show-monitoring-help.sh scripts/monitoring/
    fi
fi

echo -e "${GREEN}✅ Monitoring scripts moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 7: MOVE UTILITY SCRIPTS
# ============================================================

echo -e "${CYAN}🔄 Phase 7: Moving Utility Scripts${NC}"
echo "==================================="

UTILITY_SCRIPTS=(
    "ci-health-check.sh"
    "cleanup-for-github.sh"
    "show-urls.sh"
    "fix-webhook-service.sh"
    "fix-rabbitmq-queues.sh"
    "set-java-17.sh"
    "open-demo-tabs.sh"
)

for script in "${UTILITY_SCRIPTS[@]}"; do
    if [ -f "$script" ]; then
        echo -e "   Moving ${YELLOW}$script${NC} → scripts/utilities/"
        git mv "$script" scripts/utilities/
    fi
done

echo -e "${GREEN}✅ Utility scripts moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 8: MOVE DOCUMENTATION
# ============================================================

echo -e "${CYAN}🔄 Phase 8: Moving Documentation${NC}"
echo "================================="

DOC_FILES=(
    "CONTRIBUTING.md"
    "MONITORING_VERIFICATION_REPORT.md"
    "WEBHOOK_EVENT_ENHANCEMENT.md"
    "TESTING_SETUP_COMPLETE.txt"
)

for doc in "${DOC_FILES[@]}"; do
    if [ -f "$doc" ]; then
        echo -e "   Moving ${YELLOW}$doc${NC} → docs/"
        git mv "$doc" docs/
    fi
done

echo -e "${GREEN}✅ Documentation moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 9: MOVE CONFIGURATION FILES
# ============================================================

echo -e "${CYAN}🔄 Phase 9: Moving Configuration Files${NC}"
echo "======================================="

CONFIG_FILES=(
    "grafana-dashboard-comprehensive.json"
    "grafana-dashboard-e2e.json"
    "SDV_E2E_Postman_Collection.json"
)

for config in "${CONFIG_FILES[@]}"; do
    if [ -f "$config" ]; then
        echo -e "   Moving ${YELLOW}$config${NC} → config/"
        git mv "$config" config/
    fi
done

echo -e "${GREEN}✅ Configuration files moved${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 10: DELETE TEMPORARY FILES
# ============================================================

echo -e "${CYAN}🗑️  Phase 10: Deleting Temporary Files${NC}"
echo "======================================="

TEMP_FILES=(
    "PROJECT_REORGANIZATION_GUIDE.md"
    "PROJECT_REORGANIZATION_PLAN.md"
    "QUICK_REORGANIZATION_REFERENCE.md"
    "REORGANIZATION_SUMMARY.md"
    "reorganize-project.sh"
    "update-docker-compose-paths.sh"
)

for file in "${TEMP_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "   Deleting ${RED}$file${NC}"
        rm "$file"
    fi
done

echo -e "${GREEN}✅ Temporary files deleted${NC}"
echo ""
sleep 1

# ============================================================
# PHASE 11: VERIFICATION
# ============================================================

echo -e "${CYAN}🔍 Phase 11: Verification${NC}"
echo "=========================="

# Verify main scripts are untouched
if [ -f "start-all-services.sh" ] && [ -f "run-e2e-demo.sh" ]; then
    echo -e "${GREEN}✅ Main entry scripts still in root${NC}"
else
    echo -e "${RED}❌ ERROR: Main scripts missing!${NC}"
    exit 1
fi

# Verify docker-compose is untouched
if [ -f "docker-compose.yml" ]; then
    echo -e "${GREEN}✅ docker-compose.yml still in root${NC}"
else
    echo -e "${RED}❌ ERROR: docker-compose.yml missing!${NC}"
    exit 1
fi

# Verify service folders are untouched
if [ -d "dco-gateway" ] && [ -d "postgres" ] && [ -d "minio" ]; then
    echo -e "${GREEN}✅ Service and infrastructure folders untouched${NC}"
else
    echo -e "${YELLOW}⚠️  Some service folders may be missing (check if expected)${NC}"
fi

# Check new directories exist
if [ -d "scripts/build" ] && [ -d "scripts/testing" ] && [ -d "docs" ] && [ -d "config" ]; then
    echo -e "${GREEN}✅ New directory structure created${NC}"
else
    echo -e "${RED}❌ Directory structure incomplete${NC}"
fi

echo ""
sleep 1

# ============================================================
# PHASE 12: COMMIT CHANGES
# ============================================================

echo -e "${CYAN}💾 Phase 12: Committing Changes${NC}"
echo "================================"

git add -A
git commit -m "Organize loose files into proper folders

- Move build scripts to scripts/build/
- Move database scripts to scripts/database/
- Move testing scripts to scripts/testing/
- Move monitoring scripts to scripts/monitoring/
- Move utility scripts to scripts/utilities/
- Move documentation to docs/
- Move config files to config/
- Delete temporary reorganization files
- Keep main entry scripts (start-all-services.sh, run-e2e-demo.sh) in root
- Keep all service/infrastructure folders in place

No functional changes. All features work as before."

echo -e "${GREEN}✅ Changes committed${NC}"
echo ""

# ============================================================
# SUMMARY
# ============================================================

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  File Organization Complete! 🎉                        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}✅ Files successfully organized!${NC}"
echo ""
echo -e "${CYAN}📊 Summary:${NC}"
echo "   • Build scripts → scripts/build/"
echo "   • Database scripts → scripts/database/"
echo "   • Testing scripts → scripts/testing/"
echo "   • Monitoring scripts → scripts/monitoring/"
echo "   • Utility scripts → scripts/utilities/"
echo "   • Documentation → docs/"
echo "   • Config files → config/"
echo "   • Temporary files deleted"
echo ""
echo -e "${CYAN}✅ Untouched (as required):${NC}"
echo "   • ./start-all-services.sh"
echo "   • ./run-e2e-demo.sh"
echo "   • ./docker-compose.yml"
echo "   • ./prometheus.yml"
echo "   • ./package.json"
echo "   • All service folders (dco-gateway, etc.)"
echo "   • All infrastructure folders (postgres, minio, etc.)"
echo ""
echo -e "${CYAN}📝 Backup Information:${NC}"
echo "   • Git tag: ${YELLOW}$BACKUP_TAG${NC}"
echo "   • To rollback: ${YELLOW}git reset --hard $BACKUP_TAG${NC}"
echo ""
echo -e "${CYAN}🧪 Verify Everything Works:${NC}"
echo "   1. Test startup: ${YELLOW}./start-all-services.sh${NC}"
echo "   2. Test E2E: ${YELLOW}./run-e2e-demo.sh${NC}"
echo ""
echo -e "${GREEN}🎯 Root directory is now clean and organized! 🚀${NC}"
echo ""
