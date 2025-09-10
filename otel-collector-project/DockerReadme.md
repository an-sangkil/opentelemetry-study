# OpenTelemetry Docker 환경 설치 및 실행 가이드

이 문서는 OpenTelemetry 스택을 Docker 환경에서 구성하고 실행하는 방법을 설명합니다.

## 🏗️ 아키텍처 개요

OpenTelemetry 스택은 다음 컴포넌트들로 구성됩니다:

```
Spring Boot App → OpenTelemetry Collector → Tempo (트레이스)
                                        → Loki (로그)
                                        → Grafana (시각화)
```

## 📋 서비스 구성

### 1. OpenTelemetry Collector
- **역할**: 관측 데이터 수집 및 전송 허브
- **포트**: 4317 (OTLP gRPC), 4318 (OTLP HTTP)
- **설정 파일**: `collector-config.yaml`

### 2. Grafana Tempo
- **역할**: 분산 트레이스 저장 및 조회
- **포트**: 3200 (UI), 4417 (OTLP gRPC), 4418 (OTLP HTTP)
- **설정 파일**: `tempo-config.yaml`

### 3. Grafana Loki
- **역할**: 로그 수집 및 저장
- **포트**: 3100 (HTTP API)

### 4. Grafana
- **역할**: 통합 관측 가능성 대시보드
- **포트**: 3000 (웹 UI)
- **접속**: http://localhost:3000 (익명 로그인 활성화)

## 🚀 실행 방법

### 1. Docker 환경 시작
```bash
# OpenTelemetry 스택 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 상태 확인
docker-compose ps
```

### 2. 서비스 확인
- **Grafana**: http://localhost:3000
- **Tempo UI**: http://localhost:3200
- **Collector 헬스체크**: curl http://localhost:4318/v1/traces

### 3. Spring Boot 애플리케이션 연결
Spring Boot 애플리케이션에서 다음 설정을 사용하여 Collector에 연결:

```yaml
# application.yml
management:
  otlp:
    tracing:
      endpoint: http://localhost:4317  # gRPC 엔드포인트
```

또는 HTTP 사용 시:
```yaml
management:
  otlp:
    tracing:
      endpoint: http://localhost:4318  # HTTP 엔드포인트
```

## 🔧 설정 파일

### collector-config.yaml
OpenTelemetry Collector의 수신기, 프로세서, 내보내기 설정

### tempo-config.yaml  
Tempo의 스토리지 및 수신 설정

### grafana/provisioning/datasources/
Grafana 데이터소스 자동 설정 (Tempo, Loki)

## 📊 모니터링

### 트레이스 확인
1. Grafana (http://localhost:3000) 접속
2. Explore → Tempo 선택
3. 트레이스 ID로 검색하거나 서비스별 필터링

### 로그 확인
1. Grafana에서 Explore → Loki 선택
2. 로그 쿼리 작성 및 확인

## 🛑 환경 관리

### 전체 환경 종료
```bash
# 모든 서비스 종료
docker-compose down

# 볼륨까지 삭제 (데이터 완전 삭제)
docker-compose down -v
```

### 특정 컨테이너 관리
```bash
# 특정 서비스만 재시작
docker-compose restart otel-collector
docker-compose restart tempo
docker-compose restart grafana
docker-compose restart loki

# 특정 서비스만 종료
docker-compose stop otel-collector
docker-compose stop tempo

# 특정 서비스만 시작
docker-compose start otel-collector
docker-compose start tempo

# 특정 서비스 강제 재생성 (설정 변경 후)
docker-compose up -d --force-recreate otel-collector
docker-compose up -d --force-recreate tempo
# 옵션 설명:
# -d: 백그라운드에서 실행 (detached 모드)
# --force-recreate: 기존 컨테이너를 강제로 삭제하고 새로 생성

# 특정 컨테이너 삭제 후 재생성
docker-compose rm -sf tempo        # 컨테이너 강제 삭제
docker-compose up -d tempo         # 새로 생성 및 시작

# 특정 서비스 로그 확인
docker-compose logs -f otel-collector
docker-compose logs -f tempo
```

### 설정 변경 후 적용
```bash
# collector-config.yaml 변경 후
docker-compose restart otel-collector

# tempo-config.yaml 변경 후  
docker-compose restart tempo

# grafana 데이터소스 변경 후
docker-compose restart grafana
```

### 삭제 후 재실행 
```shell
docker-compose rm -sf {containerName}
docker-compose rm -sf tempo 
# 옵션 
# - s : 컨테이너 중지후 삭제  
# - f : 강제 삭제  
```
 
## 📝 주의사항

### 포트 구성
- **표준 포트**: Collector는 IANA 표준 포트 4317/4318 사용
- **개발 포트**: Tempo는 개발환경에서 포트 구분을 위해 4417/4418 사용
- **운영 환경**: 각 서비스가 별도 서버에 배포되므로 모두 표준 포트 사용 권장

### 데이터 영속성
- Docker 볼륨을 통해 컨테이너 재시작 시에도 데이터 보존
- 완전 초기화가 필요한 경우 `docker-compose down -v` 사용

### 개발 모드 설정/
- Grafana 익명 접근 활성화 (개발용)
- 운영 환경에서는 보안 설정 강화 필요