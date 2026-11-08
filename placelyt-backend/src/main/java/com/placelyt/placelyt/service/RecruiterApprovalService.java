
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.RecruiterReviewResponse;
import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.UserNotFoundException;
import com.placelyt.placelyt.exception.RecruiterApprovalException;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecruiterApprovalService {

    private static final String RECRUITER_ROLE = "RECRUITER";

    private final UserRepository userRepository;

    public RecruiterApprovalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RecruiterReviewResponse> getPendingRecruiters() {

        return userRepository
                .findByRoleAndApprovalStatusOrderByIdAsc(
                        RECRUITER_ROLE,
                        RecruiterApprovalStatus.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecruiterReviewResponse getRecruiterDetails(Long recruiterId) {

        User recruiter = findRecruiter(recruiterId);

        return toResponse(recruiter);
    }

    @Transactional
    public RecruiterReviewResponse reviewRecruiter(
            Long recruiterId,
            RecruiterApprovalStatus requestedStatus
    ) {

        if (requestedStatus == null
                || requestedStatus == RecruiterApprovalStatus.PENDING) {
            throw new RecruiterApprovalException(
                    "Admin must approve or reject the recruiter"
            );
        }

        User recruiter = findRecruiter(recruiterId);

        if (recruiter.getApprovalStatus()
                != RecruiterApprovalStatus.PENDING) {
            throw new RecruiterApprovalException(
                    "This recruiter has already been reviewed"
            );
        }

        if (requestedStatus == RecruiterApprovalStatus.APPROVED
                && !recruiter.isEmailVerified()) {
            throw new RecruiterApprovalException(
                    "Recruiter email must be verified before approval"
            );
        }

        recruiter.setApprovalStatus(requestedStatus);
        recruiter.setReviewedAt(LocalDateTime.now());

        User savedRecruiter = userRepository.save(recruiter);

        return toResponse(savedRecruiter);
    }

    private User findRecruiter(Long recruiterId) {

        return userRepository
                .findByIdAndRole(recruiterId, RECRUITER_ROLE)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Recruiter not found"
                        )
                );
    }

    private RecruiterReviewResponse toResponse(User recruiter) {

        Long companyId = null;
        String companyName = null;

        if (recruiter.getCompany() != null) {
            companyId = recruiter.getCompany().getId();
            companyName = recruiter.getCompany().getName();
        }

        return new RecruiterReviewResponse(
                recruiter.getId(),
                recruiter.getName(),
                recruiter.getEmail(),
                companyId,
                companyName,
                recruiter.isEmailVerified(),
                recruiter.getApprovalStatus(),
                recruiter.getEmailVerifiedAt(),
                recruiter.getReviewedAt()
        );
    }
}
