
package com.placelyt.placelyt.controller;

import com.placelyt.placelyt.config.JwtAuthenticationFilter;
import com.placelyt.placelyt.dto.JobRequest;
import com.placelyt.placelyt.dto.JobResponse;
import com.placelyt.placelyt.service.JobService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JobController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        JobControllerSecurityTest.MethodSecurityTestConfiguration.class,
        JobControllerSecurityTest.TestAuthorizationExceptionHandler.class
})
class JobControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Test-only method security configuration

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfiguration {
    }

    // Converts method-security denials into HTTP 403 responses

    @RestControllerAdvice
    static class TestAuthorizationExceptionHandler {

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<Void> handleAccessDenied(
                AuthorizationDeniedException exception) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ---------------------------------------------------------
    // STUDENT RESTRICTIONS
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotCreateJob() throws Exception {

        mockMvc.perform(post("/api/jobs/company/1")
                        .contentType("application/json")
                        .content(validJobJson()))
                .andExpect(status().isForbidden());

        verify(jobService, never())
                .createJob(eq(1L), any(JobRequest.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotUpdateJob() throws Exception {

        mockMvc.perform(put("/api/jobs/1")
                        .contentType("application/json")
                        .content(validJobJson()))
                .andExpect(status().isForbidden());

        verify(jobService, never())
                .updateJob(eq(1L), any(JobRequest.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotDeleteJob() throws Exception {

        mockMvc.perform(delete("/api/jobs/1"))
                .andExpect(status().isForbidden());

        verify(jobService, never())
                .deleteJob(1L);
    }

    // ---------------------------------------------------------
    // RECRUITER ACCESS
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCanReachCreateJobEndpoint() throws Exception {

        mockMvc.perform(post("/api/jobs/company/1")
                        .contentType("application/json")
                        .content(validJobJson()))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService)
                .createJob(eq(1L), any(JobRequest.class));
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCanReachUpdateJobEndpoint() throws Exception {

        mockMvc.perform(put("/api/jobs/1")
                        .contentType("application/json")
                        .content(validJobJson()))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService)
                .updateJob(eq(1L), any(JobRequest.class));
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void recruiterCanReachDeleteJobEndpoint() throws Exception {

        mockMvc.perform(delete("/api/jobs/1"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService)
                .deleteJob(1L);
    }

    // ---------------------------------------------------------
    // PUBLICLY AVAILABLE GET ENDPOINTS
    // ---------------------------------------------------------

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanAccessGetAllJobs() throws Exception {

        mockMvc.perform(get("/api/jobs"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService).getAllJobs();
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanSearchJobs() throws Exception {

        mockMvc.perform(get("/api/jobs/search")
                        .param("keyword", "Java"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService).searchJobs("Java");
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanAccessJobDetails() throws Exception {

        mockMvc.perform(get("/api/jobs/1"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService).getJobById(1L);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanAccessJobsByCompany() throws Exception {

        mockMvc.perform(get("/api/jobs/company/1"))
                .andExpect(result -> assertNotEquals(
                        HttpStatus.FORBIDDEN.value(),
                        result.getResponse().getStatus()
                ));

        verify(jobService).getJobsByCompanyId(1L);
    }

    // ---------------------------------------------------------
    // TEST DATA
    // ---------------------------------------------------------

    private String validJobJson() {

        return """
                {
                    "title": "Java Backend Developer",
                    "description": "Backend development using Java",
                    "employmentType": "FULL_TIME",
                    "workMode": "REMOTE",
                    "location": "Remote",
                    "minimumSalary": 40000,
                    "maximumSalary": 70000,
                    "minimumCgpa": 7.0,
                    "requiredDegree": "B.Tech",
                    "eligibleGraduationYear": 2028,
                    "eligibleBranches": ["CSE", "ECE"],
                    "requiredSkillIds": [],
                    "applicationDeadline": "2027-12-31",
                    "status": "OPEN"
                }
                """;
    }
}
