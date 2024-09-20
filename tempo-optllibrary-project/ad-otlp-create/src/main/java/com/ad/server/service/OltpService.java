package com.ad.server.service;

import com.ad.server.config.Traced;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 9. 20.
 */
@Slf4j
@Service
public class OltpService {

    final RestClient restClient;

    public OltpService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Traced
    public void recommendCampaignCall(String gender, String age) {

        restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost").port("8081")
                        .path("/recommend/v1/campaign")
                        .build()
                )
                .retrieve()
                .body(String.class)
        ;


        log.info("ad-server recommend campaign call");

    }
}
