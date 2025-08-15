package com.example;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 광고 데이터를 제공하는 컨트롤러 클래스.
 * - 이 클래스는 광고 캠페인 ID를 받아 해당 캠페인에 대한 정보를 제공하는 엔드포인트를 포함합니다.
 * 
 */
@Slf4j
@RestController
public class ServingController {

  
    private final RestTemplate restTemplate;

    public ServingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Endpoint to serve requests.
     * 
     * @return
     */
    @GetMapping("/sering/v1/campaign/{campaignId}")
    public ResponseEntity<?> serve(@PathVariable(name="campaignId", required = true) String campaignId) {

        log.info("Serving request for campaignId: {}", campaignId);

        // Call user-service REST API to fetch user information
        
        String userServiceUrl = "http://user-service/user/v1/info/" + campaignId;
        String userInfo;
        try {
            userInfo = restTemplate.getForObject(userServiceUrl, String.class);
        } catch (Exception e) {
            log.error("Failed to fetch user info from user-service", e);
            userInfo = "User info not available";
        }

        return ResponseEntity.ok("Serving request successfully! User info: " + userInfo);
    }
}