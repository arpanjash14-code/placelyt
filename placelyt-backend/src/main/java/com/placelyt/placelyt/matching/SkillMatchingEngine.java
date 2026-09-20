package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.UserSkill;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SkillMatchingEngine {

    public boolean hasSkill(
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

            if (requiredSkillName.trim()
                    .equalsIgnoreCase(
                            userSkill.getSkill()
                                    .getName()
                                    .trim())) {

                return true;
            }
        }

        return false;
    }

    public List<String> findMatchedSkillNames(
            List<String> requiredSkillNames,
            List<UserSkill> userSkills) {

        List<String> matchedSkills = new ArrayList<>();

        if (requiredSkillNames == null ||
                requiredSkillNames.isEmpty()) {

            return matchedSkills;
        }

        for (String requiredSkillName : requiredSkillNames) {

            if (hasSkill(requiredSkillName, userSkills)) {
                matchedSkills.add(requiredSkillName);
            }
        }

        return matchedSkills;
    }

    public List<String> findMissingSkillNames(
            List<String> requiredSkillNames,
            List<UserSkill> userSkills) {

        List<String> missingSkills = new ArrayList<>();

        if (requiredSkillNames == null ||
                requiredSkillNames.isEmpty()) {

            return missingSkills;
        }

        for (String requiredSkillName : requiredSkillNames) {

            if (!hasSkill(requiredSkillName, userSkills)) {
                missingSkills.add(requiredSkillName);
            }
        }

        return missingSkills;
    }

    public int countMatchedSkills(
            List<String> requiredSkillNames,
            List<UserSkill> userSkills) {

        return findMatchedSkillNames(
                requiredSkillNames,
                userSkills
        ).size();
    }
}