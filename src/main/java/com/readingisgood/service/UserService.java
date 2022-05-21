package com.readingisgood.service;

import com.readingisgood.models.request.CreateUserRequest;
import com.readingisgood.models.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse saveUser(CreateUserRequest request);

    List<UserResponse> getUsers();

}
