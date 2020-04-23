package ru.itis.websockets.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.itis.websockets.orm.NodeNotification;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageHistoryRepositoryJdbcImpl implements MessageHistoryRepository {
    private JdbcTemplate jdbcTemplate;

    public MessageHistoryRepositoryJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<NodeNotification> chatRowMapper = (row, rowNumber) ->
            NodeNotification.builder()
                    .notificationPayload(row.getString("payload"))
                    .id(row.getLong("id"))
                    .chatId(row.getString("chat_id"))
                    .owner(row.getLong("owner"))
                    .ownerName(row.getString("owner_name"))
                    .timestamp(row.getObject("timestamp", LocalDateTime.class))
                    .build();
    private static final String SQL_SELECT_BY_CHAT_ID =
            "SELECT * FROM messages_history where chat_id = ? order by timestamp asc ";
    @Override
    public List<NodeNotification> findAllByChat(String chat) {
        return jdbcTemplate.query(SQL_SELECT_BY_CHAT_ID,new Object[]{chat}, chatRowMapper);
    }

    @Override
    public Optional<NodeNotification> find(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<NodeNotification> findAll() {
        return null;
    }
    //language=sql
    private static final String SQL_INSERT =
            "INSERT INTO messages_history (chat_id, payload, owner, timestamp, owner_name)" +
                    " values (?,?,?,?,?)";
    @Override
    public void save(NodeNotification entity) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            statement.setString(1, entity.getChatId());
            statement.setString(2, entity.getNotificationPayload());
            statement.setLong(3,entity.getOwner());
            statement.setObject(4,entity.getTimestamp());
            statement.setString(5,entity.getOwnerName());
            return statement;
        });
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void update(NodeNotification entity) {

    }
}
