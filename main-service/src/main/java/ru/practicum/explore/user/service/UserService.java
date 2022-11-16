package ru.practicum.explore.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.exception.ObjectNotFoundException;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.mapper.UserMapper;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAllByIdOrderByIdDesc(ids, pageable)
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public User getUserById(long userId) throws ObjectNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User with id = " + userId + " not found"));
    }

    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    public boolean checkUserId(long userId) {
        return userRepository.existsById(userId);
    }
}
