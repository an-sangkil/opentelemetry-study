package com.ad.adrepository.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 7. 16.
 */
@Slf4j
@Configuration
public class WebClientServiceConfig {
    @Autowired
    private ContextPropagators propagators;

    @Autowired
    private Tracer tracer;

    /**
     * Spring conecxt 에서 관리되는 Webclient.builder 를 사용하기 때문에 설정에 대한 공유 가능
     * @param webClientBuilder
     * @param observationRegistry
     * @return
     */
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder, ObservationRegistry observationRegistry) {
        // WebClient.Builder를 사용하여 WebClient 생성
        WebClient webClient = webClientBuilder
                .observationRegistry(observationRegistry)
                //.filter(new TracingWebClientFilter(propagators,tracer)) // 추적 필터 추가
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    clientRequest.headers().forEach((s, strings) -> {
                        log.info( "key {} , value {}", s, strings);
                    });
                    return Mono.just(clientRequest);
                }))



                .filter((request, next) -> {
                    Span span = Span.current();
                    if (span != null) {
                        Context context = Context.current();
                        ClientRequest newRequest = ClientRequest.from(request)
                                .headers(headers -> propagators.getTextMapPropagator().inject(context, headers, HttpHeaders::add))
                                .build();
                        return next.exchange(newRequest);
                    } else {
                        return next.exchange(request);
                    }
                })
//

                .build();

        return webClient;
    }


//    /**
//     * 새로운 인스턴스로 만들어 사용함.
//     * @param observationRegistry
//     * @return
//     */
//    @Bean
//    public WebClient webClient(ObservationRegistry observationRegistry) {
//        // WebClient.Builder를 사용하여 WebClient 생성
//        WebClient webClient = WebClient.builder()
//                .observationRegistry(observationRegistry)
//                .filter(new TracingWebClientFilter.create())
//                .build();
//
//        return webClient;
//    }


    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("http-pool")
                .maxConnections(100)                    // connection pool의 갯수
                .pendingAcquireTimeout(Duration.ofMillis(0)) //커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
                .pendingAcquireMaxCount(-1)             //커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
                .maxIdleTime(Duration.ofMillis(1000L))        //커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
                .build();
    }
}


