package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.UserNotFoundException;
import com.wezaam.withdrawal.mappers.UserMapper;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> findAllUsers() {
        return UserMapper.mapToUserResponses(userRepository.findAll());
    }

    public UserResponse findUserById(Long id) {
        return UserMapper.mapToUserResponse(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }
}
