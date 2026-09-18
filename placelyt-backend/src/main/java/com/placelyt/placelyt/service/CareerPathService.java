package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CareerPathService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final CareerPathRepository careerPathRepository;
    private final CareerPathEngine careerPathEngine;

    public CareerPathService(
            StudentProfileRepository studentProfileRepository,
            UserSkillRepository userSkillRepository,
            CareerPathRepository careerPathRepository,
            CareerPathEngine careerPathEngine) {

        this.studentProfileRepository =
                studentProfileRepository;

        this.userSkillRepository =
                userSkillRepository;

        this.careerPathRepository =
                careerPathRepository;

        this.careerPathEngine =
                careerPathEngine;
    }

    public List<CareerPathResponse> getCareerPaths(Long userId) {

        studentProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student profile not found"
                        ));

        List<UserSkill> userSkills =
                userSkillRepository.findByUserId(userId);

        List<CareerPath> careerPaths =
                careerPathRepository.findAll();

        return careerPaths.stream()
                .map(careerPath ->
                        createResponse(
                                careerPath,
                                userSkills
                        ))
                .sorted(
                        Comparator.comparingDouble(
                                CareerPathResponse::getReadinessScore
                        ).reversed()
                )
                .toList();
    }

    public List<CareerPathAlternativeResponse> getCareerPathAlternatives(
            Long userId,
            Long targetCareerPathId) {

        studentProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student profile not found"
                        ));

        CareerPath targetCareerPath =
                careerPathRepository
                        .findById(targetCareerPathId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Target career path not found"
                                ));

        List<UserSkill> userSkills =
                userSkillRepository.findByUserId(userId);

        List<String> targetMatchedSkills =
                careerPathEngine.getMatchedSkills(
                        targetCareerPath,
                        userSkills
                );

        Set<String> normalizedTargetSkills =
                normalizeSkills(targetMatchedSkills);

        List<CareerPath> careerPaths =
                careerPathRepository.findAll();

        return careerPaths.stream()
                .filter(careerPath ->
                        !careerPath.getId()
                                .equals(targetCareerPathId))
                .map(careerPath ->
                        createAlternativeResponse(
                                careerPath,
                                userSkills,
                                normalizedTargetSkills
                        ))
                .filter(response ->
                        hasMeaningfulOverlap(
                                response.getMatchedSkills(),
                                normalizedTargetSkills
                        ))
                .sorted(
                        Comparator.comparingDouble(
                                CareerPathAlternativeResponse::
                                        getReadinessScore
                        ).reversed()
                )
                .toList();
    }

    private CareerPathResponse createResponse(
            CareerPath careerPath,
            List<UserSkill> userSkills) {

        double readinessScore =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills
                );

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills
                );

        return new CareerPathResponse(
                careerPath.getId(),
                careerPath.getName(),
                careerPath.getDescription(),
                readinessScore,
                matchedSkills,
                missingSkills
        );
    }

    private CareerPathAlternativeResponse createAlternativeResponse(
            CareerPath careerPath,
            List<UserSkill> userSkills,
            Set<String> normalizedTargetSkills) {

        double readinessScore =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills
                );

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills
                );

        return new CareerPathAlternativeResponse(
                careerPath.getName(),
                readinessScore,
                "",
                matchedSkills,
                missingSkills
        );
    }

    private boolean hasMeaningfulOverlap(
            List<String> matchedSkills,
            Set<String> normalizedTargetSkills) {

        if (matchedSkills == null ||
                matchedSkills.isEmpty() ||
                normalizedTargetSkills.isEmpty()) {

            return false;
        }

        for (String matchedSkill : matchedSkills) {

            if (matchedSkill != null &&
                    normalizedTargetSkills.contains(
                            matchedSkill.trim().toLowerCase()
                    )) {

                return true;
            }
        }

        return false;
    }

    private Set<String> normalizeSkills(
            List<String> skills) {

        Set<String> normalizedSkills =
                new HashSet<>();

        if (skills == null) {
            return normalizedSkills;
        }

        for (String skill : skills) {

            if (skill != null &&
                    !skill.isBlank()) {

                normalizedSkills.add(
                        skill.trim().toLowerCase()
                );
            }
        }

        return normalizedSkills;
    }
}