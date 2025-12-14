#!/bin/bash

# Test script to demonstrate the complete simulation + evaluation flow
# This script creates a new simulation, triggers result generation, and verifies evaluation

echo "==========================================="
echo "SDV Simulation + Evaluation Test"
echo "==========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Generate a new simulation ID
SIMULATION_ID=$(uuidgen | tr '[:upper:]' '[:lower:]')

echo -e "${BLUE}Step 1: Generated new simulation ID${NC}"
echo "Simulation ID: $SIMULATION_ID"
echo ""

echo -e "${BLUE}Step 2: Triggering simulation result generation${NC}"
echo "This simulates running a simulation and generating metrics..."
TRIGGER_RESPONSE=$(curl -s -X POST "http://localhost:8082/api/campaigns/simulations/$SIMULATION_ID/results" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczNzM4MzQwMCwiZXhwIjoxNzM3Mzg3MDAwfQ.dummySignature")

echo "$TRIGGER_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$TRIGGER_RESPONSE"
echo ""

# Wait for evaluation to be triggered automatically
echo -e "${YELLOW}Waiting 3 seconds for automatic evaluation to trigger...${NC}"
sleep 3
echo ""

echo -e "${BLUE}Step 3: Checking evaluation result${NC}"
EVAL_RESPONSE=$(curl -s "http://localhost:8085/api/v1/evaluations/$SIMULATION_ID")

if echo "$EVAL_RESPONSE" | grep -q "overallScore"; then
    echo -e "${GREEN}✓ Evaluation completed successfully!${NC}"
    echo ""
    echo "Evaluation Result:"
    echo "$EVAL_RESPONSE" | python3 -m json.tool | head -40
    echo ""
    
    # Extract score and verdict
    SCORE=$(echo "$EVAL_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['overallScore'])" 2>/dev/null)
    VERDICT=$(echo "$EVAL_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['verdict'])" 2>/dev/null)
    
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo -e "${GREEN}Overall Score: $SCORE/100${NC}"
    echo -e "${GREEN}Verdict: $VERDICT${NC}"
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo ""
    echo "Metric Details:"
    echo "$EVAL_RESPONSE" | python3 -c "
import sys, json
data = json.load(sys.stdin)
for metric in data.get('metricResults', []):
    status = '✓ PASS' if metric['passed'] else '✗ FAIL'
    print(f\"  {status} {metric['ruleName']}: {metric['actualValue']:.2f} (threshold: {metric['expectedValue']:.2f})\")
" 2>/dev/null
    
else
    echo -e "${YELLOW}⚠ Evaluation not found yet${NC}"
    echo "Response: $EVAL_RESPONSE"
    echo ""
    echo "You can manually trigger evaluation with:"
    echo "  curl -X POST http://localhost:8085/api/v1/evaluations/trigger \\"
    echo "    -H 'Content-Type: application/json' \\"
    echo "    -d '{\"simulationId\":\"$SIMULATION_ID\"}'"
fi

echo ""
echo "==========================================="
echo "Test Complete"
echo "==========================================="
echo ""
echo "You can check the simulation logs in the UI:"
echo "  http://localhost:3000/dco/scenario"
echo ""
echo "Or via API:"
echo "  curl http://localhost:8085/api/v1/evaluations/$SIMULATION_ID"
