### Grafana Tempo 를 사용한 데이터 수집
- Zipkin Tracing tool 을 사용하여 Tempo 에 데이터 적재한다.  
- 적재된 데이터를 Grafana 를 사용하여 모니터링 한다. 

1. tempo 에서는 open tracing 의 표준을 지킴으로 zipkin 의 
exporter 라이브러리(implementation 'io.opentelemetry:opentelemetry-exporter-zipkin')를 사용하여  Tempo 로 데이터를 적재 할 수있다. 

2. micrometer-tracing-bridge-otel 라이브러리를 사용하여 otel 트레이스 구현체 사용 

### 시스템 구성도


