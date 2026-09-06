package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.ExperienceResponse;
import com.placelyt.placelyt.entity.Experience;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.ExperienceRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceService(
            ExperienceRepository experienceRepository,
            UserRepository userRepository) {

        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    public ExperienceResponse createExperience(
            Long userId,
            Experience experience) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        experience.setUser(user);

        Experience savedExperience =
                experienceRepository.save(experience);

        return toResponse(savedExperience);
    }

    public List<ExperienceResponse> getExperiencesByUserId(
            Long userId) {

        return experienceRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ExperienceResponse updateExperience(
            Long userId,
            Long experienceId,
            Experience updatedExperience) {

        Experience existingExperience =
                experienceRepository.findById(experienceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Experience not found"
                                ));

        if (!existingExperience.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Experience does not belong to this user"
            );
        }

        existingExperience.setCompanyName(
                updatedExperience.getCompanyName()
        );

        existingExperience.setJobTitle(
                updatedExperience.getJobTitle()
        );

        existingExperience.setEmploymentType(
                updatedExperience.getEmploymentType()
        );

        existingExperience.setStartDate(
                updatedExperience.getStartDate()
        );

        existingExperience.setEndDate(
                updatedExperience.getEndDate()
        );

        existingExperience.setCurrentlyWorking(
                updatedExperience.getCurrentlyWorking()
        );

        existingExperience.setDescription(
                updatedExperience.getDescription()
        );

        Experience savedExperience =
                experienceRepository.save(existingExperience);

        return toResponse(savedExperience);
    }

    public void deleteExperience(
            Long userId,
            Long experienceId) {

        Experience experience =
                experienceRepository.findById(experienceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Experience not found"
                                ));

        if (!experience.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Experience does not belong to this user"
            );
        }

        experienceRepository.delete(experience);
    }

    private ExperienceResponse toResponse(
            Experience experience) {

        return new ExperienceResponse(
                experience.getId(),
                experience.getUser().getId(),
                experience.getCompanyName(),
                experience.getJobTitle(),
                experience.getEmploymentType(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.getCurrentlyWorking(),
                experience.getDescription()
        );
    }
}