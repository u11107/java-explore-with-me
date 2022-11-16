package ru.practicum.explore.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.dto.UserShortDto;
import ru.practicum.explore.user.model.User;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
