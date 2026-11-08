
package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.RecruiterReviewResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.RecruiterApprovalException;
import com.placelyt.placelyt.exception.UserNotFoundException;
import com.placelyt.placelyt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecruiterApprovalServiceTest {

    private UserRepository userRepository;
    private RecruiterApprovalService approvalService;

    private User recruiter;
    private Company company;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        approvalService = new RecruiterApprovalService(userRepository);

        company = new Company();
        company.setId(10L);
        company.setName("Test Technologies");

        recruiter = new User();
        recruiter.setId(100L);
        recruiter.setName("Test Recruiter");
        recruiter.setEmail("recruiter@testcompany.com");
        recruiter.setRole("RECRUITER");
        recruiter.setCompany(company);
        recruiter.setEmailVerified(true);
        recruiter.setApprovalStatus(RecruiterApprovalStatus.PENDING);
    }

    @Test
    void adminCanApproveVerifiedPendingRecruiter() {

        when(userRepository.findByIdAndRole(100L, "RECRUITER"))
                .thenReturn(Optional.of(recruiter));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecruiterReviewResponse response =
                approvalService.reviewRecruiter(
                        100L,
                        RecruiterApprovalStatus.APPROVED
                );

        assertEquals(
                RecruiterApprovalStatus.APPROVED,
                recruiter.getApprovalStatus()
        );

        assertNotNull(recruiter.getReviewedAt());

        assertEquals(100L, response.getId());
        assertEquals(
                RecruiterApprovalStatus.APPROVED,
                response.getApprovalStatus()
        );
        assertEquals("Test Technologies", response.getCompanyName());
        assertTrue(recruiter.isRecruiterActive());

        verify(userRepository).save(recruiter);
    }

    @Test
    void adminCanRejectPendingRecruiter() {

        when(userRepository.findByIdAndRole(100L, "RECRUITER"))
                .thenReturn(Optional.of(recruiter));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecruiterReviewResponse response =
                approvalService.reviewRecruiter(
                        100L,
                        RecruiterApprovalStatus.REJECTED
                );

        assertEquals(
                RecruiterApprovalStatus.REJECTED,
                recruiter.getApprovalStatus()
        );

        assertNotNull(recruiter.getReviewedAt());

        assertEquals(
                RecruiterApprovalStatus.REJECTED,
                response.getApprovalStatus()
        );

        assertFalse(recruiter.isRecruiterActive());

        verify(userRepository).save(recruiter);
    }

    @Test
    void unverifiedRecruiterCannotBeApproved() {

        recruiter.setEmailVerified(false);

        when(userRepository.findByIdAndRole(100L, "RECRUITER"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                RecruiterApprovalException.class,
                () -> approvalService.reviewRecruiter(
                        100L,
                        RecruiterApprovalStatus.APPROVED
                )
        );

        assertEquals(
                RecruiterApprovalStatus.PENDING,
                recruiter.getApprovalStatus()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void alreadyReviewedRecruiterCannotBeReviewedAgain() {

        recruiter.setApprovalStatus(RecruiterApprovalStatus.APPROVED);

        when(userRepository.findByIdAndRole(100L, "RECRUITER"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                RecruiterApprovalException.class,
                () -> approvalService.reviewRecruiter(
                        100L,
                        RecruiterApprovalStatus.REJECTED
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void pendingCannotBeSubmittedAsAdminDecision() {

        assertThrows(
                RecruiterApprovalException.class,
                () -> approvalService.reviewRecruiter(
                        100L,
                        RecruiterApprovalStatus.PENDING
                )
        );

        verify(userRepository, never())
                .findByIdAndRole(anyLong(), anyString());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void nonexistentRecruiterReturnsNotFoundException() {

        when(userRepository.findByIdAndRole(999L, "RECRUITER"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> approvalService.reviewRecruiter(
                        999L,
                        RecruiterApprovalStatus.APPROVED
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void pendingRecruitersCanBeRetrieved() {

        when(userRepository.findByRoleAndApprovalStatusOrderByIdAsc(
                "RECRUITER",
                RecruiterApprovalStatus.PENDING
        )).thenReturn(List.of(recruiter));

        List<RecruiterReviewResponse> response =
                approvalService.getPendingRecruiters();

        assertEquals(1, response.size());
        assertEquals(100L, response.get(0).getId());
        assertEquals(
                RecruiterApprovalStatus.PENDING,
                response.get(0).getApprovalStatus()
        );

        verify(userRepository).findByRoleAndApprovalStatusOrderByIdAsc(
                "RECRUITER",
                RecruiterApprovalStatus.PENDING
        );
    }
}
