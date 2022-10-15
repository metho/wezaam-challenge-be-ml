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
        return UserMapper.mapToUserResponses(userRepository.findAll());
    }

    public UserDto findUserById(Long id) {
        return UserMapper.mapToUserResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }
}
