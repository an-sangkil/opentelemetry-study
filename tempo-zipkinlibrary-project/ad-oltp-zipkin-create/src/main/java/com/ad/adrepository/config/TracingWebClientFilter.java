package com.ad.adrepository.config;

import org.springframework.http.HttpHeaders;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class TracingWebClientFilter implements ExchangeFilterFunction {


    private final ContextPropagators propagators;
    private final Tracer tracer;

    public TracingWebClientFilter(ContextPropagators propagators, Tracer tracer) {
        this.propagators = propagators;
        this.tracer = tracer;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        /*Span span = tracer.currentSpan();
        if (span != null) {
            ClientRequest newRequest = ClientRequest.from(request)
                    .header("traceparent", span.context().traceId() + "-" + span.context().spanId())
                    .build();
            return next.exchange(newRequest);
        }
        return next.exchange(request);
        */

        // 현재 컨텍스트에서 스팬을 가져옵니다.
        Context context = Context.current();

        // 컨텍스트를 사용하여 헤더에 trace 정보를 주입
        ClientRequest newRequest = ClientRequest.from(request)
                .headers(headers -> propagators.getTextMapPropagator().inject(context, headers, HttpHeaders::add))
                .build();
        return next.exchange(newRequest);


    }


}
