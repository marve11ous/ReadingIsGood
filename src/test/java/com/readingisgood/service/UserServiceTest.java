package com.readingisgood.service;

import com.readingisgood.dao.UserRepository;
import com.readingisgood.dao.dto.User;
import com.readingisgood.mapper.UserMapper;
import com.readingisgood.models.request.CreateUserRequest;
import com.readingisgood.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Spy
    @SuppressWarnings("unused")
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void saveUser() {
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(any(User.class));
        var request = new CreateUserRequest("Jon", "Snow", LocalDate.EPOCH, "snow@test.com");
        var response = userService.saveUser(request);
        assertEquals(response.getName(), request.getName());
        assertEquals(response.getLastName(), request.getLastName());
        assertEquals(response.getBirthDate(), request.getBirthDate());
        assertEquals(response.getEmail(), request.getEmail());
    }

    @Test
    void getUsers() {
        var user = new User();
        user.setId(1L);
        user.setName("Jon");
        user.setLastName("Snow");
        user.setBirthDate(LocalDate.EPOCH);
        user.setEmail("snow@test.com");
        doReturn(List.of(user)).when(userRepository).findAll();
        var userResponses = userService.getUsers();
        assertEquals(1, userResponses.size());
        var userResponse = userResponses.get(0);
        assertEquals(userResponse.getId(), user.getId());
        assertEquals(userResponse.getName(), user.getName());
        assertEquals(userResponse.getLastName(), user.getLastName());
        assertEquals(userResponse.getBirthDate(), user.getBirthDate());
        assertEquals(userResponse.getEmail(), user.getEmail());
    }

}