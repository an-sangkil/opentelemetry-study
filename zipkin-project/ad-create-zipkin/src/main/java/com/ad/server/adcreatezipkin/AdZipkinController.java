package com.ad.server.adcreatezipkin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@Slf4j
@RestController
public class AdZipkinController {

    @GetMapping("/ad/create/zipkin/hello")
    public String hello() {

        RestClient restClient = RestClient.create();

        var adServerResult = restClient.get()
                .uri("http://localhost:9000/ad/server/zipkin/hello")
                .retrieve()
                .body(String.class);

        log.debug("data = {}", adServerResult);

        return "Hello World zipkin";
    }
}
