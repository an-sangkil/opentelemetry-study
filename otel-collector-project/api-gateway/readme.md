# API Gateway 모듈

## 개요
OpenTelemetry 스터디 프로젝트의 API Gateway 모듈입니다. Spring Cloud Gateway를 사용하여 마이크로서비스 아키텍처에서 중앙 집중식 API 관리 역할을 담당합니다.

## 주요 기능
- **라우팅**: 클라이언트 요청을 적절한 마이크로서비스로 라우팅
- **로드 밸런싱**: Eureka 서비스 디스커버리를 통한 서비스 인스턴스 로드 밸런싱
- **분산 추적**: OpenTelemetry를 활용한 요청 추적 및 모니터링
- **필터링**: 인증, 로깅 등의 횡단 관심사 처리

## 기술 스택
- **Spring Cloud Gateway**: API 게이트웨이 프레임워크
- **Spring WebFlux**: 리액티브 웹 스택
- **Eureka Client**: 서비스 디스커버리 클라이언트
- **OpenTelemetry**: 분산 추적 및 모니터링
- **Micrometer**: 메트릭 수집

## 라우팅 구성

### Campaign Service
- **경로**: `/api/campaign/**`
- **대상**: `lb://campaign-service:8080`
- **필터**: CampaignAuthFilter, CampaignLoggingFilter

### Serving Service
- **경로**: `/api/serving/**`
- **대상**: `lb://serving-service`
- **필터**: RewritePath (경로 재작성)

### User Service
- **경로**: `/api/user/**`
- **대상**: `lb://user-service`
- **필터**: RewritePath (경로 재작성)

## 의존성
```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-gateway-server-webflux'
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
implementation 'io.micrometer:micrometer-tracing-bridge-otel' // 분산 추적
runtimeOnly 'io.micrometer:micrometer-registry-otlp' // 메트릭 전송
```

## 실행 환경
- **포트**: 80
- **프로필**: Spring Boot 기본 프로필
- **로깅**: Spring Cloud Gateway 및 HTTP 요청/응답 정보 DEBUG 레벨

## 실행 방법
```bash
./gradlew bootRun
```

## 필터 구성
- **CampaignAuthFilter**: Campaign 서비스 요청에 대한 인증 처리
- **CampaignLoggingFilter**: Campaign 서비스 요청에 대한 로깅 처리
- **AddRequestHeader**: 모든 요청에 X-Request-Foo 헤더 추가
- **AddResponseHeader**: 모든 응답에 X-Response-Foo 헤더 추가

## 분산 추적
OpenTelemetry와 Micrometer를 통해 요청의 분산 추적이 가능합니다:
- 트레이스 ID와 스팬 ID 자동 생성
- MDC(Mapped Diagnostic Context)를 통한 로그 연동
- OTLP 프로토콜을 통한 수집기로 데이터 전송