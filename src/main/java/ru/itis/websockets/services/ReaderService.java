package ru.itis.websockets.services;

import com.fasterxml.jackson.databind.JsonNode;
import ru.itis.websockets.dto.MessageDto;
import ru.itis.websockets.dto.SignInDto;
import ru.itis.websockets.dto.SignUpDto;

public interface ReaderService {
    SignInDto getLoginData(JsonNode message);
    SignUpDto getSignUpData(JsonNode message);

    MessageDto readMessage(JsonNode jsonNode);
}
