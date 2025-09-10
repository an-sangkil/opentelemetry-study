

# User Service

OpenTelemetry 분산 트레이싱 및 Loki 로그 수집을 지원하는 사용자 관리 서비스입니다.

## 기술 스택
- Java 21
- Spring Boot 3.5.4
- Spring Cloud 2025.0.0
- Micrometer Tracing Bridge (OpenTelemetry)
- Loki4j Logback Appender 2.0.0

## 주요 기능
- 사용자 관리 REST API
- Eureka 서비스 디스커버리
- OpenTelemetry 분산 트레이싱
- Grafana Loki 로그 수집
- Prometheus 메트릭 수집

## Loki Logback Appender 설정

### 1. 의존성
```gradle
implementation("com.github.loki4j:loki-logback-appender:2.0.0")
```

### 2. logback.xml 설정
```xml
<appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
    <http>
        <url>http://${LOKI_IP}:3100/loki/api/v1/push</url>
    </http>
    <labels>
        service_name = ${LOKI_SERVICE:-user-service}
        host = ${HOSTNAME:-my-hostname}
        level = %level
        env = ${LOKI_ENV:-development}
        traceId = %X{traceId:-}
        spanId = %X{spanId:-}
    </labels>
    <batch>
        <maxItems>100</maxItems>
        <timeoutMs>10000</timeoutMs>
    </batch>
</appender>
```

### 3. 환경변수 설정

**JVM 옵션 (권장):**
```
-DLOKI_IP=localhost -DLOKI_SERVICE=user-service -DHOSTNAME=my-hostname -DLOKI_ENV=development
```

**또는 Environment Variables:**
- LOKI_IP=localhost
- LOKI_SERVICE=user-service  
- HOSTNAME=my-hostname
- LOKI_ENV=production

### 4. Grafana에서 로그 조회

**라벨별 조회:**
```
{service_name="user-service"}
{env="development"}
{level="ERROR"}
{traceId="your-trace-id"}
```

**복합 조건:**
```
{service_name="user-service", env="development", level="ERROR"}
```

## 실행 방법

### 1. 인프라 시작
```bash
# OpenTelemetry Collector, Tempo, Grafana 시작
docker-compose up -d
```

### 2. 서비스 실행
```bash
# Gradle 빌드 및 실행
./gradlew :user-service:bootRun

# 또는 IDE에서 실행 시 JVM 옵션 추가
-DLOKI_IP=localhost -DLOKI_SERVICE=user-service -DHOSTNAME=my-hostname -DLOKI_ENV=development
```

### 3. 확인
- **서비스 헬스체크**: http://localhost:8080/actuator/health
- **Grafana**: http://localhost:3000
- **로그 확인**: Grafana > Explore > Loki

## 모니터링 엔드포인트
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus
- Tracing: OpenTelemetry Collector → Tempo → Grafana
- Logs: Loki4j → Loki → Grafana
