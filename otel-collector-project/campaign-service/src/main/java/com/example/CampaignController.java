package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2025 by KakaoHealthcare. All right reserved.
 * @since 2025. 8. 6.
 */
@RestController
public class CampaignController {

    @GetMapping("/api/campaign/create")
    public String campaignCreate() {
        return "success";
    }
}
