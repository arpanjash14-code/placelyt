package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.StudentProfileResponse;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.User;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentProfileService(
            StudentProfileRepository studentProfileRepository,
            UserRepository userRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    public StudentProfileResponse createProfile(Long userId, StudentProfile profile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StudentProfile> existingProfile =
                studentProfileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            throw new RuntimeException("Student profile already exists");
        }

        profile.setUser(user);

        StudentProfile savedProfile =
                studentProfileRepository.save(profile);

        return toResponse(savedProfile);
    }

    public StudentProfileResponse getProfileByUserId(Long userId) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return toResponse(profile);
    }

    public StudentProfileResponse updateProfile(
            Long userId,
            StudentProfile updatedProfile) {

        StudentProfile existingProfile = studentProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setCollege(updatedProfile.getCollege());
        existingProfile.setDegree(updatedProfile.getDegree());
        existingProfile.setBranch(updatedProfile.getBranch());
        existingProfile.setGraduationYear(updatedProfile.getGraduationYear());
        existingProfile.setCgpa(updatedProfile.getCgpa());
        existingProfile.setLocation(updatedProfile.getLocation());

        StudentProfile savedProfile =
                studentProfileRepository.save(existingProfile);

        return toResponse(savedProfile);
    }

    private StudentProfileResponse toResponse(StudentProfile profile) {

        return new StudentProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getFullName(),
                profile.getCollege(),
                profile.getDegree(),
                profile.getBranch(),
                profile.getGraduationYear(),
                profile.getCgpa(),
                profile.getLocation()
        );
    }
}