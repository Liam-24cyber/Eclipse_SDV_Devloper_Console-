#!/bin/bash

# 🧹 GitHub Repository Cleanup Script
# This script helps you clean up your main branch before pushing to GitHub

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  GitHub Repository Cleanup for Main Branch            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Step 1: Show current status
echo -e "${CYAN}📋 Step 1: Checking current repository status...${NC}"
git status
echo ""
sleep 2

# Step 2: Remove unwanted files that are already tracked
echo -e "${CYAN}📋 Step 2: Removing redundant documentation files...${NC}"

# Remove old redundant .md files (if they exist)
files_to_remove=(
    "TESTING_QUICKSTART.md"
    "TESTING_README.md"
    "TESTING_COMPLETION_SUMMARY.md"
    "MONITORING_SUMMARY.md"
    "MONITORING_GUIDE.md"
)

for file in "${files_to_remove[@]}"; do
    if [ -f "$file" ]; then
        git rm -f "$file" 2>/dev/null || rm -f "$file"
        echo -e "${GREEN}✅ Removed: $file${NC}"
    fi
done

echo ""
sleep 1

# Step 3: Create/Update .gitignore
echo -e "${CYAN}📋 Step 3: Updating .gitignore...${NC}"

cat > .gitignore << 'GITIGNORE_END'
# Maven build outputs
**/target/
**/dependency-reduced-pom.xml

# Node.js
**/node_modules/
**/dist/
**/.next/
**/out/

# IDE files
**/.idea/
**/.vscode/
**/.DS_Store
*.iml
*.swp
*.swo

# Test reports (keep in gitignore, upload as artifacts in CI/CD)
**/test-reports*/
**/surefire-reports/
**/failsafe-reports/

# Logs
**/*.log
**/logs/

# Environment files
**/.env
**/.env.local
**/minio_keys.env

# Temporary files
**/*.tmp
**/*.temp
**/.cache/

# Docker volumes (if any)
**/volumes/

# OS files
**/.DS_Store
**/Thumbs.db

GITIGNORE_END

echo -e "${GREEN}✅ .gitignore updated${NC}"
echo ""
sleep 1

# Step 4: Check for large files
echo -e "${CYAN}📋 Step 4: Checking for large files (>10MB)...${NC}"
find . -type f -size +10M ! -path "**/node_modules/*" ! -path "**/target/*" ! -path "**/.git/*" -exec ls -lh {} \; | awk '{print $5, $9}' || echo "No large files found"
echo ""
sleep 1

# Step 5: Summary of what will be kept
echo -e "${CYAN}📋 Step 5: Files that will be kept in the repository:${NC}"
echo ""
echo -e "${GREEN}✅ Essential Scripts (root):${NC}"
echo "   - start-all-services.sh"
echo "   - run-e2e-demo.sh"
echo "   - run-all-tests.sh"
echo "   - 10-build-script.sh"
echo "   - 20-deploy-script.sh"
echo "   - 30-destroy-script.sh"
echo ""

echo -e "${GREEN}✅ Scripts Folder:${NC}"
echo "   - scripts/monitor-rabbitmq-live.sh"
echo "   - scripts/monitor-webhook-activity.sh"
echo "   - scripts/show-monitoring-help.sh"
echo "   - scripts/show-urls.sh"
echo "   - scripts/set-java-17.sh"
echo "   - scripts/README.md"
echo ""

echo -e "${GREEN}✅ Documentation (root):${NC}"
echo "   - README.md"
echo "   - CONTRIBUTING.md"
echo "   - LICENSE.md"
echo "   - TESTING.md"
echo "   - MONITORING.md"
echo ""

echo -e "${GREEN}✅ CI/CD Configuration:${NC}"
echo "   - .github/workflows/test.yml"
echo ""

echo -e "${GREEN}✅ Source Code:${NC}"
echo "   - All service directories (dco-gateway, scenario-library-service, etc.)"
echo "   - Docker configuration files"
echo "   - Database scripts"
echo ""

# Step 6: Stage all changes
echo -e "${CYAN}📋 Step 6: Staging changes for commit...${NC}"
git add .
echo -e "${GREEN}✅ Changes staged${NC}"
echo ""

# Step 7: Show what will be committed
echo -e "${CYAN}📋 Step 7: Files to be committed:${NC}"
git status --short
echo ""

# Step 8: Confirmation
echo -e "${YELLOW}⚠️  Review the changes above carefully!${NC}"
echo ""
echo -e "${CYAN}Next steps:${NC}"
echo "1. Review the changes shown above"
echo "2. If everything looks good, run: git commit -m 'Clean up repository structure'"
echo "3. Then run: git push origin main"
echo ""
echo -e "${YELLOW}Note: This script has NOT committed or pushed anything yet.${NC}"
echo -e "${YELLOW}You must manually commit and push after reviewing.${NC}"
echo ""

# End
echo -e "${GREEN}🎉 Cleanup preparation complete!${NC}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  To complete the cleanup, run these commands:${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "  ${CYAN}git commit -m 'Clean up repository: organize scripts, consolidate docs, add CI/CD testing'${NC}"
echo -e "  ${CYAN}git push origin main${NC}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
