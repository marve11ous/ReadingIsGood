package com.readingisgood.service.impl;

import com.readingisgood.dao.UserRepository;
import com.readingisgood.mapper.UserMapper;
import com.readingisgood.models.request.CreateUserRequest;
import com.readingisgood.models.response.UserResponse;
import com.readingisgood.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse saveUser(CreateUserRequest request) {
        var response = userMapper.toUserResponse(userRepository.save(userMapper.toUser(request)));
        log.info("New customer {}", response);
        return response;
    }

    @Override
    public List<UserResponse> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
