package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.EducationResponse;
import com.placelyt.placelyt.entity.Education;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.EducationRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationService(
            EducationRepository educationRepository,
            UserRepository userRepository) {
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

    public EducationResponse createEducation(
            Long userId,
            Education education) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        education.setUser(user);

        Education savedEducation =
                educationRepository.save(education);

        return toResponse(savedEducation);
    }

    public List<EducationResponse> getEducationByUserId(Long userId) {

        return educationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EducationResponse updateEducation(
            Long userId,
            Long educationId,
            Education updatedEducation) {

        Education existingEducation =
                educationRepository.findById(educationId)
                        .orElseThrow(() ->
                                new RuntimeException("Education not found"));

        if (!existingEducation.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Education does not belong to this user"
            );
        }

        existingEducation.setInstitution(
                updatedEducation.getInstitution()
        );

        existingEducation.setDegree(
                updatedEducation.getDegree()
        );

        existingEducation.setFieldOfStudy(
                updatedEducation.getFieldOfStudy()
        );

        existingEducation.setStartYear(
                updatedEducation.getStartYear()
        );

        existingEducation.setEndYear(
                updatedEducation.getEndYear()
        );

        existingEducation.setCgpa(
                updatedEducation.getCgpa()
        );

        Education savedEducation =
                educationRepository.save(existingEducation);

        return toResponse(savedEducation);
    }

    public void deleteEducation(
            Long userId,
            Long educationId) {

        Education education =
                educationRepository.findById(educationId)
                        .orElseThrow(() ->
                                new RuntimeException("Education not found"));

        if (!education.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Education does not belong to this user"
            );
        }

        educationRepository.delete(education);
    }

    private EducationResponse toResponse(Education education) {

        return new EducationResponse(
                education.getId(),
                education.getUser().getId(),
                education.getInstitution(),
                education.getDegree(),
                education.getFieldOfStudy(),
                education.getStartYear(),
                education.getEndYear(),
                education.getCgpa()
        );
    }
}