package ru.itis.websockets.handlers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.websockets.dto.SignInDto;
import ru.itis.websockets.dto.TokenDto;
import ru.itis.websockets.services.ReaderService;
import ru.itis.websockets.services.SignInService;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;import java.util.HashMap;
import java.util.Map;

@Component("loginHandler")
@EnableWebSocket
public class LoginHandler extends TextWebSocketHandler {
    private ObjectMapper objectMapper;
    private SignInService signInService;
    private ReaderService readerService;

    public LoginHandler(ObjectMapper objectMapper, SignInService signInService, ReaderService readerService) {
        this.objectMapper = objectMapper;
        this.signInService = signInService;
        this.readerService = readerService;
    }

    @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String messageText = (String) message.getPayload();

        JsonNode jsonNode = null;
        Map<String, String> result = new HashMap<>();
        try {
            jsonNode = objectMapper.readTree(messageText);
            TokenDto tokenDto = signInService.signIn(readerService.getLoginData(jsonNode));
            result.put("status", "200");
            result.put("token", tokenDto.getToken());
            result.put("socketRedirect", "/chats");
        } catch (AccessDeniedException e) {
            result.put("status", "403");
            result.put("message", "Данные не верны");
        } finally {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
         }

    }
}
