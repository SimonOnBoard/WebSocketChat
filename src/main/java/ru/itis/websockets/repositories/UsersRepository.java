package ru.itis.websockets.repositories;

import ru.itis.websockets.orm.User;

import java.util.Optional;

public interface UsersRepository {
    void save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    Optional<User> findByCode(String key);
    void update(User user);
}
