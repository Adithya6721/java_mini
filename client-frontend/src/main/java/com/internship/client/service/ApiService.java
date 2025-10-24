package com.internship.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.internship.client.model.*;
import com.internship.client.main.AppContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080";
    private static ApiService INSTANCE;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private volatile Role currentUserRole;

    private ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static synchronized ApiService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiService();
        }
        return INSTANCE;
    }

    public Optional<String> getJwtToken() {
        return AppContext.tokens().getToken();
    }

    public Optional<Role> getCurrentUserRole() {
        return Optional.ofNullable(currentUserRole);
    }

    public void logout() {
        AppContext.tokens().clear();
        this.currentUserRole = null;
    }

    private HttpRequest.Builder baseBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(20));
        AppContext.tokens().getToken().ifPresent(t -> builder.header("Authorization", "Bearer " + t));
        return builder.header("Content-Type", "application/json");
    }

    public CompletableFuture<LoginResponse> login(LoginRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = baseBuilder("/api/auth/login")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            try {
                                com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(resp.body());
                                String token = jsonNode.get("token").asText();
                                String roleStr = jsonNode.get("role").asText();
                                Role role = Role.valueOf(roleStr);
                                
                                LoginResponse lr = new LoginResponse(token, role);
                                AppContext.tokens().setToken(token);
                                this.currentUserRole = role;
                                return CompletableFuture.completedFuture(lr);
                            } catch (Exception e) {
                                return CompletableFuture.failedFuture(e);
                            }
                        }
                        String message = resp.statusCode() == 401 || resp.statusCode() == 403
                                ? "Invalid username or password"
                                : ("Login failed: " + resp.statusCode());
                        return CompletableFuture.failedFuture(new RuntimeException(message));
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> register(RegisterRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = baseBuilder("/api/auth/register")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            return CompletableFuture.completedFuture(null);
                        }
                        return CompletableFuture.failedFuture(new RuntimeException("Registration failed: " + resp.body()));
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<Internship>> getInternships() {
        HttpRequest request = baseBuilder("/api/student/internships/available")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<InternshipDTO> dtoList = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            List<Internship> list = dtoList.stream()
                                    .map(this::convertToInternship)
                                    .toList();
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch internships: " + resp.statusCode()));
                });
    }

    public CompletableFuture<List<Internship>> getCompanyInternships() {
        HttpRequest request = baseBuilder("/api/company/internships?companyId=2")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<InternshipDTO> dtoList = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            List<Internship> list = dtoList.stream()
                                    .map(this::convertToInternship)
                                    .toList();
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch company internships: " + resp.statusCode()));
                });
    }

    public CompletableFuture<List<Internship>> getMentorInternships() {
        HttpRequest request = baseBuilder("/api/student/internships/available")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<InternshipDTO> dtoList = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            List<Internship> list = dtoList.stream()
                                    .map(this::convertToInternship)
                                    .toList();
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch mentor internships: " + resp.statusCode()));
                });
    }

    public CompletableFuture<Internship> postInternship(InternshipCreateDTO dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            System.out.println("Sending internship creation request: " + body);
            
            HttpRequest request = baseBuilder("/api/company/internships?companyId=2")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        System.out.println("Response status: " + resp.statusCode());
                        System.out.println("Response body: " + resp.body());
                        
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            try {
                                InternshipDTO createdDto = objectMapper.readValue(resp.body(), InternshipDTO.class);
                                Internship created = convertToInternship(createdDto);
                                return CompletableFuture.completedFuture(created);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return CompletableFuture.failedFuture(e);
                            }
                        }
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to post internship: " + resp.statusCode() + " - " + resp.body()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<Task>> getTasksByInternshipId(long internshipId) {
        HttpRequest request = baseBuilder("/api/student/internships/" + internshipId + "/tasks")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<TaskDTO> dtoList = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            List<Task> list = dtoList.stream()
                                    .map(taskDto -> convertToTask(taskDto, internshipId))
                                    .toList();
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch tasks: " + resp.statusCode()));
                });
    }

    public CompletableFuture<Task> createTask(TaskDTO taskDto, Long internshipId) {
        try {
            String body = objectMapper.writeValueAsString(taskDto);
            System.out.println("Sending task creation request: " + body);
            
            // Use mentorId=3 (the mentor user created in DataInitializer)
            HttpRequest request = baseBuilder("/api/mentor/internships/" + internshipId + "/tasks?mentorId=3")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        System.out.println("Response status: " + resp.statusCode());
                        System.out.println("Response body: " + resp.body());
                        
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            try {
                                TaskDTO createdDto = objectMapper.readValue(resp.body(), TaskDTO.class);
                                Task created = convertToTask(createdDto, internshipId);
                                return CompletableFuture.completedFuture(created);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return CompletableFuture.failedFuture(e);
                            }
                        }
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to create task: " + resp.statusCode() + " - " + resp.body()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }

    // Helper methods to convert between DTOs and model objects
    private Internship convertToInternship(InternshipDTO dto) {
        Internship internship = new Internship();
        internship.setId(dto.getId());
        internship.setTitle(dto.getTitle());
        internship.setCompany(dto.getCompanyName() != null ? dto.getCompanyName() : "Company");
        internship.setRequirements(dto.getRequirements() != null ? dto.getRequirements() : "");
        internship.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        return internship;
    }

    private Task convertToTask(TaskDTO dto, Long internshipId) {
        Task task = new Task();
        task.setId(null); // Will be set by backend
        task.setInternshipId(internshipId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}