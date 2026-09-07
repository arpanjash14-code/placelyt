package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.PreferenceResponse;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.PreferenceRepository;
import com.placelyt.placelyt.repository.UserRepository;
import com.placelyt.placelyt.exception.DuplicatePreferenceException;
import org.springframework.stereotype.Service;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public PreferenceService(
            PreferenceRepository preferenceRepository,
            UserRepository userRepository) {

        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public PreferenceResponse createPreference(
            Long userId,
            Preference preference) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (preferenceRepository.findByUserId(userId).isPresent()) {
    throw new DuplicatePreferenceException(
            "Preferences already exist for this user"
    );

        }

        preference.setUser(user);

        Preference savedPreference =
                preferenceRepository.save(preference);

        return toResponse(savedPreference);
    }

    public PreferenceResponse getPreferenceByUserId(
            Long userId) {

        Preference preference =
                preferenceRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Preferences not found"
                                ));

        return toResponse(preference);
    }

    public PreferenceResponse updatePreference(
            Long userId,
            Preference updatedPreference) {

        Preference existingPreference =
                preferenceRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Preferences not found"
                                ));

        existingPreference.setPreferredRole(
                updatedPreference.getPreferredRole()
        );

        existingPreference.setPreferredLocation(
                updatedPreference.getPreferredLocation()
        );

        existingPreference.setEmploymentType(
                updatedPreference.getEmploymentType()
        );

        existingPreference.setWorkMode(
                updatedPreference.getWorkMode()
        );

        existingPreference.setMinimumSalary(
                updatedPreference.getMinimumSalary()
        );

        existingPreference.setMaximumSalary(
                updatedPreference.getMaximumSalary()
        );

        existingPreference.setWillingToRelocate(
                updatedPreference.getWillingToRelocate()
        );

        Preference savedPreference =
                preferenceRepository.save(existingPreference);

        return toResponse(savedPreference);
    }

    public void deletePreference(Long userId) {

        Preference preference =
                preferenceRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Preferences not found"
                                ));

        preferenceRepository.delete(preference);
    }

    private PreferenceResponse toResponse(
            Preference preference) {

        return new PreferenceResponse(
                preference.getId(),
                preference.getUser().getId(),
                preference.getPreferredRole(),
                preference.getPreferredLocation(),
                preference.getEmploymentType(),
                preference.getWorkMode(),
                preference.getMinimumSalary(),
                preference.getMaximumSalary(),
                preference.getWillingToRelocate()
        );
    }
}