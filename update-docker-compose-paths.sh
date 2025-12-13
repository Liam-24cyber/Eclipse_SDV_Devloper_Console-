#!/bin/bash
# 🔧 Automatic docker-compose.yml Path Updater
# This script updates all file paths in docker-compose.yml to match the new reorganized structure

set -e

COMPOSE_FILE="docker-compose.yml"
BACKUP_FILE="docker-compose.yml.backup.$(date +%Y%m%d-%H%M%S)"

echo "🔧 SDV docker-compose.yml Path Updater"
echo "======================================"
echo ""

# Check if docker-compose.yml exists
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "❌ Error: docker-compose.yml not found in current directory"
    echo "   Please run this script from the project root"
    exit 1
fi

# Create backup
echo "📋 Creating backup: $BACKUP_FILE"
cp "$COMPOSE_FILE" "$BACKUP_FILE"

# Update paths using sed
echo "🔄 Updating service paths..."

# Service Dockerfiles
sed -i.tmp 's|dockerfile: dco-gateway/Dockerfile\.app|dockerfile: services/dco-gateway/Dockerfile.app|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: developer-console-ui/Dockerfile|dockerfile: services/developer-console-ui/Dockerfile|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: message-queue-service/Dockerfile\.app|dockerfile: services/message-queue-service/Dockerfile.app|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: scenario-library-service/Dockerfile\.app|dockerfile: services/scenario-library-service/Dockerfile.app|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: tracks-management-service/Dockerfile\.app|dockerfile: services/tracks-management-service/Dockerfile.app|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: webhook-management-service/Dockerfile\.app|dockerfile: services/webhook-management-service/Dockerfile.app|g' "$COMPOSE_FILE"

echo "✅ Service paths updated"

# Infrastructure Dockerfiles
echo "🔄 Updating infrastructure paths..."
sed -i.tmp 's|dockerfile: postgres/Dockerfile\.database|dockerfile: infrastructure/postgres/Dockerfile.database|g' "$COMPOSE_FILE"
sed -i.tmp 's|dockerfile: minio/Dockerfile\.minio|dockerfile: infrastructure/minio/Dockerfile.minio|g' "$COMPOSE_FILE"

echo "✅ Infrastructure paths updated"

# Environment files
echo "🔄 Updating environment file paths..."
sed -i.tmp 's|- minio/minio_keys\.env|- infrastructure/minio/minio_keys.env|g' "$COMPOSE_FILE"

echo "✅ Environment file paths updated"

# Monitoring volume mounts
echo "🔄 Updating monitoring paths..."
sed -i.tmp 's|\./prometheus\.yml:/etc/prometheus/prometheus\.yml|./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml|g' "$COMPOSE_FILE"
sed -i.tmp 's|\./grafana/provisioning:/etc/grafana/provisioning|./monitoring/grafana/provisioning:/etc/grafana/provisioning|g' "$COMPOSE_FILE"

echo "✅ Monitoring paths updated"

# Clean up temporary files created by sed -i on macOS
rm -f "${COMPOSE_FILE}.tmp"

# Verify the changes
echo ""
echo "📊 Verifying docker-compose.yml syntax..."
if docker-compose config > /dev/null 2>&1; then
    echo "✅ docker-compose.yml syntax is valid!"
else
    echo "❌ Error: docker-compose.yml has syntax errors"
    echo "   Restoring backup..."
    cp "$BACKUP_FILE" "$COMPOSE_FILE"
    echo "   Backup restored. Please check the paths manually."
    exit 1
fi

# Show summary of changes
echo ""
echo "📋 Summary of changes:"
echo "======================================"
diff "$BACKUP_FILE" "$COMPOSE_FILE" | grep -E "^[<>].*dockerfile:|^[<>].*\.yml:|^[<>].*\.env|^[<>].*provisioning" || echo "No differences to display"

echo ""
echo "✅ All paths updated successfully!"
echo ""
echo "📁 Backup saved as: $BACKUP_FILE"
echo ""
echo "🔍 Next steps:"
echo "   1. Review the changes: diff $BACKUP_FILE $COMPOSE_FILE"
echo "   2. Test the configuration: docker-compose config"
echo "   3. Build services: docker-compose build"
echo "   4. Start services: ./start-all-services.sh"
echo ""
