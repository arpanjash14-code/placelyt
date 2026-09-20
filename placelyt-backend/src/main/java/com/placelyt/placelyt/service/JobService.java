package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobFilterRequest;
import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.JobSortField;
import com.placelyt.placelyt.entity.JobStatus;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.exception.CompanyNotFoundException;
import com.placelyt.placelyt.exception.InvalidJobException;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.exception.SkillNotFoundException;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.JobEligibleBranchRepository;
import com.placelyt.placelyt.repository.JobRepository;
import com.placelyt.placelyt.repository.JobRequiredSkillRepository;
import com.placelyt.placelyt.repository.SkillRepository;
import com.placelyt.placelyt.specification.JobSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    private static final int MAX_PAGE_SIZE = 100;

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

        validateJobRequest(request);

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

    public List<JobResponse> searchJobs(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new InvalidJobException(
                    "Search keyword cannot be blank"
            );
        }

        return jobRepository.searchByKeyword(keyword.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<JobResponse> filterJobs(
            JobFilterRequest filter,
            int page,
            int size) {

        validatePagination(page, size);

        Sort sort = Sort.unsorted();

        if (filter.getSortBy() != null) {

            Sort.Direction direction =
                    filter.getDirection() != null
                            ? filter.getDirection()
                            : Sort.Direction.ASC;

            sort = Sort.by(
                    direction,
                    getSortProperty(filter.getSortBy())
            );
        }

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return jobRepository.findAll(
                        JobSpecification.withFilters(filter),
                        pageable
                )
                .map(this::toResponse);
    }

    private void validatePagination(
            int page,
            int size) {

        if (page < 0) {
            throw new InvalidJobException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1) {
            throw new InvalidJobException(
                    "Page size must be at least 1"
            );
        }

        if (size > MAX_PAGE_SIZE) {
            throw new InvalidJobException(
                    "Page size cannot exceed "
                            + MAX_PAGE_SIZE
            );
        }
    }

    private String getSortProperty(JobSortField sortField) {

        return switch (sortField) {

            case MINIMUM_SALARY ->
                    "minimumSalary";

            case MAXIMUM_SALARY ->
                    "maximumSalary";

            case MINIMUM_CGPA ->
                    "minimumCgpa";

            case APPLICATION_DEADLINE ->
                    "applicationDeadline";

            case TITLE ->
                    "title";
        };
    }

    public JobResponse getJobById(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        return toResponse(job);
    }

   public List<JobResponse> getJobsByCompanyId(
        Long companyId) {

    List<Job> jobs = jobRepository.findByCompanyId(companyId);

    if (jobs.isEmpty() && !companyRepository.existsById(companyId)) {
        throw new CompanyNotFoundException("Company not found");
    }

    return jobs.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}
    @Transactional
    public JobResponse updateJob(
            Long jobId,
            JobRequest request) {

        validateJobRequest(request);

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
         * Load all required skills in one database operation.
         */
        Map<Long, Skill> skillsById =
                loadSkillsById(request.getRequiredSkillIds());

        /*
         * Add the new required skills.
         */
        if (request.getRequiredSkillIds() != null) {

            for (Long skillId : request.getRequiredSkillIds()) {

                Skill skill = skillsById.get(skillId);

                if (skill == null) {
                    throw new SkillNotFoundException(
                            "Skill not found: " + skillId
                    );
                }

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

    private void validateJobRequest(JobRequest request) {

        if (request.getMinimumSalary() != null
                && request.getMaximumSalary() != null
                && request.getMinimumSalary()
                > request.getMaximumSalary()) {

            throw new InvalidJobException(
                    "Minimum salary cannot be greater than maximum salary");
        }

        if (request.getStatus() == JobStatus.OPEN
                && request.getApplicationDeadline() == null) {

            throw new InvalidJobException(
                    "An open job must have an application deadline");
        }

        if (request.getStatus() == JobStatus.OPEN
                && request.getApplicationDeadline() != null
                && request.getApplicationDeadline()
                .isBefore(LocalDate.now())) {

            throw new InvalidJobException(
                    "An open job cannot have a past application deadline");
        }

        validateUniqueValues(
                request.getEligibleBranches(),
                "eligible branches"
        );

        validateUniqueValues(
                request.getRequiredSkillIds(),
                "required skills"
        );

        if (request.getEligibleBranches() != null
                && request.getEligibleBranches().stream().anyMatch(
                branch -> branch == null || branch.isBlank())) {

            throw new InvalidJobException(
                    "Eligible branches cannot contain blank values");
        }

        if (request.getRequiredSkillIds() != null
                && request.getRequiredSkillIds().stream().anyMatch(
                skillId -> skillId == null || skillId <= 0)) {

            throw new InvalidJobException(
                    "Required skill IDs must be positive");
        }
    }

    private <T> void validateUniqueValues(
            List<T> values,
            String fieldName) {

        if (values == null) {
            return;
        }

        Set<T> uniqueValues = new HashSet<>(values);

        if (uniqueValues.size() != values.size()) {

            throw new InvalidJobException(
                    "Duplicate values are not allowed in " + fieldName);
        }
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
         * Load all required skills in one database operation.
         */
        Map<Long, Skill> skillsById =
                loadSkillsById(request.getRequiredSkillIds());

        /*
         * Add required skills during job creation.
         */
        if (request.getRequiredSkillIds() != null) {

            for (Long skillId : request.getRequiredSkillIds()) {

                Skill skill = skillsById.get(skillId);

                if (skill == null) {
                    throw new SkillNotFoundException(
                            "Skill not found: " + skillId
                    );
                }

                JobRequiredSkill requiredSkill =
                        new JobRequiredSkill();

                requiredSkill.setJob(job);
                requiredSkill.setSkill(skill);

                job.getRequiredSkills()
                        .add(requiredSkill);
            }
        }
    }

    private Map<Long, Skill> loadSkillsById(
            List<Long> skillIds) {

        if (skillIds == null || skillIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Skill> skills =
                skillRepository.findAllById(skillIds);

        return skills.stream()
                .collect(Collectors.toMap(
                        Skill::getId,
                        skill -> skill
                ));
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