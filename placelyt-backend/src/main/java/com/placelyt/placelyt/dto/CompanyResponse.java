package com.placelyt.placelyt.dto;

public class CompanyResponse {

    private Long id;
    private String name;
    private String industry;
    private String website;
    private String location;
    private String description;
    private String companySize;
    private Integer foundedYear;

    public CompanyResponse() {
    }

    public CompanyResponse(
            Long id,
            String name,
            String industry,
            String website,
            String location,
            String description,
            String companySize,
            Integer foundedYear
    ) {
        this.id = id;
        this.name = name;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.description = description;
        this.companySize = companySize;
        this.foundedYear = foundedYear;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanySize() {
        return companySize;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }
}