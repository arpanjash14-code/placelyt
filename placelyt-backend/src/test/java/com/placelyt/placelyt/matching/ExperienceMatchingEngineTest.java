package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.UserSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceMatchingEngineTest {

    private ExperienceMatchingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ExperienceMatchingEngine(
                new SkillMatchingEngine()
        );
    }

    @Test
    void shouldCalculateAverageRelevantExperience() {

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");

        Job job = createJobWithRequiredSkills(
                java,
                springBoot
        );

        UserSkill javaUserSkill =
                createUserSkill(java, 2.0);

        UserSkill springBootUserSkill =
                createUserSkill(springBoot, 4.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(
                                javaUserSkill,
                                springBootUserSkill
                        ),
                        job
                );

        assertNotNull(result);
        assertEquals(3.0, result);
    }

    @Test
    void shouldIgnoreUnrelatedSkills() {

        Skill java = createSkill("Java");
        Skill springBoot = createSkill("Spring Boot");
        Skill python = createSkill("Python");

        Job job = createJobWithRequiredSkills(
                java,
                springBoot
        );

        UserSkill javaUserSkill =
                createUserSkill(java, 2.0);

        UserSkill springBootUserSkill =
                createUserSkill(springBoot, 4.0);

        UserSkill pythonUserSkill =
                createUserSkill(python, 10.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(
                                javaUserSkill,
                                springBootUserSkill,
                                pythonUserSkill
                        ),
                        job
                );

        assertNotNull(result);
        assertEquals(3.0, result);
    }

    @Test
    void shouldTreatZeroYearsOfExperienceAsValid() {

        Skill java = createSkill("Java");

        Job job =
                createJobWithRequiredSkills(java);

        UserSkill javaUserSkill =
                createUserSkill(java, 0.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(javaUserSkill),
                        job
                );

        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    void shouldReturnNullWhenThereIsNoRelevantExperience() {

        Skill java = createSkill("Java");
        Skill python = createSkill("Python");

        Job job =
                createJobWithRequiredSkills(java);

        UserSkill pythonUserSkill =
                createUserSkill(python, 5.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(pythonUserSkill),
                        job
                );

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenUserSkillsAreEmpty() {

        Skill java = createSkill("Java");

        Job job =
                createJobWithRequiredSkills(java);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(),
                        job
                );

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenUserSkillsAreNull() {

        Skill java = createSkill("Java");

        Job job =
                createJobWithRequiredSkills(java);

        Double result =
                engine.calculateAverageRelevantExperience(
                        null,
                        job
                );

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenJobIsNull() {

        Skill java = createSkill("Java");

        UserSkill javaUserSkill =
                createUserSkill(java, 2.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(javaUserSkill),
                        null
                );

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenJobHasNoRequiredSkills() {

        Job job = new Job();

        Skill java = createSkill("Java");

        UserSkill javaUserSkill =
                createUserSkill(java, 2.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(javaUserSkill),
                        job
                );

        assertNull(result);
    }

    @Test
    void shouldIgnoreNegativeExperienceValues() {

        Skill java = createSkill("Java");

        Job job =
                createJobWithRequiredSkills(java);

        UserSkill javaUserSkill =
                createUserSkill(java, -1.0);

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(javaUserSkill),
                        job
                );

        assertNull(result);
    }

    @Test
    void shouldMatchSkillNamesCaseInsensitively() {

        Skill java =
                createSkill("Java");

        Job job =
                createJobWithRequiredSkills(java);

        UserSkill javaUserSkill =
                createUserSkillWithSkillName(
                        "java",
                        3.0
                );

        Double result =
                engine.calculateAverageRelevantExperience(
                        List.of(javaUserSkill),
                        job
                );

        assertNotNull(result);
        assertEquals(3.0, result);
    }

    private Skill createSkill(String name) {

        Skill skill = new Skill();

        skill.setName(name);

        return skill;
    }

    private UserSkill createUserSkill(
            Skill skill,
            double yearsOfExperience) {

        UserSkill userSkill =
                new UserSkill();

        userSkill.setSkill(skill);
        userSkill.setYearsOfExperience(
                yearsOfExperience
        );

        return userSkill;
    }

    private UserSkill createUserSkillWithSkillName(
            String skillName,
            double yearsOfExperience) {

        Skill skill =
                createSkill(skillName);

        return createUserSkill(
                skill,
                yearsOfExperience
        );
    }

    private Job createJobWithRequiredSkills(
            Skill... skills) {

        Job job = new Job();

        for (Skill skill : skills) {

            JobRequiredSkill requiredSkill =
                    new JobRequiredSkill();

            requiredSkill.setJob(job);
            requiredSkill.setSkill(skill);

            job.getRequiredSkills()
                    .add(requiredSkill);
        }

        return job;
    }
}