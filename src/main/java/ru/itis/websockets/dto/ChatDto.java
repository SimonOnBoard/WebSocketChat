package ru.itis.websockets.dto;

import lombok.Builder;
import lombok.Data;
import ru.itis.websockets.orm.Chat;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ChatDto {
    private String name;
    private String id;
    private String code;
    public static List<ChatDto> fromPageUserList(List<Chat> collect) {
        return collect.stream()
                .map(ChatDto::from)
                .collect(Collectors.toList());
    }

    public static ChatDto from(Chat s) {
        return ChatDto.builder()
                .code(s.getCode())
                .name(s.getName())
                .id(s.getId())
                .build();
    }
}
