package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.entity.Company;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.exception.CompanyNotFoundException;
import com.placelyt.placelyt.exception.JobNotFoundException;
import com.placelyt.placelyt.repository.CompanyRepository;
import com.placelyt.placelyt.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    public JobResponse createJob(
            Long companyId,
            Job job) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        job.setCompany(company);

        Job savedJob =
                jobRepository.save(job);

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

    public JobResponse updateJob(
            Long jobId,
            Job updatedJob) {

        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setEmploymentType(
                updatedJob.getEmploymentType()
        );
        existingJob.setWorkMode(
                updatedJob.getWorkMode()
        );
        existingJob.setLocation(
                updatedJob.getLocation()
        );
        existingJob.setMinimumSalary(
                updatedJob.getMinimumSalary()
        );
        existingJob.setMaximumSalary(
                updatedJob.getMaximumSalary()
        );
        existingJob.setMinimumCgpa(
                updatedJob.getMinimumCgpa()
        );
        existingJob.setApplicationDeadline(
                updatedJob.getApplicationDeadline()
        );
        existingJob.setStatus(
                updatedJob.getStatus()
        );

        Job savedJob =
                jobRepository.save(existingJob);

        return toResponse(savedJob);
    }

    public void deleteJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        jobRepository.delete(job);
    }

    private JobResponse toResponse(Job job) {

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
                job.getApplicationDeadline(),
                job.getStatus()
        );
    }
}