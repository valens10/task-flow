# Multi-stage build for Task Flow Spring Boot app

# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Pre-copy pom.xml and resolve deps to leverage Docker layer caching
COPY pom.xml ./
# Use BuildKit cache for Maven repo to speed up builds
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests dependency:go-offline || true

# Copy source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests package

# Fetch OpenTelemetry Java agent in build stage (single source of truth)
RUN curl -fsSL -o /app/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"
ENV OTEL_SERVICE_NAME=task-flow \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://tempo:4318 \
    OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf \
    OTEL_TRACES_EXPORTER=otlp \
    OTEL_METRICS_EXPORTER=none \
    OTEL_LOGS_EXPORTER=none
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar
# Copy OpenTelemetry agent fetched during build stage
COPY --from=build /app/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Create non-root user and adjust ownership
RUN useradd -r -u 10001 appuser && chown -R appuser /app
USER appuser

EXPOSE 8080

# Use exec form to allow proper signal handling
ENTRYPOINT ["sh", "-c", "java -javaagent:/app/opentelemetry-javaagent.jar $JAVA_OPTS -jar /app/app.jar"]


