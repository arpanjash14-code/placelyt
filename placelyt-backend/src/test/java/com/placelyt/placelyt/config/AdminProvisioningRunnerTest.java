
package com.placelyt.placelyt.config;

import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProvisioningRunnerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    private AdminProvisioningRunner runner;

    @BeforeEach
    void setUp() {
        runner = new AdminProvisioningRunner(
                userRepository,
                passwordEncoder
        );
    }

    private void enableProvisioning(
            String email,
            String password
    ) {
        ReflectionTestUtils.setField(
                runner, "provisioningEnabled", true
        );
        ReflectionTestUtils.setField(
                runner, "adminEmail", email
        );
        ReflectionTestUtils.setField(
                runner, "adminPassword", password
        );
    }

    @Test
    void shouldDoNothingWhenProvisioningIsDisabled() throws Exception {

        ReflectionTestUtils.setField(
                runner, "provisioningEnabled", false
        );

        runner.run(applicationArguments);

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void shouldRejectMissingCredentials() {

        enableProvisioning("", "");

        assertThrows(
                IllegalStateException.class,
                () -> runner.run(applicationArguments)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void shouldSkipWhenAdminAlreadyExists() throws Exception {

        enableProvisioning(
                "admin@placelyt.com",
                "StrongPassword123!"
        );

        when(userRepository.existsByRole("ADMIN"))
                .thenReturn(true);

        runner.run(applicationArguments);

        verify(userRepository).existsByRole("ADMIN");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectEmailAlreadyUsedByAnotherAccount() {

        enableProvisioning(
                "student@placelyt.com",
                "StrongPassword123!"
        );

        when(userRepository.existsByRole("ADMIN"))
                .thenReturn(false);

        when(userRepository.findByEmail("student@placelyt.com"))
                .thenReturn(java.util.Optional.of(new User()));

        assertThrows(
                IllegalStateException.class,
                () -> runner.run(applicationArguments)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectPasswordShorterThanTwelveCharacters() {

        enableProvisioning(
                "admin@placelyt.com",
                "Short123!"
        );

        when(userRepository.existsByRole("ADMIN"))
                .thenReturn(false);

        when(userRepository.findByEmail("admin@placelyt.com"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> runner.run(applicationArguments)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldCreateAdminWithEncodedPassword() throws Exception {

        enableProvisioning(
                " ADMIN@Placelyt.com ",
                "StrongPassword123!"
        );

        when(userRepository.existsByRole("ADMIN"))
                .thenReturn(false);

        when(userRepository.findByEmail("admin@placelyt.com"))
                .thenReturn(java.util.Optional.empty());

        when(passwordEncoder.encode("StrongPassword123!"))
                .thenReturn("bcrypt-encoded-password");

        runner.run(applicationArguments);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedAdmin = captor.getValue();

        assertEquals("ADMIN", savedAdmin.getRole());
        assertEquals("admin@placelyt.com", savedAdmin.getEmail());
        assertEquals("bcrypt-encoded-password", savedAdmin.getPassword());
        assertEquals("Placelyt Administrator", savedAdmin.getName());

        verify(passwordEncoder).encode("StrongPassword123!");
    }
}
