# Campaign Service - Pure OpenTelemetry Implementation

이 서비스는 **순수 OpenTelemetry SDK** 방식을 사용한 관측 가능성 구현을 보여줍니다.

## 구현 방식

### Technology Stack
- **OpenTelemetry SDK**: `io.opentelemetry:opentelemetry-bom:1.32.0`
- **Micrometer Bridge**: `io.micrometer:micrometer-tracing-bridge-otel`
- **OTLP Exporter**: `io.opentelemetry:opentelemetry-exporter-otlp`

### Key Dependencies
```gradle
// Micrometer OpenTelemetry 브릿지 방식
implementation 'io.micrometer:micrometer-tracing-bridge-otel'
implementation 'io.opentelemetry:opentelemetry-exporter-otlp'

// Prometheus 메트릭 (Actuator)
runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
```

## OpenTelemetry 설정

### Bean Configuration
```java
@Configuration
public class OpenTelemetryConfig {
    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer("campaign-service");
    }

    @Bean  
    public Meter meter() {
        return GlobalOpenTelemetry.getMeter("campaign-service");
    }
}
```

### Application Configuration
```yaml
management:
  otlp:
    tracing:
      endpoint: http://localhost:4318  # HTTP endpoint
    metrics:
      export:
        enabled: true
        url: http://localhost:4318/v1/metrics
        step: 10s
  tracing:
    sampling:
      probability: 1.0
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} %clr(%-5level) %clr([${spring.application.name:-}]){yellow} %clr([%15.15t]){faint} %clr([%X{traceId:-},%X{spanId:-}]){magenta} %clr(%-40.40logger{39}){cyan} : %msg%n"
```

## 트레이싱 구현 예제

### 1. 수동 트레이싱 (Manual Instrumentation)
```java
@GetMapping("/api/campaign/create")
public String campaignCreate() {
    // 수동 스팬 생성
    Span span = tracer.spanBuilder("campaign_create")
        .setSpanKind(SpanKind.SERVER)
        .startSpan();

    try (Scope scope = span.makeCurrent()) {
        // 스팬 속성 추가
        span.setAttribute("campaign.operation", "create");
        span.setAttribute("http.method", "GET");
        
        // 메트릭 카운터 증가
        campaignCreateCounter.add(1);
        
        // 비즈니스 로직...
        return "success";
    } catch (Exception e) {
        span.recordException(e);
        span.setStatus(StatusCode.ERROR, e.getMessage());
        throw new RuntimeException(e);
    } finally {
        span.end();
    }
}
```

### 2. 자동 트레이싱 (Auto Instrumentation)
```java
@GetMapping("/api/campaign/list")
public String campaignList() throws InterruptedException {
    // Spring Boot가 자동으로 HTTP 요청을 트레이싱
    log.info("Fetching campaign list");
    Thread.sleep(30);
    return "campaign-list-data";
}
```

## serving-service와의 차이점

| 특징 | campaign-service | serving-service |
|------|------------------|-----------------|
| **구현 방식** | 순수 OpenTelemetry SDK | Micrometer + OpenTelemetry Bridge |
| **트레이싱** | 수동 스팬 생성 | 자동 계측 중심 |
| **메트릭** | OpenTelemetry Meter | Micrometer 추상화 |
| **설정 복잡도** | 높음 | 낮음 |
| **제어 수준** | 세밀함 | 표준화됨 |

## 장단점 비교

### 장점
- **완전한 제어**: 스팬 생성부터 속성까지 세밀하게 제어 가능
- **OpenTelemetry 표준 준수**: 순수 OpenTelemetry 방식
- **성능 최적화**: 필요한 부분만 계측 가능
- **벤더 중립**: OpenTelemetry 표준을 완전히 따름

### 단점  
- **개발 복잡도 증가**: 수동으로 스팬을 관리해야 함
- **실수 가능성**: 스팬을 제대로 닫지 않으면 메모리 누수 위험
- **보일러플레이트 코드**: 반복적인 트레이싱 코드 필요

## 사용 사례

이 방식은 다음과 같은 경우에 적합합니다:

1. **세밀한 제어가 필요한 경우**
2. **복잡한 비즈니스 로직의 상세한 추적**
3. **커스텀 메트릭과 속성이 많이 필요한 경우**
4. **OpenTelemetry 표준을 완전히 준수해야 하는 경우**

## 실행 방법

```bash
# 빌드 및 실행
./gradlew :campaign-service:bootRun

# 테스트 엔드포인트
curl http://localhost:8080/api/campaign/create
curl http://localhost:8080/api/campaign/list
```

## 모니터링 엔드포인트

- **Prometheus 메트릭**: `http://localhost:8080/actuator/prometheus`
- **Health Check**: `http://localhost:8080/actuator/health`
- **사용자 정의 메트릭**: `campaign_create_requests_total`