package ru.itis.websockets.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.websockets.content.FormContentLoadable;
import ru.itis.websockets.dto.UserDto;
import ru.itis.websockets.services.ReaderService;
import ru.itis.websockets.services.SignUpService;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Component("registrationHandler")
@EnableWebSocket
public class RegistrationHandler extends TextWebSocketHandler implements FormContentLoadable {
    private ObjectMapper objectMapper;
    private SignUpService signUpService;
    private ReaderService readerService;

    public RegistrationHandler(ObjectMapper objectMapper, SignUpService signUpService, ReaderService readerService) {
        this.objectMapper = objectMapper;
        this.signUpService = signUpService;
        this.readerService = readerService;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String messageText = (String) message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(messageText);
        UserDto userDto = signUpService.signUp(readerService.getSignUpData(jsonNode));
        Map<String, String> result = new HashMap<>();
        if(userDto != null){
            result.put("status","200");
            result.put("socketRedirect", "/login");
            result.put("content",loginForm);
        }
        else{
            result.put("status", "403");
            result.put("message", "Пользователь с таким email уже существует");
        }
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
    }
}
