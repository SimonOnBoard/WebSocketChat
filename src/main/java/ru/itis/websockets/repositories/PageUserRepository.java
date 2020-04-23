package ru.itis.websockets.repositories;

import ru.itis.websockets.orm.Chat;
import ru.itis.websockets.orm.PageUser;

import java.util.List;
import java.util.Optional;

public interface PageUserRepository extends CrudRepository<Long, PageUser> {
    Optional<PageUser> findByUUID(String pageId);
    Optional<PageUser> findByUserId(Long id);

    Optional<PageUser> findByUserIdAndChat(Chat chatId, Long userId);

    List<PageUser> findAllByUserId(Long userId);

    Optional<PageUser> findByPageIdAndChat(Chat chat, String id);
}
