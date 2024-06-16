package com.ad.server.adserverzipkin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdServerZipkinController {


    @GetMapping("/ad/server/zipkin/hello")
    public String hello() {

        return "Hello World zipkin hello";
    }

}
