package ru.itis.websockets.dto;

import lombok.Builder;
import lombok.Data;
import ru.itis.websockets.orm.User;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class UserDto {
    private Long id;
    private String name;
    private String mail;

    public static UserDto from(User user, String form) {
        switch (form) {
            case "light": {
                return UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .mail(user.getLogin())
                        .build();
            }
            case "full":
                return UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .mail(user.getLogin())
                        .build();
            default:
                return null;
        }
    }

    public static List<UserDto> from(List<User> users) {
        return users.stream()
                .map(user -> from(user, "full"))
                .collect(Collectors.toList());
    }

}
