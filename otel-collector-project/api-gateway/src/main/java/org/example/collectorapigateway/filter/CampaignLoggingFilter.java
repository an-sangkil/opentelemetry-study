package org.example.collectorapigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Order(2)
@Component
public class CampaignLoggingFilter extends AbstractGatewayFilterFactory<CampaignLoggingFilter.Config> implements Ordered {

    private static final Logger logger = Logger.getLogger(CampaignLoggingFilter.class.getName());

    public CampaignLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logger.info("CampaignLoggingFilter: Request to campaign service");
            return chain.filter(exchange);
        };
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public static class Config {
        // Put the configuration properties here
    }
}
