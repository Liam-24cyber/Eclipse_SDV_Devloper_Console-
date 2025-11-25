# Utility Scripts

This folder contains utility and helper scripts for the SDV Developer Console project.

## 📋 Available Scripts

### Testing
- **`run-all-tests.sh`** - Execute all JUnit tests for all services with reporting

### Monitoring & Debugging
- **`monitor-rabbitmq-live.sh`** - Real-time RabbitMQ queue monitoring
- **`monitor-webhook-activity.sh`** - Track webhook delivery activity
- **`show-monitoring-help.sh`** - Quick reference for monitoring commands
- **`show-urls.sh`** - Display all service URLs and endpoints

### Verification
- **`verify-dlq.sh`** - Check dead letter queue for failed messages
- **`verify-restart-persistence.sh`** - Test data persistence after service restart

## 🚀 Usage

All scripts can be run from the project root:

```bash
# Run from project root
./scripts/run-all-tests.sh
./scripts/monitor-rabbitmq-live.sh
./scripts/show-urls.sh
```

Or from within the scripts directory:

```bash
cd scripts
./run-all-tests.sh
./monitor-rabbitmq-live.sh
```

## 📖 Related Documentation

- [`../TESTING.md`](../TESTING.md) - Complete testing guide
- [`../MONITORING.md`](../MONITORING.md) - Monitoring and observability guide
- [`../README.md`](../README.md) - Main project documentation

## 🔗 Main Workflow Scripts

Main workflow scripts remain in the project root for easy access:
- `../run-e2e-demo.sh` - **Main E2E demonstration workflow**
- `../start-all-services.sh` - Start all Docker services
- `../10-build-script.sh` - Build all services
- `../20-deploy-script.sh` - Deploy services
- `../30-destroy-script.sh` - Cleanup and destroy
