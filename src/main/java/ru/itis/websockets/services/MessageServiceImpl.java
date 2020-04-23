package ru.itis.websockets.services;

import org.springframework.stereotype.Service;
import ru.itis.websockets.dto.MessageDto;
import ru.itis.websockets.dto.MessageResultDto;
import ru.itis.websockets.orm.NodeNotification;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.repositories.MessageHistoryRepository;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    private MessageHistoryRepository messageHistoryRepository;

    public MessageServiceImpl(MessageHistoryRepository messageHistoryRepository) {
        this.messageHistoryRepository = messageHistoryRepository;
    }

    @Override
    public MessageResultDto saveMessage(MessageDto messageDto, User user) {
        final NodeNotification notification = new NodeNotification();
        notification.setNotificationPayload(messageDto.getMessage());
        notification.setChatId(messageDto.getChatId());
        notification.setOwner(user.getId());
        notification.setOwnerName(user.getName());
        notification.setTimestamp(LocalDateTime.now());
        messageHistoryRepository.save(notification);
        return MessageResultDto.from(notification);
    }
}
