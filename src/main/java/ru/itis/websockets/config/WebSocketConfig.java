package ru.itis.websockets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.itis.websockets.handlers.*;

import java.util.Map;
import java.util.Objects;


@Configuration
@EnableWebSocket
@Import({ApplicationContextConfig.class})
public class WebSocketConfig implements WebSocketConfigurer {
    private StartHandler startHandler;
    private LoginHandler loginHandler;
    private RegistrationHandler registrationHandler;
    private AuthHandshakeHandler authHandshakeHandler;
    private ChatHandler chatHandler;

    public WebSocketConfig(StartHandler startHandler, LoginHandler loginHandler, RegistrationHandler registrationHandler, AuthHandshakeHandler authHandshakeHandler, ChatHandler chatHandler) {
        this.startHandler = startHandler;
        this.loginHandler = loginHandler;
        this.registrationHandler = registrationHandler;
        this.authHandshakeHandler = authHandshakeHandler;
        this.chatHandler = chatHandler;
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(startHandler,"/rootMenu")
                .addHandler(loginHandler,"/login")
                .addHandler(registrationHandler,"/registration")
                .addHandler(chatHandler,"/chat")
                .setHandshakeHandler(authHandshakeHandler)
                .withSockJS();
    }
}
