# Serving Service 모듈

## 개요
OpenTelemetry 스터디 프로젝트의 Serving Service 모듈입니다. Micrometer 기반의 관측 가능성(Observability) 구현을 통해 분산 추적, 메트릭 수집, 로그 전송 기능을 제공합니다.

## 주요 기능
- **분산 추적**: Micrometer를 통한 OpenTelemetry 트레이스 생성 및 전송
- **메트릭 수집**: OTLP 및 Prometheus 형식의 메트릭 수집
- **로그 전송**: OTLP 프로토콜을 통한 중앙 집중식 로그 관리
- **서비스 디스커버리**: Eureka를 통한 마이크로서비스 등록 및 발견

## 기술 스택
- **Spring Boot**: 애플리케이션 프레임워크
- **Spring Web**: RESTful API 제공
- **Eureka Client**: 서비스 디스커버리 클라이언트
- **Micrometer**: 메트릭 및 트레이싱 추상화 라이브러리
- **OpenTelemetry**: 분산 추적 표준

## 관측 가능성 구성

### 1. 분산 추적
```gradle
implementation 'io.micrometer:micrometer-tracing-bridge-otel'
```
- Micrometer의 트레이싱 추상화를 OpenTelemetry 구현체로 연결
- 자동 트레이스 ID 및 스팬 ID 생성
- MDC(Mapped Diagnostic Context)를 통한 로그와 트레이스 연동

### 2. 메트릭 수집
```gradle
runtimeOnly 'io.micrometer:micrometer-registry-otlp'      // OTLP 프로토콜 전송
runtimeOnly 'io.micrometer:micrometer-registry-prometheus' // Prometheus 메트릭 노출
```
- **OTLP 방식**: Push 방식으로 OpenTelemetry Collector에 메트릭 전송
- **Prometheus 방식**: Pull 방식으로 `/actuator/prometheus` 엔드포인트 노출

### 3. 로그 전송
```gradle
implementation 'io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.19.0-alpha'
```
- OpenTelemetry Logback Appender를 통한 OTLP 로그 전송
- `logback-spring.xml` 설정을 통한 콘솔과 OTLP 이중 출력
- 트레이스 ID와 스팬 ID가 자동으로 로그에 포함
- Micrometer와 OpenTelemetry 하이브리드 방식으로 완전한 관측 가능성 구현

## 설정 구성

### application.yml
```yaml
spring:
  application:
    name: serving-service

logging:
  level:
    root: info
    com.example: debug
```

### logback-spring.xml
```xml
<appender name="OTLP" class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
    <endpoint>http://localhost:4317</endpoint>
    <captureExperimentalAttributes>true</captureExperimentalAttributes>
    <captureKeyValuePairAttributes>true</captureKeyValuePairAttributes>
    <captureLoggerContext>true</captureLoggerContext>
    <captureMarkerAttribute>true</captureMarkerAttribute>
    <captureMdcAttributes>true</captureMdcAttributes>
</appender>
```

#### ⚠️ 중요: gRPC 엔드포인트 설정
OpenTelemetry에서 gRPC 연결시 스키마 사용법:
- ✅ `http://localhost:4317` - insecure gRPC 연결 (올바름)
- ✅ `https://localhost:4317` - secure gRPC 연결 (TLS)
- ❌ `grpc://localhost:4317` - OpenTelemetry 표준에서 사용하지 않음

**포트별 프로토콜**:
- **4317**: gRPC (하지만 `http://` 스키마 사용)
- **4318**: HTTP REST API

## 아키텍처 선택 배경

### Micrometer 기반 접근 방식 채택 이유
1. **Spring Boot 생태계 통합**: Spring Actuator와의 자연스러운 연동
2. **점진적 도입**: 기존 Spring 애플리케이션에 최소한의 변경으로 적용 가능
3. **다중 백엔드 지원**: OTLP, Prometheus 등 여러 모니터링 시스템 동시 지원
4. **안정성**: Spring 진영에서 검증된 메트릭 수집 방식

### 순수 OpenTelemetry 대비 장단점

#### 장점
- Spring Boot 자동 설정 활용
- 기존 Spring 메트릭과의 호환성
- 설정 복잡도 감소

#### 단점
- 여러 라이브러리 조합으로 인한 복잡성
- OpenTelemetry 표준 완전 준수 어려움
- 로그 전송을 위한 별도 설정 필요

## 데이터 흐름

```리드 
Serving Service
    ├── 트레이스 → micrometer-tracing-bridge-otel → OTLP → OpenTelemetry Collector
    ├── 메트릭 → micrometer-registry-otlp → OTLP → OpenTelemetry Collector
    │         → micrometer-registry-prometheus → /actuator/prometheus → Prometheus
    └── 로그 → opentelemetry-logback-appender → OTLP → OpenTelemetry Collector
```

## 실행 방법
```bash
./gradlew :serving-service:bootRun
```

## 모니터링 엔드포인트
- **Prometheus 메트릭**: `http://localhost:8080/actuator/prometheus`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Eureka 등록 정보**: Eureka Server에서 확인 가능