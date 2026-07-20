package com.example.st2i.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                try {
                    String token = auth.substring(7);
                    var jwt = jwtDecoder.decode(token);
                    accessor.setUser(new UserPrincipal(jwt.getSubject()));
                    org.slf4j.LoggerFactory.getLogger(getClass())
                            .info("[WS] CONNECT authentifié pour sub={}", jwt.getSubject());
                } catch (Exception e) {
                    org.slf4j.LoggerFactory.getLogger(getClass())
                            .error("[WS] token rejeté au CONNECT: {}", e.getMessage());
                }
            } else {
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .warn("[WS] CONNECT sans header Authorization");
            }
        }
        return message;
    }
}
