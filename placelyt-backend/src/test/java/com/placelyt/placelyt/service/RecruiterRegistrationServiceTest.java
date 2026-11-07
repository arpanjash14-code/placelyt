
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.RecruiterRegistrationRequest;
import com.placelyt.placelyt.dto.UserResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.CompanyEmailDomain;
import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.DuplicateEmailException;
import com.placelyt.placelyt.exception.InvalidCompanyEmailDomainException;
import com.placelyt.placelyt.exception.UserNotFoundException;
import com.placelyt.placelyt.repository.CompanyEmailDomainRepository;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecruiterRegistrationServiceTest {

    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    private CompanyEmailDomainRepository domainRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    private RecruiterRegistrationService registrationService;

    private Company company;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        companyRepository = mock(CompanyRepository.class);
        domainRepository = mock(CompanyEmailDomainRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        emailService = mock(EmailService.class);

        registrationService = new RecruiterRegistrationService(
                userRepository,
                companyRepository,
                domainRepository,
                passwordEncoder,
                emailService
        );

        company = new Company();
        company.setId(10L);
        company.setName("Test Technologies");
    }

    private RecruiterRegistrationRequest validRequest() {
        RecruiterRegistrationRequest request =
                new RecruiterRegistrationRequest();

        request.setName("Test Recruiter");
        request.setEmail("recruiter@testcompany.com");
        request.setPassword("StrongPassword123!");
        request.setCompanyId(10L);

        return request;
    }

    private void mockValidRegistration() {
        when(userRepository.findByEmail(
                "recruiter@testcompany.com"
        )).thenReturn(Optional.empty());

        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(company));

        when(domainRepository
                .findByDomainAndCompanyIdAndActiveTrue(
                        "testcompany.com",
                        10L
                ))
                .thenReturn(Optional.of(new CompanyEmailDomain()));

        when(passwordEncoder.encode("StrongPassword123!"))
                .thenReturn("bcrypt-encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(100L);
                    return user;
                });
    }

    @Test
    void successfulRegistrationCreatesPendingUnverifiedRecruiter() {
        mockValidRegistration();

        UserResponse response =
                registrationService.registerRecruiter(validRequest());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(100L, response.getId());
        assertEquals("Test Recruiter", response.getName());
        assertEquals(
                "recruiter@testcompany.com",
                response.getEmail()
        );
        assertEquals("RECRUITER", response.getRole());

        assertEquals("RECRUITER", savedUser.getRole());
        assertEquals(company, savedUser.getCompany());
        assertEquals("bcrypt-encoded-password", savedUser.getPassword());

        assertFalse(savedUser.isEmailVerified());
        assertEquals(
                RecruiterApprovalStatus.PENDING,
                savedUser.getApprovalStatus()
        );

        assertNotNull(savedUser.getEmailVerificationTokenHash());
        assertEquals(
                64,
                savedUser.getEmailVerificationTokenHash().length()
        );

        assertNotNull(
                savedUser.getEmailVerificationTokenExpiresAt()
        );

        assertTrue(
                savedUser.getEmailVerificationTokenExpiresAt()
                        .isAfter(LocalDateTime.now())
        );

        assertTrue(
                savedUser.getEmailVerificationTokenExpiresAt()
                        .isBefore(LocalDateTime.now().plusMinutes(31))
        );

        verify(emailService).sendVerificationEmail(
                eq("recruiter@testcompany.com"),
                anyString()
        );

        // The API response must not expose verification tokens.
        assertFalse(response.toString().contains(
                savedUser.getEmailVerificationTokenHash()
        ));
    }

    @Test
    void registrationNormalizesEmailToLowercase() {
        mockValidRegistration();

        RecruiterRegistrationRequest request = validRequest();
        request.setEmail("Recruiter@TestCompany.com");

        UserResponse response =
                registrationService.registerRecruiter(request);

        assertEquals(
                "recruiter@testcompany.com",
                response.getEmail()
        );

        verify(userRepository).findByEmail(
                "recruiter@testcompany.com"
        );
    }

    @Test
    void duplicateEmailIsRejected() {
        RecruiterRegistrationRequest request = validRequest();

        User existingUser = new User();

        when(userRepository.findByEmail(
                "recruiter@testcompany.com"
        )).thenReturn(Optional.of(existingUser));

        assertThrows(
                DuplicateEmailException.class,
                () -> registrationService.registerRecruiter(request)
        );

        verifyNoInteractions(companyRepository);
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void unknownCompanyIsRejected() {
        RecruiterRegistrationRequest request = validRequest();

        when(userRepository.findByEmail(
                "recruiter@testcompany.com"
        )).thenReturn(Optional.empty());

        when(companyRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> registrationService.registerRecruiter(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void unregisteredCompanyDomainIsRejected() {
        RecruiterRegistrationRequest request = validRequest();

        when(userRepository.findByEmail(
                "recruiter@testcompany.com"
        )).thenReturn(Optional.empty());

        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(company));

        when(domainRepository
                .findByDomainAndCompanyIdAndActiveTrue(
                        "testcompany.com",
                        10L
                ))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCompanyEmailDomainException.class,
                () -> registrationService.registerRecruiter(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void domainBelongingToDifferentCompanyIsRejected() {
        RecruiterRegistrationRequest request = validRequest();

        when(userRepository.findByEmail(
                "recruiter@testcompany.com"
        )).thenReturn(Optional.empty());

        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(company));

        when(domainRepository
                .findByDomainAndCompanyIdAndActiveTrue(
                        "testcompany.com",
                        10L
                ))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCompanyEmailDomainException.class,
                () -> registrationService.registerRecruiter(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
