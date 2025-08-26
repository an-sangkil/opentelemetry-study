# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an OpenTelemetry study project implementing a distributed microservices architecture with comprehensive observability. The project demonstrates two different OpenTelemetry implementation approaches across multiple Spring Boot services.

**Technology Stack:**
- Java 21
- Spring Boot 3.5.4
- Spring Cloud 2025.0.0

## Architecture

The system consists of 6 main components:

1. **eureka-server** (port varies) - Service discovery registry
2. **api-gateway** (port 80) - Spring Cloud Gateway routing requests to services
3. **campaign-service** (port 8080) - Business service using pure OpenTelemetry SDK
4. **serving-service** (port 8080) - Business service using Micrometer + OpenTelemetry bridge
5. **user-service** (port varies) - Basic user management service
6. **OpenTelemetry Infrastructure** - Collector, Tempo, and Grafana for observability


## OpenTelemetry Implementation Approaches

### Campaign Service - Pure OpenTelemetry
- Uses `io.opentelemetry:opentelemetry-bom:1.32.0`
- Direct OpenTelemetry SDK implementation
- Spring Boot starter: `opentelemetry-spring-boot-starter:1.32.0`

### Serving Service - Micrometer Bridge
- Uses `io.micrometer:micrometer-tracing-bridge-otel`
- Micrometer abstraction with OpenTelemetry backend
- Additional log shipping via `opentelemetry-logback-appender:1.32.0`
- Supports both OTLP and Prometheus metrics

## Common Development Commands

### Build and Run
```bash
# Build entire project
./gradlew build

# Run specific service
./gradlew :campaign-service:bootRun
./gradlew :serving-service:bootRun
./gradlew :api-gateway:bootRun
./gradlew :eureka-server:bootRun
./gradlew :user-service:bootRun

# Run all services (if configured)
./gradlew bootRun
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific service tests
./gradlew :campaign-service:test
./gradlew :serving-service:test
```

### Infrastructure
```bash
# Start OpenTelemetry infrastructure (Collector, Tempo, Grafana)
docker-compose up -d

# Stop infrastructure
docker-compose down
```

## Key Configuration Files

### OpenTelemetry Collector
- `collector-config.yaml` - OTLP receivers, tail sampling, exporters to Tempo
- Receives data on ports 4317 (gRPC) and 4318 (HTTP)

### Grafana Tempo
- `tempo-config.yaml` - Trace storage backend configuration
- UI available at http://localhost:3200

### Service Configuration
- Each service has `application.yml` with Eureka client and observability settings
- Serving service includes `logback-spring.xml` for OTLP log shipping

## Service Dependencies

All services depend on:
- Spring Boot 3.5.4
- Spring Cloud 2025.0.0
- Java 21
- Eureka Client for service discovery
- Spring Boot Actuator

### Campaign Service Additional Dependencies
- Pure OpenTelemetry SDK
- Prometheus metrics registry

### Serving Service Additional Dependencies  
- Micrometer tracing bridge
- OTLP metrics registry
- OpenTelemetry Logback appender

### API Gateway Additional Dependencies
- Spring Cloud Gateway
- Custom filters: CampaignAuthFilter, CampaignLoggingFilter

## Monitoring Endpoints

- Grafana UI: http://localhost:3000
- Tempo UI: http://localhost:3200
- Service metrics: http://localhost:8080/actuator/prometheus
- Service health: http://localhost:8080/actuator/health

## Data Flow

1. Requests → API Gateway → Services
2. Services generate traces/metrics/logs → OpenTelemetry Collector (port 4317/4318)
3. Collector processes and exports → Tempo (traces) and file outputs
4. Grafana queries Tempo for trace visualization

## Service Routing (API Gateway)

- `/api/campaign/**` → campaign-service (with auth/logging filters)
- `/api/serving/**` → serving-service (with path rewriting)
- `/api/user/**` → user-service (with path rewriting)