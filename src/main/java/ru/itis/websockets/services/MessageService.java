package ru.itis.websockets.services;

import ru.itis.websockets.dto.MessageDto;
import ru.itis.websockets.dto.MessageResultDto;
import ru.itis.websockets.orm.User;

public interface MessageService {

    MessageResultDto saveMessage(MessageDto message, User user);
}
