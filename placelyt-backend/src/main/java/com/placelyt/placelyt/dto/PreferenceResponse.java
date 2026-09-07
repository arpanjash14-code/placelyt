package com.placelyt.placelyt.dto;

public class PreferenceResponse {

    private Long id;
    private Long userId;
    private String preferredRole;
    private String preferredLocation;
    private String employmentType;
    private String workMode;
    private Double minimumSalary;
    private Double maximumSalary;
    private Boolean willingToRelocate;

    public PreferenceResponse() {
    }

    public PreferenceResponse(
            Long id,
            Long userId,
            String preferredRole,
            String preferredLocation,
            String employmentType,
            String workMode,
            Double minimumSalary,
            Double maximumSalary,
            Boolean willingToRelocate
    ) {
        this.id = id;
        this.userId = userId;
        this.preferredRole = preferredRole;
        this.preferredLocation = preferredLocation;
        this.employmentType = employmentType;
        this.workMode = workMode;
        this.minimumSalary = minimumSalary;
        this.maximumSalary = maximumSalary;
        this.willingToRelocate = willingToRelocate;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getWorkMode() {
        return workMode;
    }

    public Double getMinimumSalary() {
        return minimumSalary;
    }

    public Double getMaximumSalary() {
        return maximumSalary;
    }

    public Boolean getWillingToRelocate() {
        return willingToRelocate;
    }
}