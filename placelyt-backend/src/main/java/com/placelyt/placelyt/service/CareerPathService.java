package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CareerDirectionResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.CareerPathSkill;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.CareerPathSkillRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CareerPathService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final CareerPathRepository careerPathRepository;
    private final CareerPathSkillRepository careerPathSkillRepository;
    private final CareerPathEngine careerPathEngine;

    public CareerPathService(
            StudentProfileRepository studentProfileRepository,
            UserSkillRepository userSkillRepository,
            CareerPathRepository careerPathRepository,
            CareerPathSkillRepository careerPathSkillRepository,
            CareerPathEngine careerPathEngine) {

        this.studentProfileRepository =
                studentProfileRepository;

        this.userSkillRepository =
                userSkillRepository;

        this.careerPathRepository =
                careerPathRepository;

        this.careerPathSkillRepository =
                careerPathSkillRepository;

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

        Map<Long, List<CareerPathSkill>> careerPathSkills =
                loadCareerPathSkills(careerPaths);

        return careerPaths.stream()
                .map(careerPath ->
                        createResponse(
                                careerPath,
                                userSkills,
                                careerPathSkills.getOrDefault(
                                        careerPath.getId(),
                                        List.of()
                                )
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

        List<CareerPath> careerPaths =
                careerPathRepository.findAll();

        Map<Long, List<CareerPathSkill>> careerPathSkills =
                loadCareerPathSkills(careerPaths);

        List<CareerPathSkill> targetRequiredSkills =
                careerPathSkills.getOrDefault(
                        targetCareerPathId,
                        List.of()
                );

        List<String> targetMatchedSkills =
                careerPathEngine.getMatchedSkills(
                        targetCareerPath,
                        userSkills,
                        targetRequiredSkills
                );

        Set<String> normalizedTargetSkills =
                normalizeSkills(targetMatchedSkills);

        return careerPaths.stream()
                .filter(careerPath ->
                        !careerPath.getId()
                                .equals(targetCareerPathId))
                .map(careerPath ->
                        createAlternativeResponse(
                                careerPath,
                                userSkills,
                                normalizedTargetSkills,
                                careerPathSkills.getOrDefault(
                                        careerPath.getId(),
                                        List.of()
                                )
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

    public CareerDirectionResponse getCareerDirection(
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

        List<CareerPath> careerPaths =
                careerPathRepository.findAll();

        Map<Long, List<CareerPathSkill>> careerPathSkills =
                loadCareerPathSkills(careerPaths);

        CareerPath currentCareerPath =
                careerPaths.stream()
                        .max(
                                Comparator.comparingDouble(
                                        careerPath ->
                                                careerPathEngine
                                                        .calculateReadiness(
                                                                careerPath,
                                                                userSkills,
                                                                careerPathSkills
                                                                        .getOrDefault(
                                                                                careerPath.getId(),
                                                                                List.of()
                                                                        )
                                                        )
                                )
                        )
                        .orElse(null);

        if (currentCareerPath == null) {
            return new CareerDirectionResponse(
                    null,
                    createResponse(
                            targetCareerPath,
                            userSkills,
                            careerPathSkills.getOrDefault(
                                    targetCareerPathId,
                                    List.of()
                            )
                    )
            );
        }

        CareerPathResponse currentDirection =
                createResponse(
                        currentCareerPath,
                        userSkills,
                        careerPathSkills.getOrDefault(
                                currentCareerPath.getId(),
                                List.of()
                        )
                );

        CareerPathResponse targetDirection =
                createResponse(
                        targetCareerPath,
                        userSkills,
                        careerPathSkills.getOrDefault(
                                targetCareerPathId,
                                List.of()
                        )
                );

        return new CareerDirectionResponse(
                currentDirection,
                targetDirection
        );
    }

    private CareerPathResponse createResponse(
            CareerPath careerPath,
            List<UserSkill> userSkills,
            List<CareerPathSkill> requiredSkills) {

        double readinessScore =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills,
                        requiredSkills
                );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills,
                        requiredSkills
                );

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills,
                        requiredSkills
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
            Set<String> normalizedTargetSkills,
            List<CareerPathSkill> requiredSkills) {

        double readinessScore =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills,
                        requiredSkills
                );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills,
                        requiredSkills
                );

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills,
                        requiredSkills
                );

        return new CareerPathAlternativeResponse(
                careerPath.getName(),
                readinessScore,
                "",
                matchedSkills,
                missingSkills
        );
    }

    private Map<Long, List<CareerPathSkill>> loadCareerPathSkills(
            List<CareerPath> careerPaths) {

        if (careerPaths == null ||
                careerPaths.isEmpty()) {

            return Map.of();
        }

        List<Long> careerPathIds =
                careerPaths.stream()
                        .map(CareerPath::getId)
                        .collect(Collectors.toList());

        return careerPathSkillRepository
                .findByCareerPathIdIn(careerPathIds)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                careerPathSkill ->
                                        careerPathSkill
                                                .getCareerPath()
                                                .getId(),
                                HashMap::new,
                                Collectors.toList()
                        )
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