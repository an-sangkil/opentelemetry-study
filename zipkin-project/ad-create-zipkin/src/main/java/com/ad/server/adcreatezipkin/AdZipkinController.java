package com.ad.server.adcreatezipkin;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdZipkinController {

    @GetMapping("/ad/zipkin/hello")
    public String hello() {

        return "Hello World zipkin";
    }
}
