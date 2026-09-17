package com.placelyt.placelyt.dto;

import java.util.List;

public class SkillIntelligenceResponse {

    private List<SkillRelationship> skills;

    public SkillIntelligenceResponse() {
    }

    public SkillIntelligenceResponse(
            List<SkillRelationship> skills) {
        this.skills = skills;
    }

    public List<SkillRelationship> getSkills() {
        return skills;
    }

    public void setSkills(
            List<SkillRelationship> skills) {
        this.skills = skills;
    }

    public static class SkillRelationship {

        private String skill;
        private List<String> relatedSkills;

        public SkillRelationship() {
        }

        public SkillRelationship(
                String skill,
                List<String> relatedSkills) {

            this.skill = skill;
            this.relatedSkills = relatedSkills;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public List<String> getRelatedSkills() {
            return relatedSkills;
        }

        public void setRelatedSkills(
                List<String> relatedSkills) {

            this.relatedSkills =
                    relatedSkills;
        }
    }
}