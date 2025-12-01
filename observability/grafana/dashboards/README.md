# Grafana Dashboards (Provisioned)

Place exported JSON files here. They will be auto-loaded by Grafana on startup.

Suggested dashboards (Grafana.com IDs):
- 4701 — JVM (Micrometer)
- 10231 — Spring Boot Micrometer
- 17175 — Spring Boot Observability
- 9628 — PostgreSQL (Prometheus)

How to add:
1. Download JSON from Grafana.com (or export from an instance).
2. Save as JSON files in this folder (e.g., `4701-jvm.json`).
3. Restart Grafana container: `docker compose up -d grafana`.

