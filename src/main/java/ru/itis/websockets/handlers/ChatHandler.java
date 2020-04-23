package ru.itis.websockets.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.websockets.dto.ChatDto;
import ru.itis.websockets.dto.JoinChatResponseDto;
import ru.itis.websockets.dto.MessageDto;
import ru.itis.websockets.dto.MessageResultDto;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.services.ChatService;
import ru.itis.websockets.services.MessageService;
import ru.itis.websockets.services.ReaderService;

import java.io.IOException;
import java.util.*;

@Component("chatHandler")
@EnableWebSocket
public class ChatHandler extends TextWebSocketHandler {

    private static final Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    private static final Map<String, List<String>> availablePages = new HashMap<>();

    private ObjectMapper objectMapper;
    private ChatService chatService;
    private ReaderService readerService;
    private MessageService messageService;

    public ChatHandler(ObjectMapper objectMapper, ChatService chatService, ReaderService readerService, MessageService messageService) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.readerService = readerService;
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> response = new HashMap<>();
        writeChatsForUser(session, response);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String messageText = (String) message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(messageText);
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttributes().get("user");
        String chatId;
        int code = 500;
        switch (jsonNode.get("option").asText()) {
            case "getChats":
                code = writeChatsForUser(session, response);
                if (jsonNode.get("pageId") != null) {
                    clearChatDeliveringInfo(jsonNode, session);
                }
                break;
            case "chatPage":
                chatId = jsonNode.get("chatId").asText();
                if (chatService.checkIfChatAllowedToUser(chatId, user.getId())) {
                    getChatPageWithMessages(session, chatId, response, user.getId());
                    code = 200;
                }
                break;
            case "sendMessage":
                MessageDto messageDto = readerService.readMessage(jsonNode);
                code = checkAndSendMessage(messageDto, response, session, user);
                break;
            case "startChat":
                String name = jsonNode.get("name").asText();
                code = startNewChat(session, user, name, response);
                break;
            case "joinChat":
                String hash = jsonNode.get("code").asText();
                code = joinChat(session, hash, user, response);
                break;
            case "logout":
                clearChatDeliveringInfo(jsonNode, session);
                break;
            default:
                response.put("info", "Option not found");
                sendMessage(session, response);
                code = 200;
        }
        if (code == 500) {
            closeWebSocket(session, response);
        }
    }

    private void clearChatDeliveringInfo(JsonNode node, WebSocketSession session) {
        System.out.println(node);
        String pageId = node.get("pageId").asText();
        if (pageId != null) {
            String chatId = node.get("chatId").asText();
            sessions.get(chatId).remove(session);
            availablePages.get(chatId).remove(pageId);
            System.out.println();
        }
    }

    private int checkAndSendMessage(MessageDto messageDto, Map<String, Object> response, WebSocketSession session, User user) throws IOException {
        if (sessions.get(messageDto.getChatId()) != null) {
            if (availablePages.get(messageDto.getChatId()).contains(messageDto.getPageId())) {
                trySendMessage(messageDto, response, session, user);
                return 200;
            }
        }
        return 500;
    }

    private int joinChat(WebSocketSession session, String code, User user, Map<String, Object> response) throws IOException {
        JoinChatResponseDto responseDto = chatService.addUserPageByCode(code, user.getId());
        switch (responseDto.getCode()) {
            case 404:
                response.put("info", "Chat not found");
                break;
            case 302:
                response.put("info", "You are already joined");
                break;
            case 200:
                getChatPageWithMessages(session, responseDto.getChatId(), response, user.getId());
                return 200;
        }
        sendMessage(session, response);
        return 200;
    }

    private int startNewChat(WebSocketSession session, User user, String name, Map<String, Object> response) throws IOException {
        ChatDto chat = chatService.startChat(name, user);
        if (chat == null) return 500;
        response.put("chat", chat);
        response.put("message", "Запомните этот код, он является кодом для добавления в чат, отошлите его всем, кого хотите видеть в этом чате");
        sendMessage(session, response);
        return 200;
    }


    private void trySendMessage(MessageDto messageDto, Map<String, Object> response, WebSocketSession session, User user) throws IOException {
        MessageResultDto messageResultDto = messageService.saveMessage(messageDto, user);
        response.put("message", messageResultDto);
        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(response));
        for (WebSocketSession socketSession : sessions.get(messageDto.getChatId())) {
            socketSession.sendMessage(textMessage);
        }
    }


    private void closeWebSocket(WebSocketSession session, Map<String, Object> response) throws IOException {
        response.put("message", "not allowed");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        session.close();
    }


    private void getChatPageWithMessages(WebSocketSession session, String chatId, Map<String, Object> response, Long userId) throws IOException {
        List<MessageResultDto> messages = chatService.getPreviousMessages(chatId);
        String pageId = UUID.randomUUID().toString();
        loadAvailablePage(chatId, pageId);
        loadWebSocketSession(chatId, session);
        response.put("pageId", pageId);
        response.put("chatId", chatId);
        response.put("messages", messages);
        response.put("showMessageForm", true);
        sendMessage(session, response);
    }

    private void loadWebSocketSession(String chatId, WebSocketSession session) {
        List<WebSocketSession> currentChatSessions;
        if ((currentChatSessions = sessions.get(chatId)) != null) {
            currentChatSessions.add(session);
        } else {
            currentChatSessions = new ArrayList<>();
            currentChatSessions.add(session);
            sessions.put(chatId, currentChatSessions);
        }
    }

    private void loadAvailablePage(String chatId, String pageId) {
        List<String> pages;
        if ((pages = availablePages.get(chatId)) != null) {
            pages.add(pageId);
        } else {
            pages = new ArrayList<>();
            pages.add(pageId);
            availablePages.put(chatId, pages);
        }
    }

    private int writeChatsForUser(WebSocketSession session, Map<String, Object> response) throws IOException {
        User user = (User) session.getAttributes().get("user");
        List<ChatDto> chatDtoList = chatService.findAllByUserId(user.getId());
        response.put("status", "200");
        response.put("chats", chatDtoList);
        response.put("showMessageForm", false);
        sendMessage(session, response);
        return 200;
    }

    private void sendMessage(WebSocketSession session, Map<String, Object> response) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
}
