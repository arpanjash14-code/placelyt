package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.CareerPath;
import com.placelyt.placelyt.entity.CareerPathSkill;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.repository.CareerPathSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerPathEngineTest {

    @Mock
    private CareerPathSkillRepository careerPathSkillRepository;

    private CareerPathEngine careerPathEngine;

    @BeforeEach
    void setUp() {
        careerPathEngine =
                new CareerPathEngine(careerPathSkillRepository);
    }

    @Test
    void shouldCalculate100PercentReadinessWhenAllSkillsMatch() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");
        Skill sql = createSkill("SQL");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, springBoot),
                        createCareerPathSkill(careerPath, sql)
                ));

        List<UserSkill> userSkills = List.of(
                createUserSkill(java),
                createUserSkill(springBoot),
                createUserSkill(sql)
        );

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        assertEquals(100.0, readiness);
    }

    @Test
    void shouldCalculatePartialReadinessWhenSomeSkillsMatch() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");
        Skill sql = createSkill("SQL");
        Skill docker = createSkill("Docker");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, springBoot),
                        createCareerPathSkill(careerPath, sql),
                        createCareerPathSkill(careerPath, docker)
                ));

        List<UserSkill> userSkills = List.of(
                createUserSkill(java),
                createUserSkill(springBoot)
        );

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        assertEquals(50.0, readiness);
    }

    @Test
    void shouldCalculateZeroReadinessWhenNoSkillsMatch() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, springBoot)
                ));

        Skill python = createSkill("Python");

        List<UserSkill> userSkills =
                List.of(createUserSkill(python));

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        assertEquals(0.0, readiness);
    }

    @Test
    void shouldReturnAllMatchedSkills() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");
        Skill sql = createSkill("SQL");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, springBoot),
                        createCareerPathSkill(careerPath, sql)
                ));

        List<UserSkill> userSkills = List.of(
                createUserSkill(java),
                createUserSkill(sql)
        );

        List<String> matchedSkills =
                careerPathEngine.getMatchedSkills(
                        careerPath,
                        userSkills
                );

        assertEquals(
                List.of("Java", "SQL"),
                matchedSkills
        );
    }

    @Test
    void shouldReturnAllMissingSkills() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");
        Skill sql = createSkill("SQL");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, springBoot),
                        createCareerPathSkill(careerPath, sql)
                ));

        List<UserSkill> userSkills =
                List.of(createUserSkill(java));

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        userSkills
                );

        assertEquals(
                List.of("Spring Boot", "SQL"),
                missingSkills
        );
    }

    @Test
    void shouldMatchSkillsCaseInsensitively() {

        CareerPath careerPath = createCareerPath(1L);

        Skill requiredJava = createSkill("Java");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(
                                careerPath,
                                requiredJava
                        )
                ));

        Skill userJava = createSkill("java");

        List<UserSkill> userSkills =
                List.of(createUserSkill(userJava));

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        assertEquals(100.0, readiness);
    }

    @Test
    void shouldReturnZeroReadinessWhenUserHasNoSkills() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(
                                careerPath,
                                java
                        )
                ));

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        List.of()
                );

        assertEquals(0.0, readiness);
    }

    @Test
void shouldReturnEmptyMatchedSkillsWhenUserHasNoSkills() {

    CareerPath careerPath = createCareerPath(1L);

    List<String> matchedSkills =
            careerPathEngine.getMatchedSkills(
                    careerPath,
                    List.of()
            );

    assertTrue(matchedSkills.isEmpty());
}

    @Test
    void shouldReturnAllSkillsAsMissingWhenUserHasNoSkills() {

        CareerPath careerPath = createCareerPath(1L);

        Skill java = createSkill("Java");
        Skill sql = createSkill("SQL");

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of(
                        createCareerPathSkill(careerPath, java),
                        createCareerPathSkill(careerPath, sql)
                ));

        List<String> missingSkills =
                careerPathEngine.getMissingSkills(
                        careerPath,
                        List.of()
                );

        assertEquals(
                List.of("Java", "SQL"),
                missingSkills
        );
    }

    @Test
    void shouldReturnZeroReadinessWhenCareerPathHasNoRequiredSkills() {

        CareerPath careerPath = createCareerPath(1L);

        when(careerPathSkillRepository.findByCareerPathId(1L))
                .thenReturn(List.of());

        Skill java = createSkill("Java");

        List<UserSkill> userSkills =
                List.of(createUserSkill(java));

        double readiness =
                careerPathEngine.calculateReadiness(
                        careerPath,
                        userSkills
                );

        assertEquals(0.0, readiness);
    }

    private CareerPath createCareerPath(Long id) {

        CareerPath careerPath = new CareerPath();
        careerPath.setId(id);
        careerPath.setName("Backend Engineering");

        return careerPath;
    }

    private Skill createSkill(String name) {

        Skill skill = new Skill();
        skill.setName(name);

        return skill;
    }

    private CareerPathSkill createCareerPathSkill(
            CareerPath careerPath,
            Skill skill) {

        CareerPathSkill careerPathSkill =
                new CareerPathSkill();

        careerPathSkill.setCareerPath(careerPath);
        careerPathSkill.setSkill(skill);

        return careerPathSkill;
    }

    private UserSkill createUserSkill(Skill skill) {

        UserSkill userSkill = new UserSkill();
        userSkill.setSkill(skill);

        return userSkill;
    }
}