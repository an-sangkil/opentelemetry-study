package org.example.collectorapigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Order(1)
@Component
public class CampaignAuthFilter extends AbstractGatewayFilterFactory<CampaignAuthFilter.Config> implements Ordered {

    private static final Logger logger = Logger.getLogger(CampaignAuthFilter.class.getName());

    public CampaignAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Simple authentication logic for demonstration
            if (!exchange.getRequest().getHeaders().containsKey("X-Auth-Token")) {
                logger.warning("CampaignAuthFilter: Missing X-Auth-Token header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            logger.info("CampaignAuthFilter: X-Auth-Token header present");
            return chain.filter(exchange);
        };
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public static class Config {
        // Put the configuration properties here
    }
}
