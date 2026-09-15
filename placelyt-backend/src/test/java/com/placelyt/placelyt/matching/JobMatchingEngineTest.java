package com.placelyt.placelyt.matching;

import com.placelyt.placelyt.entity.EmploymentType;
import com.placelyt.placelyt.entity.Job;
import com.placelyt.placelyt.entity.JobEligibleBranch;
import com.placelyt.placelyt.entity.JobRequiredSkill;
import com.placelyt.placelyt.entity.Preference;
import com.placelyt.placelyt.entity.Skill;
import com.placelyt.placelyt.entity.StudentProfile;
import com.placelyt.placelyt.entity.UserSkill;
import com.placelyt.placelyt.entity.WorkMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobMatchingEngineTest {

    private final JobMatchingEngine engine =
            new JobMatchingEngine();

    @Test
    void shouldBeEligibleWhenAllRequirementsMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                8.0
        );

        assertTrue(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldRejectWhenCgpaIsTooLow() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                7.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                8.0
        );

        assertFalse(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldRejectWhenGraduationYearDoesNotMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2027,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                8.0
        );

        assertFalse(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldRejectWhenBranchDoesNotMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Mechanical Engineering",
                2026,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                8.0
        );

        assertFalse(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldMatchBranchAbbreviationWithFullBranchName() {

        StudentProfile student = createStudent(
                "B.Tech",
                "CSE",
                2027,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science and Engineering",
                2027,
                8.0
        );

        assertTrue(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldRejectWhenDegreeDoesNotMatch() {

        StudentProfile student = createStudent(
                "BCA",
                "Computer Science",
                2026,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                8.0
        );

        assertFalse(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldAllowJobWhenCgpaRequirementIsMissing() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                7.5
        );

        Job job = createJob(
                "B.Tech",
                "Computer Science",
                2026,
                null
        );

        assertTrue(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldAllowJobWhenBranchRequirementIsMissing() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                8.5
        );

        Job job = createJob(
                "B.Tech",
                null,
                2026,
                8.0
        );

        assertTrue(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldAllowJobWhenDegreeRequirementIsMissing() {

        StudentProfile student = createStudent(
                null,
                "Computer Science",
                2026,
                8.5
        );

        Job job = createJob(
                null,
                "Computer Science",
                2026,
                8.0
        );

        assertTrue(
                engine.isEligible(student, job)
        );
    }

    @Test
    void shouldGiveFullScoreWhenAllPreferencesSkillsAndExperienceMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                8.5
        );

        Preference preference = createPreference(
                "Backend Developer",
                "Bangalore",
                "FULL_TIME",
                "REMOTE",
                50000.0,
                80000.0,
                false
        );

        Job job = createScoringJob();

        List<UserSkill> userSkills = List.of(
                createUserSkill("Java", 1.0),
                createUserSkill("Spring Boot", 1.0)
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                userSkills,
                job
        );

        assertEquals(100.0, score);
    }

    @Test
    void shouldGiveThirtyFiveSkillPointsWhenAllSkillsMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                8.5
        );

        Preference preference = new Preference();

        Job job = new Job();

        addRequiredSkill(job, "Java");
        addRequiredSkill(job, "Spring Boot");

        List<UserSkill> userSkills = List.of(
                createUserSkill("Java"),
                createUserSkill("Spring Boot")
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                userSkills,
                job
        );

        assertEquals(35.0, score);
    }

    @Test
    void shouldGiveSeventeenPointFiveSkillPointsWhenHalfOfSkillsMatch() {

        StudentProfile student = createStudent(
                "B.Tech",
                "Computer Science",
                2026,
                8.5
        );

        Preference preference = new Preference();

        Job job = new Job();

        addRequiredSkill(job, "Java");
        addRequiredSkill(job, "Spring Boot");

        List<UserSkill> userSkills = List.of(
                createUserSkill("Java")
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                userSkills,
                job
        );

        assertEquals(17.5, score);
    }

    @Test
    void shouldGiveRolePointsWhenPreferredRoleMatchesJobTitle() {

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        preference.setPreferredRole(
                "Backend Developer"
        );

        Job job = new Job();

        job.setTitle(
                "Java Backend Developer Intern"
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        );

        assertEquals(20.0, score);
    }

    @Test
    void shouldGiveWorkModePointsWhenPreferenceMatches() {

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        preference.setWorkMode("REMOTE");

        Job job = new Job();

        job.setWorkMode(
                WorkMode.REMOTE
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        );

        assertEquals(10.0, score);
    }

    @Test
    void shouldGiveLocationPointsWhenLocationsMatch() {

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        preference.setPreferredLocation(
                "Bangalore"
        );

        Job job = new Job();

        job.setLocation(
                "Bangalore"
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        );

        assertEquals(10.0, score);
    }

    @Test
    void shouldGiveFiveEmploymentTypePointsWhenPreferenceMatches() {

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        preference.setEmploymentType(
                "FULL_TIME"
        );

        Job job = new Job();

        job.setEmploymentType(
                EmploymentType.FULL_TIME
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        );

        assertEquals(5.0, score);
    }

    @Test
    void shouldGiveFiveSalaryPointsWhenSalaryRangesOverlap() {

        StudentProfile student = new StudentProfile();

        Preference preference = new Preference();

        preference.setMinimumSalary(
                50000.0
        );

        preference.setMaximumSalary(
                80000.0
        );

        Job job = new Job();

        job.setMinimumSalary(
                60000.0
        );

        job.setMaximumSalary(
                90000.0
        );

        double score = engine.calculateMatchScore(
                student,
                preference,
                List.of(),
                job
        );

        assertEquals(5.0, score);
    }

    /*
     * ---------------------------------------------------------
     * EXPERIENCE MATCHING TESTS
     * ---------------------------------------------------------
     */

    @Test
    void shouldGiveFullExperiencePointsWhenExperienceRequirementIsMet() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(1.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        1.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(50.0, score);
    }

    @Test
    void shouldGiveFullExperiencePointsWhenStudentExceedsRequirement() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(1.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        2.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(50.0, score);
    }

    @Test
    void shouldGiveProportionalExperiencePointsWhenExperienceIsBelowRequirement() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(2.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        1.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(42.5, score);
    }

    @Test
    void shouldGiveZeroExperiencePointsWhenStudentHasNoRelevantExperience() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(2.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Python",
                        5.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(0.0, score);
    }

    @Test
    void shouldIgnoreExperienceFromUnrelatedSkills() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(2.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        1.0
                ),
                createUserSkill(
                        "Python",
                        5.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(42.5, score);
    }

    @Test
    void shouldUseAverageExperienceAcrossMatchedRequiredSkills() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(2.0);

        addRequiredSkill(
                job,
                "Java"
        );

        addRequiredSkill(
                job,
                "Spring Boot"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        2.0
                ),
                createUserSkill(
                        "Spring Boot",
                        1.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(46.25, score);
    }

    @Test
    void shouldGiveZeroExperiencePointsWhenJobHasNoExperienceRequirement() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(null);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        5.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(35.0, score);
    }

    @Test
    void shouldGiveZeroExperiencePointsWhenJobRequiresZeroExperience() {

        Job job = new Job();

        job.setMinimumYearsOfExperience(0.0);

        addRequiredSkill(
                job,
                "Java"
        );

        List<UserSkill> userSkills = List.of(
                createUserSkill(
                        "Java",
                        5.0
                )
        );

        double score = engine.calculateMatchScore(
                new StudentProfile(),
                new Preference(),
                userSkills,
                job
        );

        assertEquals(35.0, score);
    }

    /*
     * ---------------------------------------------------------
     * TEST DATA HELPERS
     * ---------------------------------------------------------
     */

    private StudentProfile createStudent(
            String degree,
            String branch,
            Integer graduationYear,
            Double cgpa) {

        StudentProfile student =
                new StudentProfile();

        student.setDegree(degree);

        student.setBranch(branch);

        student.setGraduationYear(
                graduationYear
        );

        student.setCgpa(cgpa);

        return student;
    }

    private Job createJob(
            String degree,
            String branch,
            Integer graduationYear,
            Double minimumCgpa) {

        Job job = new Job();

        job.setRequiredDegree(
                degree
        );

        job.setEligibleGraduationYear(
                graduationYear
        );

        job.setMinimumCgpa(
                minimumCgpa
        );

        if (branch != null) {

            JobEligibleBranch eligibleBranch =
                    new JobEligibleBranch();

            eligibleBranch.setBranch(
                    branch
            );

            eligibleBranch.setJob(
                    job
            );

            job.getEligibleBranches()
                    .add(eligibleBranch);
        }

        return job;
    }

    private Preference createPreference(
            String preferredRole,
            String preferredLocation,
            String employmentType,
            String workMode,
            Double minimumSalary,
            Double maximumSalary,
            Boolean willingToRelocate) {

        Preference preference =
                new Preference();

        preference.setPreferredRole(
                preferredRole
        );

        preference.setPreferredLocation(
                preferredLocation
        );

        preference.setEmploymentType(
                employmentType
        );

        preference.setWorkMode(
                workMode
        );

        preference.setMinimumSalary(
                minimumSalary
        );

        preference.setMaximumSalary(
                maximumSalary
        );

        preference.setWillingToRelocate(
                willingToRelocate
        );

        return preference;
    }

    private Job createScoringJob() {

        Job job = new Job();

        job.setTitle(
                "Backend Developer Intern"
        );

        job.setEmploymentType(
                EmploymentType.FULL_TIME
        );

        job.setWorkMode(
                WorkMode.REMOTE
        );

        job.setLocation(
                "Bangalore"
        );

        job.setMinimumSalary(
                60000.0
        );

        job.setMaximumSalary(
                90000.0
        );

        job.setMinimumYearsOfExperience(
                1.0
        );

        addRequiredSkill(
                job,
                "Java"
        );

        addRequiredSkill(
                job,
                "Spring Boot"
        );

        return job;
    }

    private void addRequiredSkill(
            Job job,
            String skillName) {

        Skill skill = new Skill();

        skill.setName(
                skillName
        );

        JobRequiredSkill requiredSkill =
                new JobRequiredSkill();

        requiredSkill.setJob(
                job
        );

        requiredSkill.setSkill(
                skill
        );

        job.getRequiredSkills()
                .add(requiredSkill);
    }

    private UserSkill createUserSkill(
            String skillName) {

        return createUserSkill(
                skillName,
                null
        );
    }

    private UserSkill createUserSkill(
            String skillName,
            Double yearsOfExperience) {

        Skill skill = new Skill();

        skill.setName(
                skillName
        );

        UserSkill userSkill =
                new UserSkill();

        userSkill.setSkill(
                skill
        );

        userSkill.setYearsOfExperience(
                yearsOfExperience
        );

        return userSkill;
    }
}