# OpenTelemetry Study Project

OpenTelemetry를 활용한 분산 추적 시스템 구현 예제 모음입니다.  
다양한 백엔드와 구현 방식으로 OpenTelemetry의 활용법을 학습할 수 있습니다.

### 프로젝트 목록
- [otel-collector-project](#1-otel-collector-project---프로덕션급-마이크로서비스-아키텍처)
- [tempo-oltplibrary-project](#2-tempo-optllibrary-project---otlp-직접-연동)
- [tempo-zipkinlibrary-project](#3-tempo-zipkinlibrary-project---zipkin-호환-모드)
- [ziplin-project](#4-zipkin-project---전통적인-zipkin-구현)

[Stack](#stack-program)
~생략~
#Stack Program


## 🏗️ 프로젝트 구조

### 1. **otel-collector-project** - 프로덕션급 마이크로서비스 아키텍처
**목적**: Spring Cloud 기반의 완전한 마이크로서비스 생태계에서 OpenTelemetry Collector와 Grafana Tempo를 활용한 고급 관찰성 구현

**핵심 구성요소**:
- **Eureka Server**: 서비스 디스커버리
- **API Gateway**: Spring Cloud Gateway 기반 라우팅 및 필터링  
- **Campaign Service**: 순수 OpenTelemetry SDK 구현 (포트 8080)
- **Serving Service**: Micrometer + OpenTelemetry 브릿지 구현 (포트 8081)
- **User Service**: 기본 사용자 관리 서비스
- **OpenTelemetry Collector**: OTLP 수신 및 데이터 처리 (포트 4317/4318)
- **Grafana Tempo**: 분산 추적 저장소 (포트 3200)
- **Grafana**: 시각화 대시보드 (포트 3000)

**기술 스택**: Java 21, Spring Boot 3.5.4, Spring Cloud 2025.0.0

### 2. **tempo-optllibrary-project** - OTLP 직접 연동
**목적**: OpenTelemetry Protocol(OTLP)을 사용해 Grafana Tempo에 직접 데이터를 전송하는 방식 학습

**특징**:
- Micrometer Tracing + OpenTelemetry 브릿지 활용
- `io.opentelemetry:opentelemetry-exporter-otlp` 사용
- Tempo로 직접 트레이스 데이터 전송

### 3. **tempo-zipkinlibrary-project** - Zipkin 호환 모드
**목적**: Zipkin 프로토콜을 통해 Grafana Tempo로 데이터를 전송하는 OpenTelemetry 표준 호환성 검증

**특징**:
- `io.opentelemetry:opentelemetry-exporter-zipkin` 사용
- Tempo의 Zipkin 호환 엔드포인트 활용
- Spring WebFlux 기반 리액티브 구현

### 4. **zipkin-project** - 전통적인 Zipkin 구현
**목적**: 기존 Zipkin 생태계와의 비교 및 마이그레이션 참고용

**특징**:
- 순수 Zipkin 구현
- OpenTelemetry로의 마이그레이션 전 베이스라인

### 5. **httpcall** - 테스트 도구
**목적**: HTTP 요청을 통한 트레이싱 테스트용 스크립트 모음

## 🎯 각 프로젝트별 학습 목표

| 프로젝트 | 학습 목표 | 적용 시나리오 |
|---------|----------|-------------|
| **otel-collector-project** | 엔터프라이즈급 관찰성 아키텍처 | 대규모 마이크로서비스, 중앙집중식 데이터 처리 |
| **tempo-optllibrary-project** | OTLP 표준 프로토콜 활용 | 표준 준수 우선, 최신 OpenTelemetry 기능 활용 |
| **tempo-zipkinlibrary-project** | 하위 호환성 유지 | 기존 Zipkin 인프라와 호환성 필요한 환경 |
| **zipkin-project** | 레거시 비교 및 마이그레이션 | 기존 Zipkin에서 OpenTelemetry로 점진적 마이그레이션 |

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 21
- Docker & Docker Compose
- Gradle 8.x

### 빠른 시작
```bash
# 1. otel-collector-project (권장 시작점)
cd otel-collector-project
docker-compose up -d  # 인프라 서비스 시작
./gradlew bootRun     # 모든 서비스 실행

# 2. 개별 프로젝트 실행
cd tempo-optllibrary-project/ad-otlp-create
./gradlew bootRun

# 3. 모니터링 접속
# - Grafana: http://localhost:3000
# - Tempo UI: http://localhost:3200  
```

## 📊 관찰성 스택 비교

| 구성요소 | otel-collector | tempo-optl | tempo-zipkin | zipkin |
|---------|---------------|------------|--------------|--------|
| **트레이싱** | OpenTelemetry Collector → Tempo | 직접 OTLP → Tempo | Zipkin Protocol → Tempo | Zipkin Server |
| **메트릭** | Prometheus/OTLP | OTLP | OTLP | - |
| **로그** | OTLP → Loki | - | - | - |
| **시각화** | Grafana | Grafana | Grafana | Zipkin UI |

## 🚀 프로젝트 진행 순서 (권장)

1. **zipkin-project**: 기존 분산 추적의 기본 개념 이해
2. **tempo-zipkinlibrary-project**: OpenTelemetry + Zipkin 호환성 학습  
3. **tempo-optllibrary-project**: 표준 OTLP 구현 학습
4. **otel-collector-project**: 엔터프라이즈급 아키텍처 구현

## 📋 주요 학습 포인트

- **OpenTelemetry 구현 방식 비교**: 순수 SDK vs Micrometer 브릿지
- **데이터 전송 프로토콜**: OTLP vs Zipkin Protocol  
- **인프라 아키텍처**: 직접 연동 vs Collector 중앙 집중식
- **Spring 생태계 통합**: Spring Boot, Spring Cloud와의 연동
- **관찰성 데이터 종류**: 트레이스, 메트릭, 로그 통합 관리
