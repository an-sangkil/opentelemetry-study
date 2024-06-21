package com.ad.server.adcreatezipkin;


import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

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

        var adServerResult = restClient.get()
                .uri("http://localhost:9000/ad/server/zipkin/hello")
                .header("traceparent", currentSpan != null ? currentSpan.context().traceId() : null)
                .header("X-B3-TraceId", currentSpan != null ? currentSpan.context().traceId() : null)
                .header("X-B3-SpanId", newSpanId) // 새로운 SpanId 사용
                .header("X-B3-ParentSpanId", currentSpan != null ? currentSpan.context().spanId() : null) // 부모 SpanId 추가
                .retrieve()
                .body(String.class);



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
}
