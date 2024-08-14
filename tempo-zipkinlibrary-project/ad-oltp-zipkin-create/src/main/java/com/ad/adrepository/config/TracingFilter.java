package com.ad.adrepository.config;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
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

    private final Tracer tracer;

    public TracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String customTraceId = exchange.getRequest().getHeaders().getFirst("X-Custom-TraceId");

        // 커스텀 테그가 있는지 확인하고 있으면 해당 커스텀 테그의 스판을 사용.
        if (customTraceId != null && !customTraceId.isEmpty()) {
            SpanContext spanContext = SpanContext.create(
                    customTraceId,
                    Span.current().getSpanContext().getSpanId(),
                    TraceFlags.getSampled(),
                    TraceState.getDefault()
            );
            Span span = tracer.spanBuilder("customSpan")
                    .setParent(Context.current().with(Span.wrap(spanContext)))
                    .startSpan();
            try (Scope scope = span.makeCurrent()) {
                exchange.getAttributes().put("currentSpan", span);
            }
        }

        // 새로운 tracer span 생성
        Span span = tracer.spanBuilder(exchange.getRequest().getMethod().name() + " " + exchange.getRequest().getURI()).startSpan();
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
            return chain.filter(exchange).doFinally(signalType -> {
                Scope currentScope = (Scope) exchange.getAttribute("scope");
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
