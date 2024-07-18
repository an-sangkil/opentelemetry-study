package com.ad.adrepository.config;

import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.context.propagation.ContextPropagators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.*;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 7. 16.
 */
@Configuration
public class WebClientServiceConfig {
    @Autowired
    private ContextPropagators propagators;

    //    @Bean
//    public WebClient webClient(WebClient.Builder webClientBuilder, ObservationRegistry observationRegistry) {
//        // WebClient.Builder를 사용하여 WebClient 생성
//        WebClient webClient = webClientBuilder
//                //.filter(new TracingWebClientFilter(observationRegistry))  // 트레이스 ID 필터 추가
//                .observationRegistry(observationRegistry)
//                .build();
//
//        return webClient;
//    }
    @Bean
    public WebClient webClient(ObservationRegistry observationRegistry) {
        // WebClient.Builder를 사용하여 WebClient 생성
        WebClient webClient = WebClient.builder()
                //.filter(new TracingWebClientFilter(observationRegistry))  // 트레이스 ID 필터 추가
                .observationRegistry(observationRegistry)
                .build();

        return webClient;
    }


//    @Bean
//    public ConnectionProvider connectionProvider() {
//        return ConnectionProvider.builder("http-pool")
//                .maxConnections(100)                    // connection pool의 갯수
//                .pendingAcquireTimeout(Duration.ofMillis(0)) //커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
//                .pendingAcquireMaxCount(-1)             //커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
//                .maxIdleTime(Duration.ofMillis(1000L))        //커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
//                .build();
//    }
}


