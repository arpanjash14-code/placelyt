package com.placelyt.placelyt.dto;

public class UserSkillResponse {

    private Long id;
    private Long userId;
    private Long skillId;
    private String skillName;
    private String category;
    private String proficiency;
    private Double yearsOfExperience;

    public UserSkillResponse() {
    }

    public UserSkillResponse(
            Long id,
            Long userId,
            Long skillId,
            String skillName,
            String category,
            String proficiency,
            Double yearsOfExperience
    ) {
        this.id = id;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.proficiency = proficiency;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getCategory() {
        return category;
    }

    public String getProficiency() {
        return proficiency;
    }

    public Double getYearsOfExperience() {
        return yearsOfExperience;
    }
}