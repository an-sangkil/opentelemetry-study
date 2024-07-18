//package com.ad.adrepository.config;
//
//import io.micrometer.observation.Observation;
//import io.micrometer.observation.ObservationRegistry;
//import org.springframework.web.reactive.function.client.ClientRequest;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.ExchangeFunction;
//import reactor.core.publisher.Mono;
//
//public class TracingWebClientFilter implements ExchangeFilterFunction {
//
//
//    private final ObservationRegistry registry;
//
//    public TracingWebClientFilter(ObservationRegistry registry) {
//        this.registry = registry;
//    }
//
//    @Override
//    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
//        return Mono.deferContextual(contextView -> {
//            Observation observation = Observation.createNotStarted("webclient.request", registry);
//            return Mono.fromCallable(() -> {
//                        observation.start();
//                        return observation;
//                    }).flatMap(obs -> next.exchange(request)
//                            .doFinally(signalType -> obs.stop()))
//                    .contextWrite(context -> context.put(Observation.class, observation));
//        });
//    }
//
//
//}
