
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the currently authenticated user
     * using the email stored in the JWT principal.
     */
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            throw new AccessDeniedException(
                    "Authenticated user identity is unavailable"
            );
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Authenticated user account was not found"
                        )
                );
    }

    /**
     * Returns the ID of the currently authenticated user.
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Checks whether the supplied ID belongs
     * to the currently authenticated user.
     */
    public boolean isCurrentUser(Long userId) {

        return userId != null
                && getCurrentUserId().equals(userId);
    }

    /**
     * Rejects requests attempting to access
     * another user's personal resources.
     */
    public void requireCurrentUser(Long userId) {

        if (!isCurrentUser(userId)) {

            throw new AccessDeniedException(
                    "You are not allowed to access another user's data"
            );
        }
    }
}
