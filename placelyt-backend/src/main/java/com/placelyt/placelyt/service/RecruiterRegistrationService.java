
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.RecruiterRegistrationRequest;
import com.placelyt.placelyt.dto.UserResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.DuplicateEmailException;
import com.placelyt.placelyt.exception.InvalidCompanyEmailDomainException;
import com.placelyt.placelyt.exception.UserNotFoundException;
import com.placelyt.placelyt.repository.CompanyEmailDomainRepository;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.UserRepository;
import com.placelyt.placelyt.util.EmailDomainUtils;
import com.placelyt.placelyt.util.EmailVerificationTokenUtils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class RecruiterRegistrationService {

    private static final int VERIFICATION_TOKEN_EXPIRY_MINUTES = 30;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyEmailDomainRepository companyEmailDomainRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RecruiterRegistrationService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            CompanyEmailDomainRepository companyEmailDomainRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyEmailDomainRepository = companyEmailDomainRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse registerRecruiter(
            RecruiterRegistrationRequest request) {

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new DuplicateEmailException(
                    "Email is already registered"
            );
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Company was not found"
                        )
                );

        String emailDomain;

        try {
            emailDomain = EmailDomainUtils.extractDomain(normalizedEmail);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCompanyEmailDomainException(
                    "Please provide a valid company email address"
            );
        }

        boolean domainBelongsToCompany =
                companyEmailDomainRepository
                        .findByDomainAndCompanyIdAndActiveTrue(
                                emailDomain,
                                company.getId()
                        )
                        .isPresent();

        if (!domainBelongsToCompany) {
            throw new InvalidCompanyEmailDomainException(
                    "Your email domain is not authorized for the selected company"
            );
        }

        User recruiter = new User();

        recruiter.setName(request.getName().trim());
        recruiter.setEmail(normalizedEmail);
        recruiter.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        recruiter.setRole("RECRUITER");
        recruiter.setCompany(company);

        recruiter.setEmailVerified(false);
        recruiter.setEmailVerifiedAt(null);
        recruiter.setApprovalStatus(RecruiterApprovalStatus.PENDING);
        recruiter.setReviewedAt(null);

        String rawToken = EmailVerificationTokenUtils.generateToken();

        recruiter.setEmailVerificationTokenHash(
                EmailVerificationTokenUtils.hashToken(rawToken)
        );

        recruiter.setEmailVerificationTokenExpiresAt(
                LocalDateTime.now().plusMinutes(
                        VERIFICATION_TOKEN_EXPIRY_MINUTES
                )
        );

        User savedRecruiter = userRepository.save(recruiter);

        // Send the raw token only through the verification email.
        // The database stores only its SHA-256 hash.
        emailService.sendVerificationEmail(
                savedRecruiter.getEmail(),
                rawToken
        );

        return new UserResponse(
                savedRecruiter.getId(),
                savedRecruiter.getName(),
                savedRecruiter.getEmail(),
                savedRecruiter.getRole()
        );
    }
}
