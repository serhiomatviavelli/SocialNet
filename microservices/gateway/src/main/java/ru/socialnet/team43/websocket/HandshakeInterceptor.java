package ru.socialnet.team43.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes)
            throws Exception {

        if (request.getHeaders().get("cookie") != null) {
            String jwt = request.getHeaders().get("cookie").get(0);
            jwt = jwt.replaceFirst("jwt=", "");
            attributes.put("token", jwt);
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
