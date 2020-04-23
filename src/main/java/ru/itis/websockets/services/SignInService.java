package ru.itis.websockets.services;

import ru.itis.websockets.dto.TokenDto;
import ru.itis.websockets.dto.SignInDto;
import org.springframework.security.access.AccessDeniedException;
public interface SignInService {
    TokenDto signIn(SignInDto build) throws AccessDeniedException;
}
