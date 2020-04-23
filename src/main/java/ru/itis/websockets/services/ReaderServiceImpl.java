package ru.itis.websockets.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import ru.itis.websockets.dto.MessageDto;
import ru.itis.websockets.dto.SignInDto;
import ru.itis.websockets.dto.SignUpDto;

@Service
public class ReaderServiceImpl implements ReaderService {
    @Override
    public SignInDto getLoginData(JsonNode message) {
        return SignInDto.builder()
                .email(message.get("login").asText())
                .password(message.get("password").asText())
                .build();
    }

    @Override
    public SignUpDto getSignUpData(JsonNode message) {
        return SignUpDto.builder()
                .email(message.get("email").asText())
                .password(message.get("password").asText())
                .nick(message.get("nick").asText())
                .build();
    }

    @Override
    public MessageDto readMessage(JsonNode jsonNode) {
        return MessageDto.builder()
                .message(jsonNode.get("message").asText())
                .pageId(jsonNode.get("pageId").asText())
                .chatId(jsonNode.get("chatId").asText())
                .build();
    }
}
