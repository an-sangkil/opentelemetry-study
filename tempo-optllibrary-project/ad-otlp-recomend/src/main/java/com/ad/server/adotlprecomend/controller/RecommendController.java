package com.ad.server.adotlprecomend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 9. 20.
 */
@Slf4j
@RestController
public class RecommendController {


    @GetMapping("/recommend/v1/campaign")
    public String recommendCampaign(String gender, String age) {

        log.info("recommend campaign return success");
        return "success";
    }
}
