package ru.itis.websockets.orm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeNotification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "CHAT_ID")
    private String chatId;

    @Column(name = "OWNER")
    private Long owner;

    @Column(name = "OWNER_NAME")
    private String ownerName;

    @Column(name = "PAYLOAD")
    private String notificationPayload;

}
