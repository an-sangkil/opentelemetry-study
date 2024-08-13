package com.ad.adrepository.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class TracingWebClientFilter implements ExchangeFilterFunction {


    private final Tracer tracer;

    public TracingWebClientFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Span span = tracer.currentSpan();
        if (span != null) {
            ClientRequest newRequest = ClientRequest.from(request)
                    .header("traceparent", span.context().traceId() + "-" + span.context().spanId())
                    .build();
            return next.exchange(newRequest);
        }
        return next.exchange(request);
    }


}
