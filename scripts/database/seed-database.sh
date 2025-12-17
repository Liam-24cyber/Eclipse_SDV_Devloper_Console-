#!/bin/bash
# Seed the database with test data

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  SDV Developer Console - Database Seeding              ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

SEED_FILE="./postgres/seed-data.sql"

# Check if seed file exists
if [ ! -f "$SEED_FILE" ]; then
    echo -e "${RED}✗ Seed file not found: ${SEED_FILE}${NC}"
    exit 1
fi

echo -e "${YELLOW}📝 Loading seed data into database...${NC}"
echo ""

# Execute seed file
docker exec -i postgres psql -U postgres -d postgres < "$SEED_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Database seeded successfully!${NC}"
    echo ""
    
    # Verify data
    echo -e "${BLUE}📊 Verification:${NC}"
    echo ""
    
    echo -e "${YELLOW}Scenarios:${NC}"
    docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) as total_scenarios FROM scenario;"
    echo ""
    
    echo -e "${YELLOW}Tracks:${NC}"
    docker exec postgres psql -U postgres -d postgres -c "SELECT COUNT(*) as total_tracks FROM track;"
    echo ""
    
    echo -e "${YELLOW}Sample Scenarios:${NC}"
    docker exec postgres psql -U postgres -d postgres -c "SELECT id, name, type, status FROM scenario LIMIT 5;"
    echo ""
    
    echo -e "${GREEN}✓ Database is ready for testing!${NC}"
else
    echo -e "${RED}✗ Failed to seed database${NC}"
    exit 1
fi
