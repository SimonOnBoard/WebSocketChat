package ru.itis.websockets.repositories;


import ru.itis.websockets.orm.NodeNotification;

import java.util.List;

public interface MessageHistoryRepository extends CrudRepository<Long, NodeNotification> {
    List<NodeNotification> findAllByChat(String chat);
}
