package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CareerPathServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private CareerPathRepository careerPathRepository;

    @Mock
    private CareerPathEngine careerPathEngine;

    private CareerPathService careerPathService;

    @BeforeEach
    void setUp() {
        careerPathService =
                new CareerPathService(
                        studentProfileRepository,
                        userSkillRepository,
                        careerPathRepository,
                        careerPathEngine
                );
    }

    @Test
    void shouldReturnCareerPathsSortedByReadiness() {

        CareerPath backend =
                createCareerPath(
                        1L,
                        "Backend Engineering",
                        "Backend career path"
                );

        CareerPath frontend =
                createCareerPath(
                        2L,
                        "Frontend Engineering",
                        "Frontend career path"
                );

        CareerPath fullStack =
                createCareerPath(
                        3L,
                        "Full Stack Development",
                        "Full stack career path"
                );

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        frontend,
                        fullStack
                ));

        when(careerPathEngine.calculateReadiness(
                backend,
                userSkills
        )).thenReturn(40.0);

        when(careerPathEngine.calculateReadiness(
                frontend,
                userSkills
        )).thenReturn(80.0);

        when(careerPathEngine.calculateReadiness(
                fullStack,
                userSkills
        )).thenReturn(60.0);

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.getMatchedSkills(
                frontend,
                userSkills
        )).thenReturn(List.of("React"));

        when(careerPathEngine.getMatchedSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of("Java", "React"));

        when(careerPathEngine.getMissingSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Spring Boot"));

        when(careerPathEngine.getMissingSkills(
                frontend,
                userSkills
        )).thenReturn(List.of("CSS"));

        when(careerPathEngine.getMissingSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of("SQL"));

        List<CareerPathResponse> responses =
                careerPathService.getCareerPaths(4L);

        assertEquals(3, responses.size());

        assertEquals(
                "Frontend Engineering",
                responses.get(0).getCareerPathName()
        );

        assertEquals(
                80.0,
                responses.get(0).getReadinessScore()
        );

        assertEquals(
                "Full Stack Development",
                responses.get(1).getCareerPathName()
        );

        assertEquals(
                60.0,
                responses.get(1).getReadinessScore()
        );

        assertEquals(
                "Backend Engineering",
                responses.get(2).getCareerPathName()
        );

        assertEquals(
                40.0,
                responses.get(2).getReadinessScore()
        );
    }

    @Test
    void shouldBuildResponseWithMatchedAndMissingSkills() {

        CareerPath backend =
                createCareerPath(
                        1L,
                        "Backend Engineering",
                        "Backend career path"
                );

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findAll())
                .thenReturn(List.of(backend));

        when(careerPathEngine.calculateReadiness(
                backend,
                userSkills
        )).thenReturn(20.0);

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.getMissingSkills(
                backend,
                userSkills
        )).thenReturn(List.of(
                "Spring Boot",
                "SQL"
        ));

        List<CareerPathResponse> responses =
                careerPathService.getCareerPaths(4L);

        assertEquals(1, responses.size());

        CareerPathResponse response =
                responses.get(0);

        assertEquals(
                1L,
                response.getCareerPathId()
        );

        assertEquals(
                "Backend Engineering",
                response.getCareerPathName()
        );

        assertEquals(
                "Backend career path",
                response.getDescription()
        );

        assertEquals(
                20.0,
                response.getReadinessScore()
        );

        assertEquals(
                List.of("Java"),
                response.getMatchedSkills()
        );

        assertEquals(
                List.of("Spring Boot", "SQL"),
                response.getMissingSkills()
        );
    }

    @Test
    void shouldThrowExceptionWhenStudentProfileDoesNotExist() {

        when(studentProfileRepository.findByUserId(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> careerPathService
                                .getCareerPaths(999L)
                );

        assertEquals(
                "Student profile not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoCareerPathsExist() {

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findAll())
                .thenReturn(List.of());

        List<CareerPathResponse> responses =
                careerPathService.getCareerPaths(4L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(careerPathRepository).findAll();
    }

    private CareerPath createCareerPath(
            Long id,
            String name,
            String description) {

        CareerPath careerPath =
                new CareerPath();

        careerPath.setId(id);
        careerPath.setName(name);
        careerPath.setDescription(description);

        return careerPath;
    }
}