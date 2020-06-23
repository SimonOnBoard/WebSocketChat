package ru.itis.websockets.handlers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.websockets.content.FormContentLoadable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("startHandler")
@EnableWebSocket
public class StartHandler extends TextWebSocketHandler implements FormContentLoadable {

    private ObjectMapper objectMapper;

    public StartHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        boolean ch = session.getAttributes().get("user") != null;
        if(ch){
            session.close(CloseStatus.NOT_ACCEPTABLE);
            System.out.println("logged");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println(message);
        String messageText = (String) message.getPayload();
        System.out.println("started");
        JsonNode jsonNode = objectMapper.readTree(messageText);
        String status = jsonNode.get("status").asText();
        Map<String, String> result = new HashMap<>();
        switch (status) {
            case "login":
                result.put("status", "200");
                result.put("content", loginForm);
                sendSuccessMessage(result, session);
                break;
            case "registration":
                result.put("status", "200");
                result.put("content", registrationForm);
                sendSuccessMessage(result, session);
                break;
            case "check":
                System.out.println("All is ok)");
                break;
        }
    }

    private void sendSuccessMessage(Map<String, String> result, WebSocketSession session) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
