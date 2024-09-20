package com.ad.adrepository.config;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 6. 28.
 */

@Component
public class TracingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(TracingFilter.class);
    private final Tracer tracer;

    public TracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String customTraceId = exchange.getRequest().getHeaders().getFirst("X-Custom-TraceId");

        // 커스텀 테그가 있는지 확인하고 있으면 해당 커스텀 테그의 스판을 사용.
        Span span;
        if (customTraceId != null && !customTraceId.isEmpty()) {
            SpanContext spanContext = SpanContext.create(
                    customTraceId,
                    Span.current().getSpanContext().getSpanId(),
                    TraceFlags.getSampled(),
                    TraceState.getDefault()
            );
            span = tracer.spanBuilder("customSpan")
                    .setParent(Context.current().with(Span.wrap(spanContext)))
                    .startSpan();
            try (Scope scope = span.makeCurrent()) {
                exchange.getAttributes().put("currentSpan", span);
            }
        } else {
            // 새로운 tracer span 생성
            span = tracer.spanBuilder(exchange.getRequest().getMethod().name() + " " + exchange.getRequest().getURI()).startSpan();
        }


        log.info("filter >> traceId = {}, spanId = {}", span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());


        exchange.getRequest().getQueryParams().forEach((key, values) -> {
            if (values != null && values.size() == 1) {
                span.setAttribute("http.param." + key, values.get(0));
            } else {
                span.setAttribute("http.param." + key, String.join(",", values));
            }
        });

        if ("POST".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
            return exchange.getRequest().getBody().collectList().flatMap(dataBuffers -> {
                String requestBody = dataBuffers.stream()
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            return new String(bytes, StandardCharsets.UTF_8);
                        }).collect(Collectors.joining(""));
                span.setAttribute("http.request.body", requestBody);
                return proceedWithTracing(exchange, chain, span);
            });
        }

        return proceedWithTracing(exchange, chain, span);
    }

    private Mono<Void> proceedWithTracing(ServerWebExchange exchange, WebFilterChain chain, Span span) {
        try (Scope scope = span.makeCurrent()) {

            exchange.getAttributes().put("span", span);
            exchange.getAttributes().put("scope", scope);

            return chain.filter(exchange)
                    .contextWrite(context ->

                    {
                        log.info("TracingFilter: Storing span in context with traceId = {}, spanId = {}", span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());
                        return context.put("currentSpan", span);
                    }) // Reactor Context에 Span을 저장

                    .doFinally(signalType -> {

                        Scope currentScope = (Scope) exchange.getAttribute("scope");
                        log.info("TracingFilter: Cleaning up span with traceId = {}, spanId = {}", span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());

                        if (currentScope != null) {
                            currentScope.close();
                        }
                        Span currentSpan = (Span) exchange.getAttribute("span");
                        if (currentSpan != null) {
                            if (exchange.getResponse().getStatusCode() != null &&
                                    exchange.getResponse().getStatusCode().isError()) {
                                currentSpan.setStatus(StatusCode.ERROR);
                            }
                            currentSpan.end();
                        }
                    });
        }
    }
}
