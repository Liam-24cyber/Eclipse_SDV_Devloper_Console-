# Evaluation Service

## Overview

The Evaluation Service automatically evaluates simulation quality based on configurable rules and metrics collected from Prometheus. It provides pass/fail verdicts with numerical scores to help teams make data-driven release decisions.

## Features

- ✅ **Auto-Evaluation**: Automatically evaluates simulations after completion
- ✅ **Configurable Rules**: Users can define custom evaluation rules
- ✅ **Scoring System**: Weighted scoring (0-100) based on rule importance
- ✅ **Pass/Fail Verdicts**: Clear PASS/FAIL/WARNING verdicts
- ✅ **Prometheus Integration**: Queries metrics directly from Prometheus
- ✅ **RESTful API**: Easy integration with other services
- ✅ **Persistent Storage**: Historical evaluation results in PostgreSQL

## Architecture

```
Simulation Completes
    ↓
Trigger Evaluation (API Call)
    ↓
Query Prometheus for Metrics
    ↓
Load Active Rules from Database
    ↓
Evaluate Each Rule
    ↓
Calculate Weighted Score
    ↓
Determine Verdict (PASS/FAIL)
    ↓
Save Results to Database
    ↓
Return Evaluation Summary
```

## Default Evaluation Rules

| Rule Name    | Metric                           | Condition | Threshold | Weight |
|--------------|----------------------------------|-----------|-----------|--------|
| Max Duration | simulation_duration_seconds      | <         | 60        | 20     |
| Max Latency  | webhook_delivery_duration_seconds| <         | 2         | 25     |
| Zero Errors  | simulation_errors_total          | =         | 0         | 30     |
| CPU Limit    | simulation_cpu_percent           | <         | 80        | 15     |
| Memory Limit | simulation_memory_percent        | <         | 85        | 10     |

## API Endpoints

### Trigger Evaluation
```http
POST /api/v1/evaluations/trigger/{simulationId}
```

**Response**:
```json
{
  "simulation_id": "sim-123",
  "overall_score": 87,
  "verdict": "PASS",
  "evaluated_at": "2025-01-12T10:30:00",
  "evaluation_duration_ms": 1234
}
```

### Get Evaluation Results
```http
GET /api/v1/evaluations/{simulationId}
```

### Get Detailed Results
```http
GET /api/v1/evaluations/{simulationId}/details
```

**Response**:
```json
{
  "simulation_id": "sim-123",
  "overall_score": 87,
  "verdict": "PASS",
  "metrics": [
    {
      "rule_name": "Max Duration",
      "expected": "< 60",
      "actual": 25.5,
      "passed": true
    }
  ]
}
```

### Manage Rules
```http
GET    /api/v1/rules              # List all rules
POST   /api/v1/rules              # Create new rule
PUT    /api/v1/rules/{id}         # Update rule
DELETE /api/v1/rules/{id}         # Delete rule
```

## Configuration

### Environment Variables

```bash
# Database
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=dco_db
DATABASE_USER=dco_user
DATABASE_PASSWORD=dco_password

# Prometheus
PROMETHEUS_URL=http://localhost:9090
```

### application.yml

```yaml
server:
  port: 8085

spring:
  application:
    name: evaluation-service
  datasource:
    url: jdbc:postgresql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
```

## Building & Running

### Local Development

```bash
# Build
mvn clean install

# Run
java -jar app/target/evaluation-service-app-latest.jar
```

### Docker

```bash
# Build image
docker build -f Dockerfile.app -t evaluation-service:latest .

# Run container
docker run -p 8085:8085 \
  -e DATABASE_HOST=postgres \
  -e PROMETHEUS_URL=http://prometheus:9090 \
  evaluation-service:latest
```

### With Docker Compose

```bash
docker-compose up evaluation-service
```

## Health Check

```bash
curl http://localhost:8085/actuator/health
```

## Metrics

The service exposes Prometheus metrics at:
```
http://localhost:8085/actuator/prometheus
```

**Custom Metrics**:
- `evaluations_completed_total` - Total evaluations performed
- `evaluations_failed_total` - Total failed evaluations
- `evaluation_duration_seconds` - Time taken to evaluate

## Database Schema

### Tables

- **evaluation_rules**: User-defined evaluation rules
- **evaluation_results**: Overall evaluation outcomes
- **evaluation_metric_results**: Individual metric pass/fail details

## Development Status

- ✅ Database schema designed
- ✅ JPA entities created
- ✅ Configuration files ready
- ⏳ Repositories (Next)
- ⏳ Service layer (Next)
- ⏳ REST controllers (Next)
- ⏳ Docker setup (Next)

## Contributing

This service follows the existing SDV microservices architecture pattern. Refer to the main project CONTRIBUTING.md for guidelines.

## License

See main project LICENSE.md
