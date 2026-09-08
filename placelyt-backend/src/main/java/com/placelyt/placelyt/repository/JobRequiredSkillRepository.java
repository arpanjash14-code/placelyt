package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.JobRequiredSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRequiredSkillRepository
        extends JpaRepository<JobRequiredSkill, Long> {

    void deleteByJobId(Long jobId);
}