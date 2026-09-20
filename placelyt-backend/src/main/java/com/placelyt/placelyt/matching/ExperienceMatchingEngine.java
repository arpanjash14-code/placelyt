package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.UserSkill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExperienceMatchingEngine {

    private final SkillMatchingEngine skillMatchingEngine;

    public ExperienceMatchingEngine(
            SkillMatchingEngine skillMatchingEngine) {

        this.skillMatchingEngine =
                skillMatchingEngine;
    }

    /*
     * ---------------------------------------------------------
     * AVERAGE RELEVANT EXPERIENCE
     * ---------------------------------------------------------
     *
     * Only experience from skills required by the job
     * is considered.
     *
     * Returns:
     *
     * - average experience when relevant experience exists
     * - null when no relevant experience exists
     *
     * A value of 0.0 is valid and means the student has
     * the required skill but zero years of experience.
     */

    public Double calculateAverageRelevantExperience(
            List<UserSkill> userSkills,
            Job job) {

        if (job == null) {
            return null;
        }

        if (userSkills == null
                || userSkills.isEmpty()) {

            return null;
        }

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            return null;
        }

        List<Double> relevantExperience =
                new ArrayList<>();

        for (JobRequiredSkill requiredSkill
                : job.getRequiredSkills()) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            String requiredSkillName =
                    requiredSkill.getSkill()
                            .getName()
                            .trim();

            if (!skillMatchingEngine.hasSkill(
                    requiredSkillName,
                    userSkills)) {

                continue;
            }

            for (UserSkill userSkill : userSkills) {

                if (userSkill == null
                        || userSkill.getSkill() == null
                        || userSkill.getSkill().getName() == null) {

                    continue;
                }

                String userSkillName =
                        userSkill.getSkill()
                                .getName()
                                .trim();

                if (requiredSkillName.equalsIgnoreCase(
                        userSkillName)) {

                    Double yearsOfExperience =
                            userSkill.getYearsOfExperience();

                    if (yearsOfExperience != null
                            && yearsOfExperience >= 0.0) {

                        relevantExperience.add(
                                yearsOfExperience
                        );
                    }

                    break;
                }
            }
        }

        if (relevantExperience.isEmpty()) {
            return null;
        }

        double totalExperience = 0.0;

        for (Double experience
                : relevantExperience) {

            totalExperience += experience;
        }

        return totalExperience
                / relevantExperience.size();
    }
}