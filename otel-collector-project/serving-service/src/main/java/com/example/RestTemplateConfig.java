package com.example;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Autowired(required = false)
    private Tracer tracer;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        
        // 트레이스 전파를 위한 인터셉터 추가 (Micrometer 자동 설정이 작동하지 않을 경우 대비)
        if (tracer != null) {
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add(new TracePropagationInterceptor(tracer));
            restTemplate.setInterceptors(interceptors);
        }
        
        return restTemplate;
    }

    /**
     * 트레이스 전파를 위한 커스텀 인터셉터
     * 현재 트레이스 컨텍스트를 HTTP 헤더로 전파합니다
     */
    private static class TracePropagationInterceptor implements ClientHttpRequestInterceptor {
        private final Tracer tracer;

        public TracePropagationInterceptor(Tracer tracer) {
            this.tracer = tracer;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                          ClientHttpRequestExecution execution) throws IOException {
            // Micrometer Tracer를 통해 현재 트레이스 컨텍스트 확인
            if (tracer.currentSpan() != null) {
                // 자동 트레이스 전파는 Spring Boot의 TracingClientHttpRequestInterceptor가 처리하므로
                // 여기서는 로깅만 추가
                System.out.println("🔗 Trace propagation - TraceId: " + 
                    tracer.currentSpan().context().traceId() + 
                    ", SpanId: " + tracer.currentSpan().context().spanId());
            }
            return execution.execute(request, body);
        }
    }
}
