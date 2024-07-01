package com.ad.server.adcreatezipkin.controller;


import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * Header 에 수동으로 x-b3 규격으로 traceId 와 span , parentSpan Id 를 넣어서 사용하는 예제
     *
     */
    @GetMapping("/v1/ad/campaign/create")
    public String hello(
            @RequestParam(name = "adId" , required = false)  String adId,
        @RequestParam (value = "campaignName", required = false) String campaignName
    ) {

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

        return "Hello World zipkin";
    }


    /**
     * Bean inject 받은 restClient
     *
     * create -> repository
     *
     */
    @PostMapping("/v2/ad/campaign/create")
    public String hello3(
            @RequestParam (value = "campaignName", required = false) String campaignName
    ) {

        log.info("/v2/ad/campaign/create, save ");


        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("campaignName", campaignName);

        //"http://localhost:9000/ad/serving/campaign"
        var adServerResult = restClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(9001)
                                .path("/ad/repository/campaign/save")
                                .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(parts)
                .retrieve()
                .body(String.class);


        log.debug("data = {}", adServerResult);

        return "Hello World zipkin";
    }


    /**
     * Bean inject 받은 restClient
     *
     * create -> serving
     *
     * @return
     */
    @GetMapping("/v2/ad/campaign/serving")
    public String hello2(
            @RequestParam(name = "adId" , required = false)  String adId
    ) {

        log.info("/v2/ad/campaing/create, hello2 ");

        //"http://localhost:9000/ad/serving/campaign"
        var adServerResult = restClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(9000)
                                .path("ad/serving/campaign")
                                .queryParam("adId",adId)
                                .build())
                .retrieve()
                .body(String.class);


        log.debug("data = {}", adServerResult);

        return "Hello World zipkin";
    }




    private String generateNewId() {
        return Long.toHexString(new Random().nextLong());
    }
}
