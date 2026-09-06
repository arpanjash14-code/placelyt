package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.UserSkillResponse;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.repository.SkillRepository;
import com.placelyt.placelyt.repository.UserRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import com.placelyt.placelyt.exception.DuplicateUserSkillException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillService(
            UserSkillRepository userSkillRepository,
            UserRepository userRepository,
            SkillRepository skillRepository) {

        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public UserSkillResponse addSkill(
            Long userId,
            Long skillId,
            UserSkill userSkill) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException("Skill not found"));

        if (userSkillRepository
                .findByUserIdAndSkillId(userId, skillId)
                .isPresent()) {

            throw new DuplicateUserSkillException(
        "User already has this skill"
);
        }

        userSkill.setUser(user);
        userSkill.setSkill(skill);

        UserSkill savedUserSkill =
                userSkillRepository.save(userSkill);

        return toResponse(savedUserSkill);
    }

    public List<UserSkillResponse> getUserSkills(Long userId) {

        return userSkillRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserSkillResponse updateSkill(
            Long userId,
            Long userSkillId,
            UserSkill updatedUserSkill) {

        UserSkill existingUserSkill =
                userSkillRepository.findById(userSkillId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User skill not found"
                                ));

        if (!existingUserSkill.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Skill does not belong to this user"
            );
        }

        existingUserSkill.setProficiency(
                updatedUserSkill.getProficiency()
        );

        existingUserSkill.setYearsOfExperience(
                updatedUserSkill.getYearsOfExperience()
        );

        UserSkill savedUserSkill =
                userSkillRepository.save(existingUserSkill);

        return toResponse(savedUserSkill);
    }

    public void removeSkill(
            Long userId,
            Long userSkillId) {

        UserSkill userSkill =
                userSkillRepository.findById(userSkillId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User skill not found"
                                ));

        if (!userSkill.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Skill does not belong to this user"
            );
        }

        userSkillRepository.delete(userSkill);
    }

    private UserSkillResponse toResponse(
            UserSkill userSkill) {

        return new UserSkillResponse(
                userSkill.getId(),
                userSkill.getUser().getId(),
                userSkill.getSkill().getId(),
                userSkill.getSkill().getName(),
                userSkill.getSkill().getCategory(),
                userSkill.getProficiency(),
                userSkill.getYearsOfExperience()
        );
    }
}