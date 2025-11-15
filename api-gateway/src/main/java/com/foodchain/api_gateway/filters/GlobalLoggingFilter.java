package com.foodchain.api_gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log de la petición entrante
        log.info(">>> INCOMING REQUEST <<<");
        log.info("Path: {}", exchange.getRequest().getPath());
        log.info("Method: {}", exchange.getRequest().getMethod());
        log.info("Headers: {}", exchange.getRequest().getHeaders());

        // Dejamos que la cadena de filtros continúe
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Log de la respuesta saliente
            log.info(">>> OUTGOING RESPONSE <<<");
            log.info("Status Code: {}", exchange.getResponse().getStatusCode());
            log.info("Headers: {}", exchange.getResponse().getHeaders());
        }));
    }

    @Override
    public int getOrder() {
        // Le damos una prioridad muy alta para que sea el primer filtro en ejecutarse.
        return -1;
    }
}