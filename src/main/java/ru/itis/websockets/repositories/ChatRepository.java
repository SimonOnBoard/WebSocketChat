package ru.itis.websockets.repositories;


import ru.itis.websockets.orm.Chat;

import java.util.Optional;

public interface ChatRepository {
    Optional<Chat> find(String id);
    void save(Chat chat);
    Optional<Chat> findByCode(String code);
}
