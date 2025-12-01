# Observability

This app is observed through the three pillars: Logs (Loki), Metrics (Prometheus), and Traces (Tempo), visualized in Grafana. Below is a bottom‑up overview of how each piece connects.

## Components and paths

- Application (Docker service: `app`)
  - Tracing: OpenTelemetry Java Agent exports traces to Tempo (OTLP/HTTP).
  - Metrics: Spring Boot Actuator + Micrometer expose `/actuator/prometheus`.
  - Logs: stdout/stderr via Logback with `trace_id`/`span_id` in the pattern.

- Promtail (service: `promtail`)
  - Reads Docker container logs via Docker socket.
  - Pushes logs to Loki.
  - Config: `observability/promtail/config.yml` (mounted to `/etc/promtail/config.yml`).

- Loki (service: `loki`)
  - Stores and indexes logs.
  - Config: `observability/loki/local-config.yml` (mounted to `/etc/loki/local-config.yml`).

- Prometheus (service: `prometheus`)
  - Scrapes the app on `app:8080/actuator/prometheus` and the Postgres exporter.
  - Config: `observability/prometheus/prometheus.yml` (mounted to `/etc/prometheus/prometheus.yml`).

- Tempo (service: `tempo`)
  - Receives traces from the app via OTLP (4317/grpc, 4318/http) and stores them locally.
  - Config: `observability/tempo/tempo.yaml` (mounted to `/etc/tempo.yaml`).

- Grafana (service: `grafana`)
  - Datasources are provisioned for Prometheus, Loki, and Tempo.
  - Dashboards are provisioned for JVM/Micrometer and Postgres.
  - Paths: `observability/grafana/provisioning/**`, dashboards in `observability/grafana/dashboards/`.

## Data flow (bottom → up)

1) Logs
   - App writes logs to stdout with MDC fields `trace_id` and `span_id`.
   - Promtail tails Docker logs and ships to Loki.
   - Grafana reads from Loki; logs correlate with traces via `trace_id`.

2) Metrics
   - App exposes metrics at `/actuator/prometheus` (Micrometer + Actuator).
   - Prometheus scrapes the endpoint on schedule.
   - Grafana reads from Prometheus (dashboards + explore).

3) Traces
   - OpenTelemetry Java Agent sends traces to Tempo (OTLP HTTP 4318).
   - Grafana reads from Tempo; from a span you can jump to related logs in Loki.

## Correlation

- Logs include `trace_id`/`span_id` (set by the OTel agent via MDC), configured in `src/main/resources/application.yml` under `logging.pattern.console`.
- Grafana `Tempo` datasource is configured with `tracesToLogs` to pivot from traces → logs.
- Metrics → traces exemplars are available if you later enable Micrometer Tracing; for now, metrics and traces are separate but co‑visible.

## Key configuration points

- Docker Compose (`compose.yaml`):
  - Prometheus mounts: `./observability/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro`
  - Tempo mounts: `./observability/tempo/tempo.yaml:/etc/tempo.yaml:ro`
  - Loki mounts: `./observability/loki/local-config.yml:/etc/loki/local-config.yml:ro`, data at `./observability/loki/data`
  - Promtail mounts: Docker socket + `./observability/promtail/config.yml:/etc/promtail/config.yml:ro`
  - Grafana mounts: `./observability/grafana/provisioning/**` and `./observability/grafana/dashboards`

- Dockerfile (OTel):
  - Downloads `opentelemetry-javaagent.jar` at build time.
  - Runs with: `-javaagent:/app/opentelemetry-javaagent.jar`
  - Env: `OTEL_TRACES_EXPORTER=otlp`, `OTEL_METRICS_EXPORTER=none`, `OTEL_LOGS_EXPORTER=none`, `OTEL_EXPORTER_OTLP_ENDPOINT=http://tempo:4318`, `OTEL_SERVICE_NAME=task-flow`.

- Spring Boot (`application.yml`):
  - Exposes Prometheus: `management.endpoints.web.exposure.include=health,info,prometheus`
  - Sets logging pattern with trace/span IDs for correlation.

## How to run and verify

1) Start (or rebuild after changes):
   - `docker compose build app`
   - `docker compose up -d`

2) Verify services:
   - Prometheus targets: `http://localhost:9090/targets` → `spring-app` is UP
   - Grafana: `http://localhost:3000` (admin/admin)
     - Explore → Tempo: service = `task-flow`, see traces
     - Explore → Loki: see app logs with `trace_id`/`span_id`
     - Dashboards: JVM/Micrometer, Postgres

3) Generate traffic: hit any application endpoints to produce traces/logs/metrics.

## Troubleshooting

- No traces in Tempo
  - Check app logs for OTel exporter errors.
  - Ensure envs in the runtime container resolve `tempo:4318`.

- Prometheus scrape fails
  - Confirm `app:8080/actuator/prometheus` is reachable from the `prometheus` container network.
  - Check `observability/prometheus/prometheus.yml` target host/port.

- Logs missing in Loki
  - Check Promtail logs and Docker socket mount permissions.
  - Verify `observability/promtail/config.yml` and labels.

## Notes

- We intentionally export only traces via OTel (metrics via Prometheus; logs via Promtail). This avoids double shipping and the 404 you get when sending metrics to Tempo.
- If you prefer direct app → Loki logging, add `loki-logback-appender` and consider disabling Promtail for the app to avoid duplication.

