package ru.socialnet.team43.configuration;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.security.jwt.JwtUtils;
import ru.socialnet.team43.websocket.HandshakeInterceptor;
import ru.socialnet.team43.websocket.WebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final CommunicationClient communicationClient;
    private final ProfileClient profileClient;
    private final JwtUtils jwtUtils;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                        new WebSocketHandler(jwtUtils, profileClient, communicationClient),
                        "/api/v1/streaming/ws")
                .addInterceptors(new HandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
