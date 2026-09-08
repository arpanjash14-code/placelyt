package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<JobResponse> createJob(
            @PathVariable Long companyId,
            @RequestBody JobRequest request) {

        return ResponseEntity.ok(
                jobService.createJob(companyId, request)
        );
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {

        return ResponseEntity.ok(
                jobService.getAllJobs()
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                jobService.getJobById(jobId)
        );
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobResponse>> getJobsByCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                jobService.getJobsByCompanyId(companyId)
        );
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @RequestBody JobRequest request) {

        return ResponseEntity.ok(
                jobService.updateJob(jobId, request)
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId) {

        jobService.deleteJob(jobId);

        return ResponseEntity.noContent().build();
    }
}