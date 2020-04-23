package ru.itis.websockets.services;


import ru.itis.websockets.dto.ChatDto;
import ru.itis.websockets.dto.JoinChatResponseDto;
import ru.itis.websockets.dto.MessageResultDto;
import ru.itis.websockets.orm.PageUser;
import ru.itis.websockets.orm.User;

import java.util.List;

public interface ChatService {

    List<ChatDto> findAllByUserId(Long userId);

    List<MessageResultDto> getPreviousMessages(String id);

    boolean checkIfChatAllowedToUser(String chatId, Long id);

    boolean checkIfChatAllowedToUser(String chatId, Long id, String pageId);

    List<PageUser> getAllPageUsers(String chatId);

    void saveCurrentUserPage(String chatId, String pageId, Long userId);

    ChatDto startChat(String name, User user);

    JoinChatResponseDto addUserPageByCode(String code, Long id);
}
