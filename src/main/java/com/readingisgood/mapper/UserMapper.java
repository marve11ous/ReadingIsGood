package com.readingisgood.mapper;

import com.readingisgood.dao.dto.User;
import com.readingisgood.models.request.CreateUserRequest;
import com.readingisgood.models.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User toUser(CreateUserRequest request);

    UserResponse toUserResponse(User user);
}
