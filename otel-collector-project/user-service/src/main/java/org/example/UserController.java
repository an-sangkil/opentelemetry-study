package org.example;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * UserController class for handling user-related requests.
 * 
 */

 @Slf4j
 @RestController
public class UserController {

    /**
     * 사용자 정보를 조회하는 엔드포인트.
     *  - 단순 예시로 사용자 ID를 받아서 사용자 정보를 반환합니다.
     * 
     * @param userId
     * @return
     */        
    @GetMapping("/user/v1/info")
    public ResponseEntity<String> getUserInfo(@RequestParam String userId) {
       
        log.info("Fetching user info for userId: {}", userId);

        return ResponseEntity.ok( "User info for userId: " + userId);
    }
    

}
