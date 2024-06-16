package com.ad.adrepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepositoryController {

    @GetMapping("/hello")
    public String hello() {
        return "open telemetry hello";
    }
}
