package com.ad.server.adcreatezipkin;


import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Random;

@Slf4j
@RestController
public class AdZipkinController {

    @Autowired
    private Tracer tracer;

    @Autowired
    private RestClient restClient;

    @GetMapping("/ad/create/zipkin/hello")
    public String hello() {

        log.info("create zipkin");

        RestClient restClient = RestClient.create();
        Span currentSpan = tracer.currentSpan();
        String newSpanId = IdGenerator.random().generateSpanId().toString();

        String traceId = currentSpan != null ? currentSpan.context().traceId() : generateNewId();
        String parentSpanId = currentSpan != null ? currentSpan.context().spanId() : null;

        var adServerResult = restClient.get()
                .uri("http://localhost:9000/ad/server/zipkin/hello")
                .header("X-B3-TraceId", traceId)
                .header("X-B3-SpanId", newSpanId)
                .header("X-B3-ParentSpanId", parentSpanId)
                .header("X-B3-Sampled", "1")
                .retrieve()
                .body(String.class);

        log.debug("data = {}", adServerResult);



        log.debug("data = {}", adServerResult);

        return "Hello World zipkin";
    }

    @GetMapping("/ad/create/zipkin/hello/2")
    public String hello2() {

        log.info("/ad/create/zipkin/hello/2, hello2 ");

        var adServerResult = restClient.get()
                .uri("http://localhost:9000/ad/server/zipkin/hello")
                .retrieve()
                .body(String.class);


        log.debug("data = {}", adServerResult);

        return "Hello World zipkin";
    }


    private String generateNewId() {
        return Long.toHexString(new Random().nextLong());
    }
}
