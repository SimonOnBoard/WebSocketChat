package ru.itis.websockets.services;

import org.springframework.util.MultiValueMap;
import ru.itis.websockets.dto.SignUpDto;
import ru.itis.websockets.dto.UserDto;

public interface SignUpService {
    UserDto signUp(SignUpDto formData);
}
