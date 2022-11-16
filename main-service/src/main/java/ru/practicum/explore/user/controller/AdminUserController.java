package ru.practicum.explore.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam List<Long> ids, @RequestParam(defaultValue = FROM) int from,
                                     @RequestParam(defaultValue = SIZE) int size) {
        log.info("Get users");
        return userService.getAllUsers(ids, from, size);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Deleting user {}", userId);
        userService.deleteUserById(userId);
    }
}
