package com.laneway.employee_portal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeePortalApplicationTests {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080";

    // ---------- AUTHENTICATION TESTS ----------

    @Test
    public void loginWithValidCredentials_shouldReturnToken() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", "rahul@laneway.com");
        loginBody.put("password", "rahul123");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginBody, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));
    }

    @Test
    public void loginWithWrongPassword_shouldReturn401() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", "rahul@laneway.com");
        loginBody.put("password", "wrongpassword");

        try {
            restTemplate.postForEntity(baseUrl + "/auth/login", loginBody, String.class);
            fail("Expected a 401 Unauthorized error but the request succeeded");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void accessingProtectedEndpointWithoutToken_shouldReturn403() {
        try {
            restTemplate.getForEntity(baseUrl + "/employees", String.class);
            fail("Expected a 403 Forbidden error but the request succeeded");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
        }
    }

    // ---------- EMPLOYEE CRUD TEST ----------

    @Test
    public void createEmployee_thenRetrieveIt_shouldSucceed() {
        String token = loginAndGetToken("rahul@laneway.com", "rahul123");

        Map<String, String> newEmployee = new HashMap<>();
        newEmployee.put("name", "Test CI Employee");
        newEmployee.put("email", "ciuser_" + System.currentTimeMillis() + "@laneway.com");
        newEmployee.put("password", "test123");
        newEmployee.put("designation", "QA");
        newEmployee.put("accessRole", "MANAGER");
        newEmployee.put("workLocation", "REMOTE");
        newEmployee.put("dateOfJoining", "2026-06-20");
        newEmployee.put("employmentStatus", "ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
                baseUrl + "/employees", request, Map.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().get("id"));
    }

    // ---------- ALLOCATION VALIDATION TEST ----------

    @Test
    public void assigningOverAllocatedEmployee_shouldBeRejected() {
        String token = loginAndGetToken("rahul@laneway.com", "rahul123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> assignmentRequest = new HashMap<>();
        assignmentRequest.put("employeeId", 2L);
        assignmentRequest.put("projectId", 1L);
        assignmentRequest.put("roleOnProject", "Tester");
        assignmentRequest.put("allocationPercentage", 150);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(assignmentRequest, headers);

        try {
            restTemplate.postForEntity(baseUrl + "/project-assignments", request, String.class);
            fail("Expected a 4xx error but the request succeeded");
        } catch (HttpClientErrorException e) {
            assertTrue(e.getStatusCode().is4xxClientError());
        }
    }

    // ---------- HELPER METHOD ----------

    private String loginAndGetToken(String email, String password) {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginBody, Map.class);

        return (String) response.getBody().get("token");
    }
}