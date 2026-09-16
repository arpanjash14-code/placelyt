package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.CareerPathSkill;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.repository.CareerPathSkillRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CareerPathEngine {

    private final CareerPathSkillRepository careerPathSkillRepository;

    public CareerPathEngine(
            CareerPathSkillRepository careerPathSkillRepository) {
        this.careerPathSkillRepository =
                careerPathSkillRepository;
    }

    public double calculateReadiness(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        if (requiredSkills.isEmpty()) {
            return 0.0;
        }

        if (userSkills == null || userSkills.isEmpty()) {
            return 0.0;
        }

        int matchedSkills = 0;

        for (CareerPathSkill requiredSkill : requiredSkills) {

            if (hasSkill(
                    requiredSkill.getSkill().getName(),
                    userSkills)) {

                matchedSkills++;
            }
        }

        return ((double) matchedSkills /
                requiredSkills.size()) * 100.0;
    }

    public List<String> getMatchedSkills(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        List<String> matchedSkills = new ArrayList<>();

        if (userSkills == null || userSkills.isEmpty()) {
            return matchedSkills;
        }

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        for (CareerPathSkill requiredSkill : requiredSkills) {

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            if (hasSkill(requiredSkillName, userSkills)) {
                matchedSkills.add(requiredSkillName);
            }
        }

        return matchedSkills;
    }

    public List<String> getMissingSkills(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        List<String> missingSkills = new ArrayList<>();

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        for (CareerPathSkill requiredSkill : requiredSkills) {

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            if (!hasSkill(requiredSkillName, userSkills)) {
                missingSkills.add(requiredSkillName);
            }
        }

        return missingSkills;
    }

    private boolean hasSkill(
            String requiredSkillName,
            List<UserSkill> userSkills) {

        if (requiredSkillName == null ||
                userSkills == null) {

            return false;
        }

        for (UserSkill userSkill : userSkills) {

            if (userSkill == null ||
                    userSkill.getSkill() == null ||
                    userSkill.getSkill().getName() == null) {

                continue;
            }

            if (requiredSkillName.equalsIgnoreCase(
                    userSkill.getSkill().getName())) {

                return true;
            }
        }

        return false;
    }
}