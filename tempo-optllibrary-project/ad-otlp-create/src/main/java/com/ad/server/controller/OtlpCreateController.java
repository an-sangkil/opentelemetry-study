package com.ad.server.controller;

import com.ad.server.service.OltpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OtlpCreateController {

    final OltpService oltpService;

    @GetMapping("/otlp/creates/hello")
    public String hello(String gender, String age) {

        oltpService.recommendCampaignCall(gender, age);

        return "Hello World Oltp Create";
    }
}
