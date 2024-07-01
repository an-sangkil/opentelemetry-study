package com.ad.server.adcreatezipkin.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 6. 21.
 */
@Configuration
public class RestClientConfig {

    /**
     * 해당 컨피그를 등록해 줌으로, 수동으로 header 에 설정해야할 traceId 등등에 대한 것들을 자동으로 부여 할 수 있다.
     *
     * @param builder
     * @param observationRegistry
     * @return
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder, ObservationRegistry observationRegistry) {
        return builder
                .observationRegistry(observationRegistry)
                .build();
    }
}
