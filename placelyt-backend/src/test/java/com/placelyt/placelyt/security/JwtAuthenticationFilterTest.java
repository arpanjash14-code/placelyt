
package com.placelyt.placelyt.security;

import com.placelyt.placelyt.config.JwtAuthenticationFilter;
import com.placelyt.placelyt.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private static final String SECRET =
            "0123456789abcdef0123456789abcdef";

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);

        userDetailsService = mock(UserDetailsService.class);
        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        filterChain = mock(FilterChain.class);

        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_STUDENT")
                .build();

        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private String generateToken(
            String secret,
            Date expiration
    ) {
        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 1000))
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private MockHttpServletRequest request(String token) {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    @Test
    void expiredTokenReturnsUnauthorized() throws Exception {
        String token = generateToken(
                SECRET,
                new Date(System.currentTimeMillis() - 10000)
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(request(token), response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never())
                .doFilter(any(), any());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void malformedTokenReturnsUnauthorized() throws Exception {
        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request("this.is.not.a.valid.jwt"),
                response,
                filterChain
        );

        assertEquals(401, response.getStatus());
        verify(filterChain, never())
                .doFilter(any(), any());
    }

    @Test
    void invalidSignatureReturnsUnauthorized() throws Exception {
        String wrongSecret =
                "abcdef0123456789abcdef0123456789";

        String token = generateToken(
                wrongSecret,
                new Date(System.currentTimeMillis() + 60000)
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(request(token), response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never())
                .doFilter(any(), any());
    }

    @Test
    void missingBearerTokenContinuesFilterChain() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());

        verifyNoInteractions(userDetailsService);
    }

    @Test
    void validTokenEstablishesAuthentication() throws Exception {
        String token = generateToken(
                SECRET,
                new Date(System.currentTimeMillis() + 60000)
        );

        MockHttpServletRequest request = request(token);

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());

        assertNotNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        assertEquals(
                "testuser",
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        verify(filterChain).doFilter(request, response);
    }
}
