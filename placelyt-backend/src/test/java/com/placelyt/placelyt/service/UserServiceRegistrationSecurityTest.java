package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.UserRequest;
import com.placelyt.placelyt.dto.UserResponse;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceRegistrationSecurityTest {

    @Test
    void publicRegistrationAlwaysCreatesStudentRole() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repository, encoder);

        UserRequest request = new UserRequest();
        request.setName("Test Student");
        request.setEmail("student@example.com");
        request.setPassword("StrongPassword123!");

        when(repository.findByEmail("student@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("StrongPassword123!")).thenReturn("encoded-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = service.createUser(request);

        assertEquals("STUDENT", response.getRole());
    }
}
