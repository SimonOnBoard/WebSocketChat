package ru.itis.websockets.services;

import org.springframework.stereotype.Service;
import ru.itis.websockets.dto.ChatDto;
import ru.itis.websockets.dto.JoinChatResponseDto;
import ru.itis.websockets.dto.MessageResultDto;
import ru.itis.websockets.orm.Chat;
import ru.itis.websockets.orm.PageUser;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.repositories.ChatRepository;
import ru.itis.websockets.repositories.MessageHistoryRepository;
import ru.itis.websockets.repositories.PageUserRepository;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl implements ChatService {
    private PageUserRepository pageUserRepository;
    private ChatRepository chatRepository;
    private MessageHistoryRepository messageHistoryRepository;

    public ChatServiceImpl(PageUserRepository pageUserRepository, ChatRepository chatRepository, MessageHistoryRepository messageHistoryRepository) {
        this.pageUserRepository = pageUserRepository;
        this.chatRepository = chatRepository;
        this.messageHistoryRepository = messageHistoryRepository;
    }

    @Override
    public List<ChatDto> findAllByUserId(Long userId) {
        List<PageUser> chats = pageUserRepository.findAllByUserId(userId);
        return ChatDto.fromPageUserList(chats.stream().map(c -> c.getChat()).collect(Collectors.toList()));
    }

    @Override
    public List<MessageResultDto> getPreviousMessages(String id) {
        return MessageResultDto.from(messageHistoryRepository.findAllByChat(id));
    }

    @Override
    public boolean checkIfChatAllowedToUser(String chatId, Long id) {
        Optional<Chat> chatCandidate = chatRepository.find(chatId);
        if (chatCandidate.isPresent()) {
            Optional<PageUser> pageUserCandidate = pageUserRepository.findByUserIdAndChat(chatCandidate.get(), id);
            if (pageUserCandidate.isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfChatAllowedToUser(String chatId, Long id, String pageId) {
        Optional<Chat> chatCandidate = chatRepository.find(chatId);
        if (chatCandidate.isPresent()) {
            Optional<PageUser> pageUserCandidate = pageUserRepository.findByPageIdAndChat(chatCandidate.get(), pageId);
            if (pageUserCandidate.isPresent()) {
                return pageUserCandidate.get().getUserId().longValue() == id;
            }
        }
        return false;
    }

    @Override
    public List<PageUser> getAllPageUsers(String chatId) {
        Optional<Chat> chatCandidate = chatRepository.find(chatId);
        if (chatCandidate.isPresent()) {
            return chatCandidate.get().getUsers();
        } else {
            throw new IllegalStateException("Something went wrong");
        }
    }

    @Override
    public void saveCurrentUserPage(String chatId, String pageId, Long userId) {
        Optional<Chat> chatCandidate = chatRepository.find(chatId);
        if (chatCandidate.isEmpty()) throw new IllegalStateException("SomeThingWentWrong");
        Optional<PageUser> pageUserCandidate = pageUserRepository.findByUserIdAndChat(chatCandidate.get(), userId);
        PageUser pageUser = pageUserCandidate.get();
        pageUser.setPageId(pageId);
        pageUserRepository.update(pageUser);
    }

    @Override
    public ChatDto startChat(String name, User user) {
        Chat chat = Chat.builder().users(new ArrayList<>()).build();
        chat.setId(UUID.randomUUID().toString());
        chat.setCode(chat.hashCode() + UUID.randomUUID().toString());
        chat.setName(name);
        chatRepository.save(chat);
        PageUser pageUser = PageUser.builder().pageId(null).userId(user.getId()).chat(chat).build();
        chat.getUsers().add(pageUser);
        pageUserRepository.save(pageUser);
        return ChatDto.from(chat);
    }

    @Override
    public JoinChatResponseDto addUserPageByCode(String code, Long id) {
        Optional<Chat> chatCandidate = chatRepository.findByCode(code);
        if (chatCandidate.isPresent()) {
            Optional<PageUser> pageUserCandidate;
            if ((pageUserCandidate = pageUserRepository.findByUserIdAndChat(chatCandidate.get(), id)).isPresent()) {
                return JoinChatResponseDto.builder().code(302).build();
            }
            PageUser pageUser = PageUser.builder()
                    .chat(chatCandidate.get())
                    .userId(id)
                    .build();
            pageUserRepository.save(pageUser);
            return JoinChatResponseDto.builder()
                    .code(200)
                    .chatId(chatCandidate.get().getId())
                    .build();
        } else {
            return JoinChatResponseDto.builder().code(404).build();
        }
    }
}
