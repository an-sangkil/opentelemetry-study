package com.kakaohealthcare.adrepository.config;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;


/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 6. 28.
 */

@Component
public class TracingInterceptor implements HandlerInterceptor {


    private final Tracer tracer;

    public TracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        // custom header 에 대한 처리
        String customTraceId = request.getHeader("X-Custom-TraceId");
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
                // 현재 스팬을 요청 속성에 저장하여 나중에 사용할 수 있게 함
                request.setAttribute("currentSpan", span);
            }
        }


        Span span = tracer.spanBuilder(request.getMethod() + " " + request.getRequestURI()).startSpan();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length == 1) {
                span.setAttribute("http.param." + key, values[0]);
            } else {
                span.setAttribute("http.param." + key, String.join(",", values));
            }
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            span.setAttribute("http.request.body", stringBuilder.toString());
        }

        Scope scope = span.makeCurrent();
        request.setAttribute("span", span);
        request.setAttribute("scope", scope);
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        Span currentSpan = (Span) request.getAttribute("currentSpan");
        if (currentSpan != null) {
            currentSpan.end();
        }

        Scope scope = (Scope) request.getAttribute("scope");
        if (scope != null) {
            scope.close();
        }
        Span span = (Span) request.getAttribute("span");
        if (span != null) {
            if (ex != null) {
                span.recordException(ex);
            }
            span.end();
        }
    }

}
