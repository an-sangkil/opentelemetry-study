package com.ad.server.adcreatezipkin;

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

    @Bean
    public RestClient restClient(RestClient.Builder builder, ObservationRegistry observationRegistry) {
        return builder
                .observationRegistry(observationRegistry)
                .build();
    }
}
