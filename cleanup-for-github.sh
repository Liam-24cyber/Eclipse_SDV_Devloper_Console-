#!/bin/bash

echo "🧹 Cleaning up unnecessary files for GitHub push..."

# Remove all test-related MD files
echo "Removing test documentation files..."
rm -f TESTING_DELIVERABLES_SUMMARY.md
rm -f TEST_EXECUTION_REPORT.md
rm -f COMPLETE_TESTING_SUMMARY.md
rm -f TEST_PLAN.md
rm -f TEST_EXECUTION_RESULTS_*.md
rm -f COMPLETE_E2E_TEST_RESULTS.md
rm -f E2E_SIMULATION_TEST_RESULTS.md
rm -f E2E_TEST_JOURNEY.md
rm -f E2E_TESTING_STATUS.md
rm -f E2E_TESTING_BLOCKERS.md
rm -f E2E_TESTING_BLOCKERS_RESOLVED.md
rm -f RABBITMQ_TEST_RESULTS.md

# Remove demo and documentation files
echo "Removing demo documentation files..."
rm -f DEMO_QUICK_REFERENCE_CARD.md
rm -f DEMO_RECORDING_GUIDE.md
rm -f DEMO_SETUP_COMPLETE.md
rm -f DEMO_SETUP_FINAL.md
rm -f demo-script.md
rm -f DEMO_QUICK_START.md
rm -f POSTMAN_E2E_GUIDE.md
rm -f POSTMAN_QUICK_REF.md
rm -f POSTMAN_SETUP_COMPLETE.md
rm -f POSTMAN_SUCCESS_SUMMARY.md

# Remove fix/status documentation
echo "Removing fix and status documentation..."
rm -f FIX_ACTION_PLAN.md
rm -f FIX_EMPTY_DATABASE.md
rm -f FIX_EXECUTION_PROGRESS.md
rm -f FIX_SUMMARY.md
rm -f FIX_COMPLETION_SUMMARY.md
rm -f COMPLETE_FIX_SUMMARY.md
rm -f COMPLETE_ISSUE_RESOLUTION_REPORT.md
rm -f CRITICAL_BLOCKERS_FIXED.md
rm -f CRITICAL_CHANGES_MADE.md
rm -f CRITICAL_ISSUES_SUMMARY.md
rm -f CURRENT_ISSUES_ANALYSIS.md
rm -f DATABASE_ISSUE_RESOLVED.md
rm -f JACKSON_FIX_SUCCESS.md
rm -f JACKSON_DATE_TIME_FIX.md
rm -f JACKSON_FIXES_COMPLETE_SUMMARY.md
rm -f JACKSON_RABBITMQ_FIX_SUMMARY.md
rm -f WEBHOOK_FIX_SUCCESS.md
rm -f WEBHOOK_JACKSON_FIX_COMPLETE.md
rm -f PROMETHEUS_FIX.md
rm -f GRAPHQL_SCHEMA_FIX.md

# Remove workflow/status documentation
echo "Removing workflow documentation..."
rm -f E2E_WORKFLOW_EXPLAINED.md
rm -f E2E_DEMO_WORKFLOW_GUIDE.md
rm -f E2E_FLOW_VERIFICATION.md
rm -f E2E_COMPLETE_ANALYSIS.md
rm -f E2E_MASTER_INDEX.md
rm -f EVENT_FLOW_DOCUMENTATION.md
rm -f COMPLETE_DEMO_SYSTEM.md

# Remove quick reference guides
echo "Removing quick reference guides..."
rm -f QUICK_START.md
rm -f QUICK_REFERENCE.md
rm -f QUICK_REFERENCE_FIXES.md
rm -f QUICK_E2E_TEST_GUIDE.md
rm -f COMPLETE_CURL_REFERENCE.md
rm -f ALL_URLS.md
rm -f SERVICE_URLS.md
rm -f LOGIN_CREDENTIALS.md
rm -f LOCAL_ENDPOINTS.md

# Remove status and analysis files
echo "Removing status files..."
rm -f DEPLOYMENT_STATUS.md
rm -f INFRASTRUCTURE_STATUS.md
rm -f RABBITMQ_STATUS_VERIFIED.md
rm -f RABBITMQ_ANALYSIS.md
rm -f RABBITMQ_FIX_QUICKSTART.md
rm -f REDIS_STATUS.md
rm -f DATA_PERSISTENCE_REALITY.md
rm -f VOLUME_PERSISTENCE_EXPLAINED.md
rm -f AUTO_SEEDING_ENABLED.md
rm -f API_VERIFICATION_REPORT.md

# Remove operational guides
echo "Removing operational guides..."
rm -f HOW_TO_START_SERVICES.md
rm -f START_SERVICES_SIMPLE.md
rm -f STARTUP_SHUTDOWN_GUIDE.md
rm -f PRE_SHUTDOWN_CHECKLIST.md
rm -f RESTART_QUICK_REFERENCE.md
rm -f RESTART_PERSISTENCE_GUARANTEE.md
rm -f STEP1_STATUS_CHECKLIST.md
rm -f SEED_DATA_REFERENCE.md

# Remove misc documentation
echo "Removing miscellaneous documentation..."
rm -f DO_THIS_NOW.md
rm -f NEXT_STEPS.md
rm -f FILES_CHANGED.md
rm -f FILES_CREATED_SUMMARY.md
rm -f READY_FOR_TOMORROW.md
rm -f SESSION_SUMMARY_CRITICAL_FIXES.md
rm -f ROOT_CAUSE_ANALYSIS.md
rm -f DOCUMENTATION_INDEX.md
rm -f SDV-CORE-2-DEPLOYMENT.md

# Remove test scripts
echo "Removing test scripts..."
rm -f execute-test-plan.sh
rm -f deploy-and-test-webhooks.sh
rm -f publish-test-event.sh
rm -f seed-test-webhook.sh
rm -f check-demo-readiness.sh
rm -f test-all-endpoints.sh

# Remove log files
echo "Removing log files..."
rm -f *.log

# Remove test API server
echo "Removing test infrastructure..."
rm -f e2e-api-server.js
rm -f mock-webhook-server.js

# Remove other test/demo scripts
echo "Removing demo scripts..."
rm -f open-demo-tabs.sh
rm -f fix-rabbitmq-queues.sh
rm -f fix-webhook-service.sh
rm -f purge-dlqs.sh
rm -f check-status.sh

# Remove Postman collection (if you don't need it)
rm -f E2E_Demo_API.postman_collection.json

echo "✅ Cleanup complete!"
echo ""
echo "Files to keep:"
echo "  ✓ README.md"
echo "  ✓ LICENSE.md"
echo "  ✓ CONTRIBUTING.md"
echo "  ✓ docker-compose.yml"
echo "  ✓ All source code directories"
echo "  ✓ Build scripts (10-build-script.sh, 20-deploy-script.sh, 30-destroy-script.sh)"
echo "  ✓ prometheus.yml"
echo "  ✓ package.json"
echo ""
echo "Run 'git status' to see what has been removed."
