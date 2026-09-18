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
        private List<String> transferableSkills;

        public SkillRelationship() {
        }

        public SkillRelationship(
                String skill,
                List<String> relatedSkills,
                List<String> transferableSkills) {

            this.skill = skill;
            this.relatedSkills = relatedSkills;
            this.transferableSkills = transferableSkills;
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

        public List<String> getTransferableSkills() {
            return transferableSkills;
        }

        public void setTransferableSkills(
                List<String> transferableSkills) {

            this.transferableSkills =
                    transferableSkills;
        }
    }
}