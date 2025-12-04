# Observability Tools Guide

Quick reference for all monitoring and observability tools in Task Flow.

## 🚀 Quick Start

```bash
# Start all services
docker compose up -d

# Check service status
docker compose ps

# View logs
docker compose logs -f [service-name]
```

---

## 📊 Observability Stack

### Grafana (Dashboards & Visualization)
- **URL**: `http://localhost:3000`
- **Version**: Latest
- **Port**: `3000`
- **Credentials**: `admin` / `admin`
- **Purpose**: Unified dashboard for metrics, logs, and traces
- **Pre-configured Dashboards**:
  - Spring Boot Observability
  - JVM Micrometer
  - PostgreSQL Metrics

### Prometheus (Metrics Collection)
- **URL**: `http://localhost:9090`
- **Version**: Latest
- **Port**: `9090`
- **Purpose**: Metrics collection and querying
- **Metrics Endpoint**: `http://localhost:8080/actuator/prometheus`
- **Example Query**: `http_server_requests_seconds_count{application="task-flow"}`

### Loki (Log Aggregation)
- **URL**: `http://localhost:3100`
- **Version**: `3.1.0`
- **Port**: `3100`
- **Purpose**: Centralized log storage
- **Access**: Via Grafana Explore (select Loki datasource)

### Tempo (Distributed Tracing)
- **URL**: `http://localhost:3200`
- **Version**: `2.6.1`
- **Ports**: 
  - `3200` (HTTP API/Query)
  - `4317` (OTLP gRPC)
  - `4318` (OTLP HTTP)
- **Purpose**: Distributed tracing backend
- **Access**: Via Grafana Explore (select Tempo datasource)

### Promtail (Log Shipper)
- **Version**: `3.1.0`
- **Purpose**: Collects Docker container logs and sends to Loki
- **Status**: Runs automatically, no direct access needed

### Postgres Exporter (Database Metrics)
- **URL**: `http://localhost:9187`
- **Version**: Latest
- **Port**: `9187`
- **Purpose**: Exports PostgreSQL metrics to Prometheus

---

## 📨 Messaging & Event Streaming

### Kafka (Message Broker)
- **Version**: `7.6.0` (Confluent Platform)
- **Mode**: KRaft (no Zookeeper required)
- **Ports**:
  - `9092` (External clients)
  - `9093` (Internal Docker network)
  - `29093` (Controller)
- **Purpose**: Event streaming and message broker

### Kafka UI (Kafka Management)
- **URL**: `http://localhost:8081`
- **Version**: Latest
- **Port**: `8081`
- **Credentials**: Set via `.env` file
  - `KAFKA_UI_USERNAME` (default: `kafka`)
  - `KAFKA_UI_PASSWORD` (default: `changeit`)
- **Purpose**: Web UI for managing topics, messages, and consumer groups

---

## 🔧 Application Endpoints

| Endpoint | URL | Description |
|----------|-----|-------------|
| **API** | `http://localhost:8080` | Main application |
| **Swagger UI** | `http://localhost:8080/swagger-ui` | API documentation |
| **Health Check** | `http://localhost:8080/actuator/health` | Application health |
| **Metrics** | `http://localhost:8080/actuator/prometheus` | Prometheus metrics |

---

## 📧 Email Testing (Optional)

### Mailpit
- **Web UI**: `http://localhost:8025`
- **SMTP**: `localhost:1025`
- **Version**: Latest
- **Ports**: `8025` (HTTP), `1025` (SMTP)
- **Start**: `docker compose --profile mail up -d`

---

## 🔍 Common Tasks

### View Logs in Grafana
1. Open Grafana: `http://localhost:3000`
2. Click **Explore** (compass icon)
3. Select **Loki** as datasource
4. Query: `{container_name="task-flow-app"}`

### View Traces in Grafana
1. Open Grafana: `http://localhost:3000`
2. Click **Explore**
3. Select **Tempo** as datasource
4. Search by trace ID or service name

### Query Metrics in Prometheus
1. Open Prometheus: `http://localhost:9090`
2. Go to **Graph** tab
3. Enter PromQL query, e.g.:
   - `http_server_requests_seconds_count{application="task-flow"}`
   - `jvm_memory_used_bytes{application="task-flow"}`

### Monitor Kafka Topics
1. Open Kafka UI: `http://localhost:8081`
2. Browse topics, view messages
3. Monitor consumer groups and offsets

---

## 🔐 Security Notes

⚠️ **Default credentials are for development only!**

- Change Grafana password in production
- Update Kafka UI credentials via `.env` file
- Use strong passwords in production environments

---

## 📋 Service Versions Summary

| Service | Version | Port |
|---------|---------|------|
| Application | Spring Boot 3.4.9, Java 21 | 8080 |
| PostgreSQL | 15.8 | 5433 |
| Kafka | 7.6.0 (KRaft) | 9092, 9093 |
| Grafana | Latest | 3000 |
| Prometheus | Latest | 9090 |
| Loki | 3.1.0 | 3100 |
| Tempo | 2.6.1 | 3200, 4317, 4318 |
| Promtail | 3.1.0 | - |
| Kafka UI | Latest | 8081 |
| Postgres Exporter | Latest | 9187 |

---

## 🆘 Troubleshooting

### Service not accessible?
```bash
# Check if service is running
docker compose ps

# Check service logs
docker compose logs [service-name]

# Restart a service
docker compose restart [service-name]
```

### Grafana dashboards not showing data?
- Verify Prometheus is running: `http://localhost:9090`
- Check datasource configuration in Grafana
- Ensure application is sending metrics to `/actuator/prometheus`

### Logs not appearing in Grafana?
- Verify Loki is running: `http://localhost:3100`
- Check Promtail is collecting logs: `docker compose logs promtail`
- Verify Loki datasource is configured in Grafana

---

**Last Updated**: December 2024  
**Maintained By**: Task Flow Team

