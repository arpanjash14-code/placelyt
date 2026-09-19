package com.placelyt.placelyt.service;

import com.placelyt.placelyt.dto.CareerDirectionResponse;
import com.placelyt.placelyt.dto.CareerPathAlternativeResponse;
import com.placelyt.placelyt.dto.CareerPathResponse;
import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.matching.CareerPathEngine;
import com.placelyt.placelyt.repository.CareerPathRepository;
import com.placelyt.placelyt.repository.StudentProfileRepository;
import com.placelyt.placelyt.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldReturnRelevantCareerPathAlternatives() {

        CareerPath backend =
                createCareerPath(
                        1L,
                        "Backend Engineering",
                        "Backend career path"
                );

        CareerPath fullStack =
                createCareerPath(
                        2L,
                        "Full Stack Development",
                        "Full stack career path"
                );

        CareerPath dataEngineering =
                createCareerPath(
                        3L,
                        "Data Engineering",
                        "Data engineering career path"
                );

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(backend));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        fullStack,
                        dataEngineering
                ));

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of(
                "Java",
                "SQL"
        ));

        when(careerPathEngine.calculateReadiness(
                fullStack,
                userSkills
        )).thenReturn(50.0);

        when(careerPathEngine.getMatchedSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of(
                "Java",
                "SQL"
        ));

        when(careerPathEngine.getMissingSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of(
                "React"
        ));

        when(careerPathEngine.calculateReadiness(
                dataEngineering,
                userSkills
        )).thenReturn(25.0);

        when(careerPathEngine.getMatchedSkills(
                dataEngineering,
                userSkills
        )).thenReturn(List.of(
                "SQL"
        ));

        when(careerPathEngine.getMissingSkills(
                dataEngineering,
                userSkills
        )).thenReturn(List.of(
                "Python",
                "ETL"
        ));

        List<CareerPathAlternativeResponse> responses =
                careerPathService.getCareerPathAlternatives(
                        4L,
                        1L
                );

        assertEquals(2, responses.size());

        assertEquals(
                "Full Stack Development",
                responses.get(0).getCareerPathName()
        );

        assertEquals(
                50.0,
                responses.get(0).getReadinessScore()
        );

        assertEquals(
                List.of("Java", "SQL"),
                responses.get(0).getMatchedSkills()
        );

        assertEquals(
                List.of("React"),
                responses.get(0).getMissingSkills()
        );

        assertEquals(
                "Data Engineering",
                responses.get(1).getCareerPathName()
        );

        assertEquals(
                25.0,
                responses.get(1).getReadinessScore()
        );
    }

    @Test
    void shouldExcludeTargetCareerPathFromAlternatives() {

        CareerPath backend =
                createCareerPath(
                        1L,
                        "Backend Engineering",
                        "Backend career path"
                );

        CareerPath fullStack =
                createCareerPath(
                        2L,
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

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(backend));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        fullStack
                ));

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.calculateReadiness(
                fullStack,
                userSkills
        )).thenReturn(40.0);

        when(careerPathEngine.getMatchedSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.getMissingSkills(
                fullStack,
                userSkills
        )).thenReturn(List.of("React"));

        List<CareerPathAlternativeResponse> responses =
                careerPathService.getCareerPathAlternatives(
                        4L,
                        1L
                );

        assertEquals(1, responses.size());

        assertEquals(
                "Full Stack Development",
                responses.get(0).getCareerPathName()
        );

        assertFalse(
                responses.stream()
                        .anyMatch(response ->
                                response.getCareerPathName()
                                        .equals("Backend Engineering")
                        )
        );
    }

    @Test
    void shouldExcludeCareerPathsWithoutMeaningfulSkillOverlap() {

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

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(backend));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        frontend
                ));

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.calculateReadiness(
                frontend,
                userSkills
        )).thenReturn(75.0);

        when(careerPathEngine.getMatchedSkills(
                frontend,
                userSkills
        )).thenReturn(List.of("React"));

        when(careerPathEngine.getMissingSkills(
                frontend,
                userSkills
        )).thenReturn(List.of("CSS"));

        List<CareerPathAlternativeResponse> responses =
                careerPathService.getCareerPathAlternatives(
                        4L,
                        1L
                );

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenTargetCareerPathDoesNotExist() {

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        when(careerPathRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> careerPathService
                                .getCareerPathAlternatives(
                                        4L,
                                        999L
                                )
                );

        assertEquals(
                "Target career path not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoMeaningfulAlternativesExist() {

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

        when(careerPathRepository.findById(1L))
                .thenReturn(Optional.of(backend));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(backend));

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        List<CareerPathAlternativeResponse> responses =
                careerPathService.getCareerPathAlternatives(
                        4L,
                        1L
                );

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void shouldDetermineCurrentAndTargetCareerDirection() {

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

        CareerPath machineLearning =
                createCareerPath(
                        5L,
                        "Machine Learning Engineering",
                        "Machine learning career path"
                );

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        List<UserSkill> userSkills = List.of();

        when(userSkillRepository.findByUserId(4L))
                .thenReturn(userSkills);

        when(careerPathRepository.findById(5L))
                .thenReturn(Optional.of(machineLearning));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        frontend,
                        machineLearning
                ));

        when(careerPathEngine.calculateReadiness(
                backend,
                userSkills
        )).thenReturn(60.0);

        when(careerPathEngine.calculateReadiness(
                frontend,
                userSkills
        )).thenReturn(30.0);

        when(careerPathEngine.calculateReadiness(
                machineLearning,
                userSkills
        )).thenReturn(20.0);

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of(
                "Java",
                "Spring Boot",
                "SQL"
        ));

        when(careerPathEngine.getMissingSkills(
                backend,
                userSkills
        )).thenReturn(List.of(
                "REST APIs",
                "Docker"
        ));

        when(careerPathEngine.getMatchedSkills(
                machineLearning,
                userSkills
        )).thenReturn(List.of(
                "Python"
        ));

        when(careerPathEngine.getMissingSkills(
                machineLearning,
                userSkills
        )).thenReturn(List.of(
                "Machine Learning",
                "Statistics",
                "TensorFlow",
                "PyTorch"
        ));

        CareerDirectionResponse response =
                careerPathService.getCareerDirection(
                        4L,
                        5L
                );

        assertNotNull(response);

        assertEquals(
                "Backend Engineering",
                response.getCurrentDirection()
                        .getCareerPathName()
        );

        assertEquals(
                60.0,
                response.getCurrentDirection()
                        .getReadinessScore()
        );

        assertEquals(
                "Machine Learning Engineering",
                response.getTargetDirection()
                        .getCareerPathName()
        );

        assertEquals(
                20.0,
                response.getTargetDirection()
                        .getReadinessScore()
        );

        assertEquals(
                List.of(
                        "Java",
                        "Spring Boot",
                        "SQL"
                ),
                response.getCurrentDirection()
                        .getMatchedSkills()
        );

        assertEquals(
                List.of(
                        "Machine Learning",
                        "Statistics",
                        "TensorFlow",
                        "PyTorch"
                ),
                response.getTargetDirection()
                        .getMissingSkills()
        );
    }

    @Test
    void shouldThrowExceptionWhenStudentProfileDoesNotExistForCareerDirection() {

        when(studentProfileRepository.findByUserId(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> careerPathService.getCareerDirection(
                                999L,
                                5L
                        )
                );

        assertEquals(
                "Student profile not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenTargetCareerPathDoesNotExistForCareerDirection() {

        when(studentProfileRepository.findByUserId(4L))
                .thenReturn(Optional.of(
                        new com.placelyt.placelyt.entity.StudentProfile()
                ));

        when(careerPathRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> careerPathService.getCareerDirection(
                                4L,
                                999L
                        )
                );

        assertEquals(
                "Target career path not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldSelectHighestReadinessCareerPathAsCurrentDirection() {

        CareerPath dataEngineering =
                createCareerPath(
                        4L,
                        "Data Engineering",
                        "Data engineering career path"
                );

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

        when(careerPathRepository.findById(4L))
                .thenReturn(Optional.of(dataEngineering));

        when(careerPathRepository.findAll())
                .thenReturn(List.of(
                        backend,
                        dataEngineering
                ));

        when(careerPathEngine.calculateReadiness(
                backend,
                userSkills
        )).thenReturn(80.0);

        when(careerPathEngine.calculateReadiness(
                dataEngineering,
                userSkills
        )).thenReturn(25.0);

        when(careerPathEngine.getMatchedSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Java"));

        when(careerPathEngine.getMissingSkills(
                backend,
                userSkills
        )).thenReturn(List.of("Spring Boot"));

        when(careerPathEngine.getMatchedSkills(
                dataEngineering,
                userSkills
        )).thenReturn(List.of("SQL"));

        when(careerPathEngine.getMissingSkills(
                dataEngineering,
                userSkills
        )).thenReturn(List.of("Python"));

        CareerDirectionResponse response =
                careerPathService.getCareerDirection(
                        4L,
                        4L
                );

        assertEquals(
                "Backend Engineering",
                response.getCurrentDirection()
                        .getCareerPathName()
        );

        assertEquals(
                "Data Engineering",
                response.getTargetDirection()
                        .getCareerPathName()
        );
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