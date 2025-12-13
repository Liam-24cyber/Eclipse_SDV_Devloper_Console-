#!/bin/bash

# 🌐 Open All Demo Tabs
# Opens all required browser tabs for demo recording

echo "🌐 Opening all demo browser tabs..."
echo ""

# Array of URLs to open
urls=(
    "http://localhost:3000"         # UI
    "http://localhost:5050"         # pgAdmin
    "http://localhost:9001"         # MinIO
    "http://localhost:15672"        # RabbitMQ
    "http://localhost:9090/targets" # Prometheus
    "http://localhost:3001"         # Grafana
)

# Open each URL in the default browser
for url in "${urls[@]}"; do
    echo "Opening: $url"
    open "$url"
    sleep 1  # Small delay between tabs
done

echo ""
echo "✅ All tabs opened!"
echo ""
echo "Login credentials:"
echo "  UI:       admin / admin123"
echo "  pgAdmin:  admin@example.com / admin"
echo "  MinIO:    minioadmin / minioadmin"
echo "  RabbitMQ: guest / guest"
echo "  Grafana:  admin / admin"
echo ""
echo "Next: Login to each service and prepare for recording!"
