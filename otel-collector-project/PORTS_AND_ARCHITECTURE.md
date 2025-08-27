# OpenTelemetry 포트 설정 및 아키텍처 가이드

## 개요

이 문서는 OpenTelemetry Collector와 Tempo 간의 포트 설정과 아키텍처를 설명합니다.
개발 환경(Docker Compose)과 실제 운영 환경 간의 차이점도 함께 다룹니다.

## 아키텍처 다이어그램

### 현재 개발 환경 (Docker Compose)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Spring Boot    │    │ OpenTelemetry   │    │ Grafana Tempo   │
│  Applications   │    │   Collector     │    │                 │
│                 │    │                 │    │                 │
│ OTLP → 4317 ────┼────┼→ 4317 → 4417 ───┼────┼→ 4417 (gRPC)   │
│ OTLP → 4318 ────┼────┼→ 4318 → 4418 ───┼────┼→ 4418 (HTTP)   │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
  localhost:8080         localhost:4317/4318   localhost:4417/4418
```

### 실제 운영 환경
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   App Server    │  │ Collector Server│  │  Tempo Server   │
│                 │  │                 │  │                 │
│ OTLP → 4317 ────┼──┼→ 4317 → 4317 ───┼──┼→ 4317 (gRPC)   │
│ OTLP → 4318 ────┼──┼→ 4318 → 4318 ───┼──┼→ 4318 (HTTP)   │
│                 │  │                 │  │                 │
└─────────────────┘  └─────────────────┘  └─────────────────┘
   server-1.com        server-2.com        server-3.com
```

## 포트 설정 상세

### OpenTelemetry 표준 포트

| 포트 | 프로토콜 | 용도 | 설명 |
|-----|----------|------|------|
| 4317 | gRPC | OTLP gRPC | IANA 공식 할당 포트 |
| 4318 | HTTP | OTLP HTTP | IANA 공식 할당 포트 |

### 현재 개발 환경 포트 매핑

#### OpenTelemetry Collector
| 내부 포트 | 외부 포트 | 프로토콜 | 용도 |
|----------|----------|----------|------|
| 4317 | 4317 | gRPC | Spring Boot 애플리케이션에서 트레이스 수신 |
| 4318 | 4318 | HTTP | Spring Boot 애플리케이션에서 트레이스 수신 |

#### Grafana Tempo
| 내부 포트 | 외부 포트 | 프로토콜 | 용도 |
|----------|----------|----------|------|
| 3200 | 3200 | HTTP | Tempo UI 및 Query API |
| 4417 | 4417 | gRPC | OTLP gRPC 수신 (Collector에서 전송) |
| 4418 | 4418 | HTTP | OTLP HTTP 수신 (Collector에서 전송) |
| 9095 | N/A | gRPC | Tempo 내부 서비스 (외부 노출 안함) |

### 포트 충돌과 해결책

#### 문제점
개발 환경에서 모든 서비스가 하나의 Docker Compose 파일에 정의되어 있어 포트 충돌이 발생:

1. **Collector**와 **Tempo** 모두 4317/4318 포트를 사용하려고 함
2. **Tempo**의 server gRPC와 distributor OTLP gRPC가 같은 포트를 사용하려고 함

#### 해결책
1. **Collector**: 표준 포트 4317/4318 유지 (애플리케이션 연결점)
2. **Tempo**: 구분 가능한 포트 사용 (4417/4418)
3. **Tempo 내부 서비스**: 별도 포트 사용 (9095)

## 설정 파일별 포트 설정

### collector-config.yaml
```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317  # 표준 OTLP gRPC 포트
      http:
        endpoint: 0.0.0.0:4318  # 표준 OTLP HTTP 포트

exporters:
  otlp/tempo:
    endpoint: tempo:4417      # Tempo OTLP gRPC 포트
```

### tempo-config.yaml
```yaml
server:
  http_listen_port: 3200      # Tempo UI/API (표준)
  grpc_listen_port: 9095      # Tempo 내부 gRPC 서비스

distributor:
  receivers:
    otlp:
      protocols:
        grpc:
          endpoint: 0.0.0.0:4417  # Collector에서 받는 OTLP gRPC
        http:
          endpoint: 0.0.0.0:4418  # Collector에서 받는 OTLP HTTP
```

### docker-compose.yaml
```yaml
otel-collector:
  ports:
    - "4317:4317"  # OTLP gRPC (애플리케이션 → Collector)
    - "4318:4318"  # OTLP HTTP (애플리케이션 → Collector)

tempo:
  ports:
    - "3200:3200"  # Tempo UI
    - "4417:4417"  # OTLP gRPC (Collector → Tempo)
    - "4418:4418"  # OTLP HTTP (Collector → Tempo)
```

## 운영 환경 권장 설정

실제 운영 환경에서는 각 서비스가 별도 서버에 배포되므로 모든 서비스가 표준 포트를 사용할 수 있습니다:

### Collector 서버
```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317  # 표준 포트
      http:
        endpoint: 0.0.0.0:4318  # 표준 포트

exporters:
  otlp:
    endpoint: tempo-server.com:4317  # Tempo 서버 표준 포트
```

### Tempo 서버
```yaml
server:
  http_listen_port: 3200
  grpc_listen_port: 9095

distributor:
  receivers:
    otlp:
      protocols:
        grpc:
          endpoint: 0.0.0.0:4317  # 표준 포트 사용 가능!
        http:
          endpoint: 0.0.0.0:4318  # 표준 포트 사용 가능!
```

### 애플리케이션 설정
```yaml
management:
  otlp:
    tracing:
      endpoint: "http://collector-server.com:4317"  # 표준 포트
```

## 데이터 흐름

### 트레이스 데이터 흐름
1. **Spring Boot App** → OTLP (4317/4318) → **Collector**
2. **Collector** → 샘플링/배치 처리 → OTLP (4417/4418) → **Tempo**
3. **Tempo** → 저장 → **Grafana**에서 조회

### 로그 데이터 흐름
1. **Spring Boot App** → OTLP (4318) → **Collector**
2. **Collector** → HTTP (3100) → **Loki**
3. **Loki** → **Grafana**에서 조회

## 트러블슈팅

### 일반적인 포트 관련 문제

1. **"address already in use" 오류**
   - 같은 포트를 두 개 이상의 서비스가 사용하려고 할 때
   - 해결: 각 서비스마다 고유한 포트 할당

2. **"connection refused" 오류**
   - 서비스가 시작되지 않았거나 잘못된 포트로 연결 시도
   - 해결: 서비스 상태 확인 및 포트 설정 검토

3. **Docker 네트워크 연결 문제**
   - `localhost` 대신 `0.0.0.0`으로 바인딩 필요
   - 컨테이너 간 통신 시 서비스명 사용 (`tempo:9095`)

## 참고 자료

- [OpenTelemetry Collector Configuration](https://opentelemetry.io/docs/collector/configuration/)
- [Grafana Tempo Configuration](https://grafana.com/docs/tempo/latest/configuration/)
- [OTLP Specification](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/protocol/otlp.md)
- [IANA Port Numbers](https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml)