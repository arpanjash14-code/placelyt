package com.placelyt.placelyt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(name = "idx_jobs_company_id", columnList = "company_id"),
                @Index(name = "idx_jobs_status", columnList = "status"),
                @Index(name = "idx_jobs_application_deadline", columnList = "application_deadline"),
                @Index(name = "idx_jobs_employment_type", columnList = "employment_type"),
                @Index(name = "idx_jobs_work_mode", columnList = "work_mode"),
                @Index(name = "idx_jobs_location", columnList = "location")
        }
)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String title;

    @Column(length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkMode workMode;

    private String location;

    private Double minimumSalary;

    private Double maximumSalary;

    private Double minimumCgpa;

    private String requiredDegree;

    private Integer eligibleGraduationYear;

    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status;

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<JobEligibleBranch> eligibleBranches = new ArrayList<>();

    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<JobRequiredSkill> requiredSkills = new ArrayList<>();

    public Job() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getMinimumSalary() {
        return minimumSalary;
    }

    public void setMinimumSalary(Double minimumSalary) {
        this.minimumSalary = minimumSalary;
    }

    public Double getMaximumSalary() {
        return maximumSalary;
    }

    public void setMaximumSalary(Double maximumSalary) {
        this.maximumSalary = maximumSalary;
    }

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(Double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public String getRequiredDegree() {
        return requiredDegree;
    }

    public void setRequiredDegree(String requiredDegree) {
        this.requiredDegree = requiredDegree;
    }

    public Integer getEligibleGraduationYear() {
        return eligibleGraduationYear;
    }

    public void setEligibleGraduationYear(Integer eligibleGraduationYear) {
        this.eligibleGraduationYear = eligibleGraduationYear;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public List<JobEligibleBranch> getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(
            List<JobEligibleBranch> eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public List<JobRequiredSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(
            List<JobRequiredSkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}