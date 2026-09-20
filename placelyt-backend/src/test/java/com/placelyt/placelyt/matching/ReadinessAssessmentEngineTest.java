package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadinessAssessmentEngineTest {

    private ReadinessAssessmentEngine engine;

   @BeforeEach
void setUp() {
    engine = new ReadinessAssessmentEngine(
            new SkillMatchingEngine(),
            new AcademicEligibilityEngine(),
            new ExperienceMatchingEngine(
                    new SkillMatchingEngine()
            )
    );
}

    // ---------------------------------------------------------
    // Academic Readiness
    // ---------------------------------------------------------

    @Test
    void academicReadinessShouldBe25WhenRequirementsAreSatisfied() {
        StudentProfile student = createCompleteStudent();

        Job job = new Job();
        job.setMinimumCgpa(8.0);
        job.setEligibleGraduationYear(2027);
        job.setRequiredDegree("B.Tech");

        double result =
                engine.calculateAcademicReadiness(student, job);

        assertEquals(25.0, result, 0.001);
    }

    @Test
    void academicReadinessShouldBeZeroWhenCgpaRequirementFails() {
        StudentProfile student = createCompleteStudent();
        student.setCgpa(7.0);

        Job job = new Job();
        job.setMinimumCgpa(8.0);

        double result =
                engine.calculateAcademicReadiness(student, job);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void academicReadinessShouldBeZeroWhenGraduationYearFails() {
        StudentProfile student = createCompleteStudent();

        Job job = new Job();
        job.setEligibleGraduationYear(2026);

        double result =
                engine.calculateAcademicReadiness(student, job);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void academicReadinessShouldBeZeroWhenDegreeFails() {
        StudentProfile student = createCompleteStudent();

        Job job = new Job();
        job.setRequiredDegree("M.Tech");

        double result =
                engine.calculateAcademicReadiness(student, job);

        assertEquals(0.0, result, 0.001);
    }

    // ---------------------------------------------------------
    // Skill Readiness
    // ---------------------------------------------------------

    @Test
    void skillReadinessShouldBe40WhenAllSkillsMatch() {
        Skill java = createSkill("Java");
        Skill python = createSkill("Python");

        Job job = new Job();
        job.setRequiredSkills(
                createRequiredSkills(java, python)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 1.0),
                createUserSkill(python, 1.0)
        );

        double result =
                engine.calculateSkillReadiness(userSkills, job);

        assertEquals(40.0, result, 0.001);
    }

    @Test
    void skillReadinessShouldBe20WhenHalfSkillsMatch() {
        Skill java = createSkill("Java");
        Skill python = createSkill("Python");

        Job job = new Job();
        job.setRequiredSkills(
                createRequiredSkills(java, python)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 1.0)
        );

        double result =
                engine.calculateSkillReadiness(userSkills, job);

        assertEquals(20.0, result, 0.001);
    }

    @Test
    void skillReadinessShouldBeZeroWhenNoSkillsMatch() {
        Skill java = createSkill("Java");
        Skill python = createSkill("Python");

        Job job = new Job();
        job.setRequiredSkills(
                createRequiredSkills(java, python)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        createSkill("JavaScript"),
                        1.0
                )
        );

        double result =
                engine.calculateSkillReadiness(userSkills, job);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void skillReadinessShouldBe40WhenJobHasNoRequiredSkills() {
        Job job = new Job();
        job.setRequiredSkills(new ArrayList<>());

        List<UserSkill> userSkills = new ArrayList<>();

        double result =
                engine.calculateSkillReadiness(userSkills, job);

        assertEquals(40.0, result, 0.001);
    }

    // ---------------------------------------------------------
    // Experience Readiness
    // ---------------------------------------------------------

    @Test
    void experienceReadinessShouldBe20WhenRequirementIsMet() {
        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumYearsOfExperience(1.0);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 1.0)
        );

        double result =
                engine.calculateExperienceReadiness(
                        userSkills,
                        job
                );

        assertEquals(20.0, result, 0.001);
    }

    @Test
    void experienceReadinessShouldBe20WhenExperienceExceedsRequirement() {
        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumYearsOfExperience(1.0);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 3.0)
        );

        double result =
                engine.calculateExperienceReadiness(
                        userSkills,
                        job
                );

        assertEquals(20.0, result, 0.001);
    }

    @Test
    void experienceReadinessShouldBe10WhenExperienceIsHalfTheRequirement() {
        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumYearsOfExperience(1.0);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 0.5)
        );

        double result =
                engine.calculateExperienceReadiness(
                        userSkills,
                        job
                );

        assertEquals(10.0, result, 0.001);
    }

    @Test
    void experienceReadinessShouldBe20WhenThereIsNoExperienceRequirement() {
        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumYearsOfExperience(null);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = new ArrayList<>();

        double result =
                engine.calculateExperienceReadiness(
                        userSkills,
                        job
                );

        assertEquals(20.0, result, 0.001);
    }

    @Test
    void experienceReadinessShouldBeZeroWhenNoRelevantExperienceExists() {
        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumYearsOfExperience(1.0);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        createSkill("Python"),
                        2.0
                )
        );

        double result =
                engine.calculateExperienceReadiness(
                        userSkills,
                        job
                );

        assertEquals(0.0, result, 0.001);
    }

    // ---------------------------------------------------------
    // Profile Completeness
    // ---------------------------------------------------------

    @Test
    void profileCompletenessShouldBe15WhenProfileIsComplete() {
        StudentProfile student = createCompleteStudent();

        double result =
                engine.calculateProfileCompleteness(student);

        assertEquals(15.0, result, 0.001);
    }

    @Test
    void profileCompletenessShouldBeZeroWhenProfileIsEmpty() {
        StudentProfile student = new StudentProfile();

        double result =
                engine.calculateProfileCompleteness(student);

        assertEquals(0.0, result, 0.001);
    }

    // ---------------------------------------------------------
    // Overall Readiness Score
    // ---------------------------------------------------------

    @Test
    void overallReadinessShouldBe100WhenStudentIsFullyReady() {
        StudentProfile student = createCompleteStudent();

        Skill java = createSkill("Java");

        Job job = new Job();
        job.setMinimumCgpa(8.0);
        job.setEligibleGraduationYear(2027);
        job.setRequiredDegree("B.Tech");
        job.setMinimumYearsOfExperience(1.0);
        job.setRequiredSkills(
                createRequiredSkills(java)
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(java, 1.0)
        );

        double result =
                engine.calculateReadinessScore(
                        student,
                        userSkills,
                        job
                );

        assertEquals(100.0, result, 0.001);
    }

    // ---------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------

    private StudentProfile createCompleteStudent() {
        StudentProfile student = new StudentProfile();

        student.setFullName("Arpan Jash");
        student.setCollege("Example College");
        student.setDegree("B.Tech");
        student.setBranch("CSE");
        student.setGraduationYear(2027);
        student.setCgpa(8.5);
        student.setLocation("Kolkata");

        return student;
    }

    private Skill createSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        return skill;
    }

    private UserSkill createUserSkill(
            Skill skill,
            double yearsOfExperience) {

        UserSkill userSkill = new UserSkill();

        userSkill.setSkill(skill);
        userSkill.setProficiency("Intermediate");
        userSkill.setYearsOfExperience(
                yearsOfExperience
        );

        return userSkill;
    }

    private List<JobRequiredSkill> createRequiredSkills(
            Skill... skills) {

        List<JobRequiredSkill> requiredSkills =
                new ArrayList<>();

        for (Skill skill : skills) {
            JobRequiredSkill requiredSkill =
                    new JobRequiredSkill();

            requiredSkill.setSkill(skill);

            requiredSkills.add(requiredSkill);
        }

        return requiredSkills;
    }
}