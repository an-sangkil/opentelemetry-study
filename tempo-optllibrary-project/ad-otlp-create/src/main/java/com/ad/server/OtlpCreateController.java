package com.ad.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtlpCreateController {

    @GetMapping("/otlp/creates/hello")
    public String hello() {

        return "Hello World Otlp Create";
    }
}
