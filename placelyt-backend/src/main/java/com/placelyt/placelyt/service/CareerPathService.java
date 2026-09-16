package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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
}