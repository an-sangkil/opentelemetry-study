package com.ad.server.adotlpserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtlpServerController {

    @GetMapping("/otlp/server/hello")
    public String hello() {

        return "Hello World Otlp Server";
    }
}
