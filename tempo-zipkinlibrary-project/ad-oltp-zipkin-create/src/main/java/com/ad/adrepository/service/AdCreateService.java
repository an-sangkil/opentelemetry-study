package com.ad.adrepository.service;

import com.ad.adrepository.config.Traced;
import com.ad.adrepository.controller.Campaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 8. 2.
 */
@Slf4j
@Service
public class AdCreateService {

    final private WebClient webClient;

    public AdCreateService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Traced
    public Mono<String> save (Campaign campaign) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .host("localhost")
                        .port(8082)
                        .path("/repository/ad/save")
                        .build())
                .bodyValue(campaign)

                .retrieve()
                .bodyToMono(String.class);

    }
}
