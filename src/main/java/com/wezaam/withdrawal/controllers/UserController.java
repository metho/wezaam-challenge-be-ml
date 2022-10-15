package com.wezaam.withdrawal.controllers;

import com.wezaam.withdrawal.dto.UserDto;
import com.wezaam.withdrawal.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> findAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDto findById(@PathVariable long id) {
        return userService.findUserById(id);
    }
}
