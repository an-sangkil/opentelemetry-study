package com.ad.adrepository.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@RestController
public class V2AdCreateTempoController {

    final private Tracer tracer;
    final private WebClient webClient;


    final private AdCreateService adCreateService;


    @PostMapping("/v2/ad/save")
    public String adSave(Campaign campaign) {


        log.info("request body {}", campaign);
        var data = adCreateService.save(campaign);
        log.info("path {}, recommend response: {}", "/ad/save", data.subscribe(System.out::println));

        return "open telemetry hello";
    }


    /**
     * Tracing 전파시 수동으로 Header에 적용
     * @return
     */
    @GetMapping("/v2/ad/delete")
    public String hello02() {
        Span currentSpan = tracer.currentSpan();
        String newSpanId = IdGenerator.random().generateSpanId().toString();

        String traceId = currentSpan != null ? currentSpan.context().traceId() : generateNewId();
        String parentSpanId = currentSpan != null ? currentSpan.context().spanId() : null;

        var data = WebClient.builder().build()
                .delete()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .queryParam("age", "11")
                        .host("localhost")
                        .port(8082)
                        .path("/ad/delete")
                        .build())
                .headers(headers -> {
                    headers.add("X-B3-TraceId", traceId);
                    headers.add("X-B3-SpanId", newSpanId);
                    headers.add("X-B3-ParentSpanId", parentSpanId);
                    headers.add("X-B3-Sampled", "1");
                })
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();


        return "open telemetry : ad delete";
    }

    private String generateNewId() {
        return Long.toHexString(new Random().nextLong());
    }
}
