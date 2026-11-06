
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticatedUserServiceTest {

    private UserRepository userRepository;
    private AuthenticatedUserService service;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);

        service = new AuthenticatedUserService(
                userRepository
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolvesCurrentUserFromAuthenticatedEmail() {

        User user = user(
                42L,
                "student@example.com"
        );

        authenticate("student@example.com");

        when(userRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(user));

        assertSame(user, service.getCurrentUser());

        assertEquals(
                42L,
                service.getCurrentUserId()
        );
    }

    @Test
    void deniesWhenNoAuthenticationExists() {

        SecurityContextHolder.clearContext();

        assertThrows(
                AccessDeniedException.class,
                service::getCurrentUser
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void allowsMatchingUserId() {

        authenticate("student@example.com");

        when(userRepository.findByEmail("student@example.com"))
                .thenReturn(
                        Optional.of(
                                user(42L, "student@example.com")
                        )
                );

        assertDoesNotThrow(
                () -> service.requireCurrentUser(42L)
        );
    }

    @Test
    void deniesDifferentUserId() {

        authenticate("student@example.com");

        when(userRepository.findByEmail("student@example.com"))
                .thenReturn(
                        Optional.of(
                                user(42L, "student@example.com")
                        )
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.requireCurrentUser(99L)
        );
    }

    
private void authenticate(String email) {

    SecurityContextHolder.getContext()
            .setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            java.util.Collections.emptyList()
                    )
            );
}


    private User user(Long id, String email) {

        User user = new User();

        user.setId(id);
        user.setEmail(email);
        user.setRole("STUDENT");

        return user;
    }
}
