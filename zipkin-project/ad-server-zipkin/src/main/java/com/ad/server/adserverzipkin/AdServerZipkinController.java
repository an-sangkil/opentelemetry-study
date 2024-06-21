package com.ad.server.adserverzipkin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AdServerZipkinController {


    @GetMapping("/ad/server/zipkin/hello")
    public String hello() {


        log.info("zipkin server hello ");
        return "Hello World zipkin hello";
    }

}
