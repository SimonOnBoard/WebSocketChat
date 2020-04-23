package ru.itis.websockets.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.websockets.dto.TokenDto;
import ru.itis.websockets.dto.SignInDto;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.repositories.UsersRepository;

import java.util.Optional;

@Service
public class SignInServiceImpl implements SignInService {
    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    public SignInServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenDto signIn(SignInDto signInData) throws AccessDeniedException  {
        Optional<User> userOptional = usersRepository.findByLogin(signInData.getEmail());
        // если у меня есть этот пользвователь
        if (userOptional.isPresent()) {
            // получаем его
            User user = userOptional.get();
            // если пароль подходит
            if (passwordEncoder.matches(signInData.getPassword(), user.getPassword())) {
                // создаем токен
                String token = Jwts.builder()
                        .setSubject(user.getId().toString()) // id пользователя
                        .claim("name", user.getName()) // имя
                        .claim("role", user.getRole().name()) // роль
                        .signWith(SignatureAlgorithm.HS256, secret) // подписываем его с нашим secret
                        .compact(); // преобразовали в строку
                return new TokenDto(token);
            } else {
                throw new AccessDeniedException("Wrong email/password");
            }
        } else {
            throw new AccessDeniedException("User not found");
        }
    }
}

