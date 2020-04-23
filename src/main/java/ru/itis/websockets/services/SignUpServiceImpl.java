package ru.itis.websockets.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ru.itis.websockets.dto.SignUpDto;
import ru.itis.websockets.dto.UserDto;
import ru.itis.websockets.models.Role;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.repositories.UsersRepository;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SignUpServiceImpl implements SignUpService {
    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    public SignUpServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDto signUp(SignUpDto formData) {

        if (usersRepository.findByLogin(formData.getEmail()).isPresent()) return null;

        User user = User.builder()
                .login(formData.getEmail())
                .name(formData.getNick())
                .password(passwordEncoder.encode(formData.getPassword()))
                .registrationDate(LocalDateTime.now())
                .role(Role.USER)
                .build();
        usersRepository.save(user);
        return UserDto.from(user, "full");
    }

}
