package com.jono.websocket.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

public class CustomUserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(final ServerHttpRequest request,
                                   final ServerHttpResponse response,
                                   final WebSocketHandler wsHandler,
                                   final Map<String, Object> attributes) {
        final var anonId = UUID.randomUUID().toString();
        attributes.put("user", new StompPrincipal(anonId));
        return true;
    }

    @Override
    public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
                               final WebSocketHandler wsHandler, @Nullable final Exception exception) {
    }

}
