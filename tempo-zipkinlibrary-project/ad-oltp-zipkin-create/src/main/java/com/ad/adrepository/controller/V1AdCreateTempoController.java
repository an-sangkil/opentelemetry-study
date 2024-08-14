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

/**
 * V2
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class V1AdCreateTempoController {

    final private Tracer tracer;
    final private WebClient webClient;

    /**
     *  {@link com.ad.adrepository.config.WebClientServiceConfig} 에서 선언된 Bean 사용
     */
    @PostMapping("/v1/ad/save")
    public String adSave(Campaign campaign) {


        log.info("request body {}", campaign);

        var data = webClient
                .post()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .host("localhost")
                        .port(8082)
                        .path("/repository/ad/save")
                        .build())
                .bodyValue(campaign)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();


        log.info("path {}, recommend response: {}", "/ad/save", data);

        return "open telemetry hello";
    }



    /**
     * Tracing 전파시 수동으로 Header에 적용
     */
    @GetMapping("/v1/ad/delete")
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
                        .path("/repository/ad/delete")
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




    @GetMapping("/v1/hello/01")
    public String hello() {


        Span currentSpan = tracer.currentSpan();
        String newSpanId = IdGenerator.random().generateSpanId().toString();

        String traceId = currentSpan != null ? currentSpan.context().traceId() : generateNewId();
        String parentSpanId = currentSpan != null ? currentSpan.context().spanId() : null;

        var data = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .queryParam("age", "11")
                        .host("localhost")
                        .port(8081)
                        .path("/repository/ad/recommend")
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


        log.info("recommend response: {}", data);

        return "open telemetry hello";
    }




    private String generateNewId() {
        return Long.toHexString(new Random().nextLong());
    }
}
