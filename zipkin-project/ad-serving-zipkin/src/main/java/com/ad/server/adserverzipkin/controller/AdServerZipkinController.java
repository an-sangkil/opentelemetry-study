package com.ad.server.adserverzipkin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AdServerZipkinController {


    @GetMapping("/ad/serving/campaign")
    public String servingCampaign(@RequestParam(required = false) String campaignId) {
        log.info("hello campaignId={}", campaignId);


        return "hello campaignId=" + campaignId;

    }

}
