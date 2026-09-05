package com.placelyt.placelyt.dto;

public class StudentProfileResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String college;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private Double cgpa;
    private String location;

    public StudentProfileResponse() {
    }

    public StudentProfileResponse(
            Long id,
            Long userId,
            String fullName,
            String college,
            String degree,
            String branch,
            Integer graduationYear,
            Double cgpa,
            String location
    ) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.college = college;
        this.degree = degree;
        this.branch = branch;
        this.graduationYear = graduationYear;
        this.cgpa = cgpa;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCollege() {
        return college;
    }

    public String getDegree() {
        return degree;
    }

    public String getBranch() {
        return branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public String getLocation() {
        return location;
    }
}