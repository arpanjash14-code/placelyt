
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.RecruiterApprovalRequest;
import com.placelyt.placelyt.dto.RecruiterReviewResponse;
import com.placelyt.placelyt.service.RecruiterApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recruiters")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecruiterController {

    private final RecruiterApprovalService approvalService;

    public AdminRecruiterController(
            RecruiterApprovalService approvalService
    ) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<RecruiterReviewResponse> getPendingRecruiters() {
        return approvalService.getPendingRecruiters();
    }

    @GetMapping("/{recruiterId}")
    public RecruiterReviewResponse getRecruiterDetails(
            @PathVariable Long recruiterId
    ) {
        return approvalService.getRecruiterDetails(recruiterId);
    }

    @PatchMapping("/{recruiterId}/approval")
    public RecruiterReviewResponse reviewRecruiter(
            @PathVariable Long recruiterId,
            @Valid @RequestBody RecruiterApprovalRequest request
    ) {
        return approvalService.reviewRecruiter(
                recruiterId,
                request.getStatus()
        );
    }
}
