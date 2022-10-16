package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.mapper.UserMapper;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> findAllUsers() {
        return UserMapper.mapToUserDtos(userRepository.findAll());
    }

    public UserDto findUserById(long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    public boolean userExists(long id) {
        return userRepository.existsById(id);
    }
}
