package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.JobEligibleBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobEligibleBranchRepository
        extends JpaRepository<JobEligibleBranch, Long> {

    void deleteByJobId(Long jobId);
}