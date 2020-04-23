package ru.itis.websockets.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.itis.websockets.orm.NodeNotification;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MessageResultDto{
    private String text;
    private String ownerName;
    private String time;
    public static MessageResultDto from(NodeNotification nodeNotification){
        MessageResultDto messageResultDto = new MessageResultDto();
        messageResultDto.setText(nodeNotification.getNotificationPayload());
        messageResultDto.setOwnerName(nodeNotification.getOwnerName());
        messageResultDto.setTime(nodeNotification.getTimestamp().toString());
        return messageResultDto;
    }
    
    public static List<MessageResultDto> from(List<NodeNotification> nodeNotificatios){
        return nodeNotificatios.stream().map(MessageResultDto::from).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "MessageResultDto{" +
                "ownerName='" + ownerName + '\'' +
                ", time='" + time + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
