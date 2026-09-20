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
    private final SkillMatchingEngine skillMatchingEngine;

    public CareerPathEngine(
            CareerPathSkillRepository careerPathSkillRepository,
            SkillMatchingEngine skillMatchingEngine) {

        this.careerPathSkillRepository =
                careerPathSkillRepository;

        this.skillMatchingEngine =
                skillMatchingEngine;
    }

    public double calculateReadiness(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        return calculateReadiness(
                careerPath,
                userSkills,
                requiredSkills
        );
    }

    public double calculateReadiness(
            CareerPath careerPath,
            List<UserSkill> userSkills,
            List<CareerPathSkill> requiredSkills) {

        if (requiredSkills == null ||
                requiredSkills.isEmpty()) {

            return 0.0;
        }

        if (userSkills == null ||
                userSkills.isEmpty()) {

            return 0.0;
        }

        int matchedSkills = 0;

        for (CareerPathSkill requiredSkill : requiredSkills) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            if (skillMatchingEngine.hasSkill(
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

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        return getMatchedSkills(
                careerPath,
                userSkills,
                requiredSkills
        );
    }

    public List<String> getMatchedSkills(
            CareerPath careerPath,
            List<UserSkill> userSkills,
            List<CareerPathSkill> requiredSkills) {

        List<String> matchedSkills =
                new ArrayList<>();

        if (userSkills == null ||
                userSkills.isEmpty()) {

            return matchedSkills;
        }

        if (requiredSkills == null ||
                requiredSkills.isEmpty()) {

            return matchedSkills;
        }

        for (CareerPathSkill requiredSkill : requiredSkills) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            if (skillMatchingEngine.hasSkill(
                    requiredSkillName,
                    userSkills)) {

                matchedSkills.add(requiredSkillName);
            }
        }

        return matchedSkills;
    }

    public List<String> getMissingSkills(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        List<CareerPathSkill> requiredSkills =
                careerPathSkillRepository
                        .findByCareerPathId(careerPath.getId());

        return getMissingSkills(
                careerPath,
                userSkills,
                requiredSkills
        );
    }

    public List<String> getMissingSkills(
            CareerPath careerPath,
            List<UserSkill> userSkills,
            List<CareerPathSkill> requiredSkills) {

        List<String> missingSkills =
                new ArrayList<>();

        if (requiredSkills == null ||
                requiredSkills.isEmpty()) {

            return missingSkills;
        }

        for (CareerPathSkill requiredSkill : requiredSkills) {

            if (requiredSkill == null
                    || requiredSkill.getSkill() == null
                    || requiredSkill.getSkill().getName() == null) {

                continue;
            }

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            if (!skillMatchingEngine.hasSkill(
                    requiredSkillName,
                    userSkills)) {

                missingSkills.add(requiredSkillName);
            }
        }

        return missingSkills;
    }
}