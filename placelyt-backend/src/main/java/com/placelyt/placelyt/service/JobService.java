package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.exception.CompanyNotFoundException;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.exception.SkillNotFoundException;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.JobEligibleBranchRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.JobRequiredSkillRepository;
import com.placelyt.placelyt.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final JobEligibleBranchRepository jobEligibleBranchRepository;
    private final JobRequiredSkillRepository jobRequiredSkillRepository;

    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            SkillRepository skillRepository,
            JobEligibleBranchRepository jobEligibleBranchRepository,
            JobRequiredSkillRepository jobRequiredSkillRepository) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.jobEligibleBranchRepository = jobEligibleBranchRepository;
        this.jobRequiredSkillRepository = jobRequiredSkillRepository;
    }

    public JobResponse createJob(
            Long companyId,
            JobRequest request) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        Job job = new Job();

        job.setCompany(company);

        applyRequestToJob(job, request);

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);
    }

    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        return toResponse(job);
    }

    public List<JobResponse> getJobsByCompanyId(
            Long companyId) {

        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        return jobRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobResponse updateJob(
            Long jobId,
            JobRequest request) {

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        existingJob.setTitle(request.getTitle());
        existingJob.setDescription(request.getDescription());
        existingJob.setEmploymentType(request.getEmploymentType());
        existingJob.setWorkMode(request.getWorkMode());
        existingJob.setLocation(request.getLocation());

        existingJob.setMinimumSalary(request.getMinimumSalary());
        existingJob.setMaximumSalary(request.getMaximumSalary());
        existingJob.setMinimumCgpa(request.getMinimumCgpa());

        existingJob.setRequiredDegree(request.getRequiredDegree());
        existingJob.setEligibleGraduationYear(
                request.getEligibleGraduationYear()
        );

        existingJob.setApplicationDeadline(
                request.getApplicationDeadline()
        );

        existingJob.setStatus(request.getStatus());

        /*
         * Remove old eligibility requirements directly
         * from the database before inserting replacements.
         */
        jobEligibleBranchRepository.deleteByJobId(jobId);
        jobRequiredSkillRepository.deleteByJobId(jobId);

        jobEligibleBranchRepository.flush();
        jobRequiredSkillRepository.flush();

        /*
         * Clear the in-memory collections as well.
         */
        existingJob.getEligibleBranches().clear();
        existingJob.getRequiredSkills().clear();

        /*
         * Add the new eligible branches.
         */
        if (request.getEligibleBranches() != null) {

            for (String branch : request.getEligibleBranches()) {

                JobEligibleBranch eligibleBranch =
                        new JobEligibleBranch();

                eligibleBranch.setJob(existingJob);
                eligibleBranch.setBranch(branch);

                existingJob.getEligibleBranches()
                        .add(eligibleBranch);
            }
        }

        /*
         * Add the new required skills.
         */
        if (request.getRequiredSkillIds() != null) {

            for (Long skillId : request.getRequiredSkillIds()) {

                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() ->
                                new SkillNotFoundException(
                                        "Skill not found: " + skillId
                                )
                        );

                JobRequiredSkill requiredSkill =
                        new JobRequiredSkill();

                requiredSkill.setJob(existingJob);
                requiredSkill.setSkill(skill);

                existingJob.getRequiredSkills()
                        .add(requiredSkill);
            }
        }

        Job savedJob = jobRepository.save(existingJob);

        return toResponse(savedJob);
    }

    public void deleteJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        jobRepository.delete(job);
    }

    private void applyRequestToJob(
            Job job,
            JobRequest request) {

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setEmploymentType(request.getEmploymentType());
        job.setWorkMode(request.getWorkMode());
        job.setLocation(request.getLocation());

        job.setMinimumSalary(request.getMinimumSalary());
        job.setMaximumSalary(request.getMaximumSalary());
        job.setMinimumCgpa(request.getMinimumCgpa());

        job.setRequiredDegree(request.getRequiredDegree());
        job.setEligibleGraduationYear(
                request.getEligibleGraduationYear()
        );

        job.setApplicationDeadline(
                request.getApplicationDeadline()
        );

        job.setStatus(request.getStatus());

        /*
         * Add eligible branches during job creation.
         */
        if (request.getEligibleBranches() != null) {

            for (String branch : request.getEligibleBranches()) {

                JobEligibleBranch eligibleBranch =
                        new JobEligibleBranch();

                eligibleBranch.setJob(job);
                eligibleBranch.setBranch(branch);

                job.getEligibleBranches()
                        .add(eligibleBranch);
            }
        }

        /*
         * Add required skills during job creation.
         */
        if (request.getRequiredSkillIds() != null) {

            for (Long skillId : request.getRequiredSkillIds()) {

                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() ->
                                new SkillNotFoundException(
                                        "Skill not found: " + skillId
                                )
                        );

                JobRequiredSkill requiredSkill =
                        new JobRequiredSkill();

                requiredSkill.setJob(job);
                requiredSkill.setSkill(skill);

                job.getRequiredSkills()
                        .add(requiredSkill);
            }
        }
    }

    private JobResponse toResponse(Job job) {

        List<String> eligibleBranches =
                job.getEligibleBranches()
                        .stream()
                        .map(JobEligibleBranch::getBranch)
                        .collect(Collectors.toList());

        List<String> requiredSkills =
                job.getRequiredSkills()
                        .stream()
                        .map(requiredSkill ->
                                requiredSkill.getSkill().getName()
                        )
                        .collect(Collectors.toList());

        return new JobResponse(
                job.getId(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getTitle(),
                job.getDescription(),
                job.getEmploymentType(),
                job.getWorkMode(),
                job.getLocation(),
                job.getMinimumSalary(),
                job.getMaximumSalary(),
                job.getMinimumCgpa(),
                job.getRequiredDegree(),
                job.getEligibleGraduationYear(),
                eligibleBranches,
                requiredSkills,
                job.getApplicationDeadline(),
                job.getStatus()
        );
    }
}