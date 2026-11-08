
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceSecurityTest {

    private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new CustomUserDetailsService(userRepository);
    }

    private User createUser(
            String role,
            boolean emailVerified,
            RecruiterApprovalStatus approvalStatus) {

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setRole(role);
        user.setEmailVerified(emailVerified);
        user.setApprovalStatus(approvalStatus);

        return user;
    }

    @Test
    void studentCanAuthenticate() {
        User student = createUser(
                "STUDENT",
                false,
                RecruiterApprovalStatus.PENDING
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(student));

        UserDetails details =
                service.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void adminCanAuthenticate() {
        User admin = createUser(
                "ADMIN",
                false,
                RecruiterApprovalStatus.PENDING
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(admin));

        UserDetails details =
                service.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void verifiedAndApprovedRecruiterCanAuthenticate() {
        User recruiter = createUser(
                "RECRUITER",
                true,
                RecruiterApprovalStatus.APPROVED
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(recruiter));

        UserDetails details =
                service.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_RECRUITER")));
    }

    @Test
    void unverifiedRecruiterCannotAuthenticate() {
        User recruiter = createUser(
                "RECRUITER",
                false,
                RecruiterApprovalStatus.APPROVED
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("test@example.com")
        );
    }

    @Test
    void verifiedButPendingRecruiterCannotAuthenticate() {
        User recruiter = createUser(
                "RECRUITER",
                true,
                RecruiterApprovalStatus.PENDING
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("test@example.com")
        );
    }

    @Test
    void rejectedRecruiterCannotAuthenticate() {
        User recruiter = createUser(
                "RECRUITER",
                true,
                RecruiterApprovalStatus.REJECTED
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("test@example.com")
        );
    }

    @Test
    void nonexistentUserCannotAuthenticate() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@example.com")
        );
    }
}
