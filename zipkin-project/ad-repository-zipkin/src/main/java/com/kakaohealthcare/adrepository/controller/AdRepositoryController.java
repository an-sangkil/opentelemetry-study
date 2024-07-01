package com.kakaohealthcare.adrepository.controller;

import com.kakaohealthcare.adrepository.repository.AdCampaignJpaRepository;
import com.kakaohealthcare.adrepository.repository.CampaignEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 7. 1.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdRepositoryController {

    final AdCampaignJpaRepository adCampaignJpaRepository;


    @PostMapping("/ad/repository/campaign/save")
    public String campaignSave(HttpServletRequest request) {


        String campaignName = (String) request.getParameter("campaignName");
        log.info("/ad/repository/campaign/save {}", campaignName);
        adCampaignJpaRepository.save(CampaignEntity.builder().campaignName(campaignName).build());

        return "saved " + campaignName;

    }

}
