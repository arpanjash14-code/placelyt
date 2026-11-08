
package com.placelyt.placelyt.dto;

import com.placelyt.placelyt.entity.RecruiterApprovalStatus;
import jakarta.validation.constraints.NotNull;

public class RecruiterApprovalRequest {

    @NotNull(message = "Approval status is required")
    private RecruiterApprovalStatus status;

    public RecruiterApprovalRequest() {
    }

    public RecruiterApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(RecruiterApprovalStatus status) {
        this.status = status;
    }
}
