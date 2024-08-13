package com.kakaohealthcare.adoltpzipkinrepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 7. 17.
 */
@Slf4j
@RestController
public class AdRepositoryController {

    @PostMapping("/repository/ad/save")
    public Mono<String> adSave(ServerHttpRequest serverRequest, Campaign campaign) {


        log.info("=======================Ad Repository Save Request Headers==========================");
        serverRequest.getHeaders().forEach((s, strings) -> {
            log.info("key {} value {}", s, strings);
        });

        log.info("path{} , request body {}", "/ad/save", campaign);


        return Mono.just("ad save...");
    }

    @DeleteMapping("/repository/ad/delete")
    public Mono<String> adDelete(ServerHttpRequest serverRequest) {


        log.info("=======================Ad Repository Delete Request Headers==========================");
        serverRequest.getHeaders().forEach((s, strings) -> {
            log.info("key {} value {}", s, strings);
        });

        log.info("path{} , request body {}", "/ad/delete",  serverRequest.getBody().toString());


        return Mono.just("ad delete...");
    }
}
