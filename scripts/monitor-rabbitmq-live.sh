#!/bin/bash

# 📊 Real-time RabbitMQ Message Monitor
# This script monitors RabbitMQ queues and shows live message flow

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     RabbitMQ Real-Time Message Flow Monitor           ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Function to get queue stats
get_queue_stats() {
    local queue_name=$1
    local queue_info=$(curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/$queue_name)
    
    local messages=$(echo $queue_info | jq -r '.messages // 0')
    local ready=$(echo $queue_info | jq -r '.messages_ready // 0')
    local unacked=$(echo $queue_info | jq -r '.messages_unacknowledged // 0')
    local publish_rate=$(echo $queue_info | jq -r '.message_stats.publish_details.rate // 0')
    local deliver_rate=$(echo $queue_info | jq -r '.message_stats.deliver_get_details.rate // 0')
    local consumers=$(echo $queue_info | jq -r '.consumers // 0')
    
    echo "$messages|$ready|$unacked|$publish_rate|$deliver_rate|$consumers"
}

# Monitor loop
echo -e "${YELLOW}📊 Monitoring queues... Press Ctrl+C to stop${NC}"
echo ""

while true; do
    clear
    echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     RabbitMQ Real-Time Message Flow Monitor           ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "Updated: $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""
    
    printf "%-25s %-8s %-8s %-10s %-12s %-12s %-10s\n" \
        "Queue" "Total" "Ready" "Unacked" "Pub/sec" "Del/sec" "Consumers"
    echo "────────────────────────────────────────────────────────────────────────────────────────"
    
    for queue in "scenario.events" "simulation.events" "track.events" "webhook.events"; do
        stats=$(get_queue_stats "$queue")
        IFS='|' read -r total ready unacked pub_rate del_rate consumers <<< "$stats"
        
        # Color code based on activity
        if (( $(echo "$total > 0" | bc -l 2>/dev/null || echo 0) )); then
            color=$YELLOW
        else
            color=$NC
        fi
        
        printf "${color}%-25s %-8s %-8s %-10s %-12.2f %-12.2f %-10s${NC}\n" \
            "$queue" "$total" "$ready" "$unacked" "$pub_rate" "$del_rate" "$consumers"
    done
    
    echo ""
    echo -e "${CYAN}🔄 Queue Activity:${NC}"
    
    # Show recent messages from exchange
    EXCHANGE_INFO=$(curl -s -u admin:admin123 http://localhost:15672/api/exchanges/%2F/sdv.events)
    PUB_IN=$(echo $EXCHANGE_INFO | jq -r '.message_stats.publish_in // 0')
    PUB_OUT=$(echo $EXCHANGE_INFO | jq -r '.message_stats.publish_out // 0')
    
    echo -e "  Exchange 'sdv.events': ${GREEN}$PUB_IN${NC} messages in, ${BLUE}$PUB_OUT${NC} messages routed"
    
    echo ""
    echo -e "${YELLOW}💡 Legend:${NC}"
    echo -e "  Total: Messages currently in queue"
    echo -e "  Ready: Waiting to be consumed"
    echo -e "  Unacked: Being processed"
    echo -e "  Pub/sec: Publishing rate"
    echo -e "  Del/sec: Delivery rate"
    
    echo ""
    echo -e "${CYAN}Press Ctrl+C to stop monitoring${NC}"
    
    sleep 1
done
