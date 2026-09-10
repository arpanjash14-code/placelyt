package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.ExperienceResponse;
import com.placelyt.placelyt.entity.Experience;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.exception.ExperienceNotFoundException;
import com.placelyt.placelyt.exception.ExperienceOwnershipException;
import com.placelyt.placelyt.exception.InvalidExperienceException;
import com.placelyt.placelyt.exception.UserNotFoundException;
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

        validateExperience(experience);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

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

        validateExperience(updatedExperience);

        Experience existingExperience =
                experienceRepository.findById(experienceId)
                        .orElseThrow(() ->
                                new ExperienceNotFoundException(
                                        "Experience not found"
                                ));

        if (!existingExperience.getUser().getId().equals(userId)) {
            throw new ExperienceOwnershipException(
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
                                new ExperienceNotFoundException(
                                        "Experience not found"
                                ));

        if (!experience.getUser().getId().equals(userId)) {
            throw new ExperienceOwnershipException(
                    "Experience does not belong to this user"
            );
        }

        experienceRepository.delete(experience);
    }

    private void validateExperience(Experience experience) {

        if (experience.getCompanyName() == null
                || experience.getCompanyName().isBlank()) {

            throw new InvalidExperienceException(
                    "Company name is required"
            );
        }

        if (experience.getJobTitle() == null
                || experience.getJobTitle().isBlank()) {

            throw new InvalidExperienceException(
                    "Job title is required"
            );
        }

        if (experience.getStartDate() != null
                && experience.getEndDate() != null
                && experience.getStartDate()
                        .isAfter(experience.getEndDate())) {

            throw new InvalidExperienceException(
                    "Start date cannot be after end date"
            );
        }

        if (Boolean.TRUE.equals(experience.getCurrentlyWorking())
                && experience.getEndDate() != null) {

            throw new InvalidExperienceException(
                    "End date must be empty when currently working"
            );
        }
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