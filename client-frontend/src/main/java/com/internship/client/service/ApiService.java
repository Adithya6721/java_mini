package com.internship.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                                LoginResponse lr = objectMapper.readValue(resp.body(), LoginResponse.class);
                                AppContext.tokens().setToken(lr.token());
                                this.currentUserRole = lr.role();
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
                        return CompletableFuture.failedFuture(new RuntimeException("Registration failed: " + resp.statusCode() + " - " + resp.body()));
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
                            List<Internship> list = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch internships: " + resp.statusCode()));
                });
    }

    public CompletableFuture<List<Internship>> getCompanyInternships() {
        HttpRequest request = baseBuilder("/api/company/internships?companyId=1")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<Internship> list = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
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
                            List<Internship> list = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch mentor internships: " + resp.statusCode()));
                });
    }

    public CompletableFuture<Internship> postInternship(Internship internship) {
        try {
            String body = objectMapper.writeValueAsString(internship);
            HttpRequest request = baseBuilder("/api/internships")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            try {
                                Internship created = objectMapper.readValue(resp.body(), Internship.class);
                                return CompletableFuture.completedFuture(created);
                            } catch (Exception e) {
                                return CompletableFuture.failedFuture(e);
                            }
                        }
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to post internship: " + resp.statusCode()));
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<List<Task>> getTasksByInternshipId(long internshipId) {
        HttpRequest request = baseBuilder("/api/tasks?internshipId=" + internshipId)
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        try {
                            List<Task> list = objectMapper.readValue(resp.body(), new TypeReference<>() {});
                            return CompletableFuture.completedFuture(list);
                        } catch (Exception e) {
                            return CompletableFuture.failedFuture(e);
                        }
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Failed to fetch tasks: " + resp.statusCode()));
                });
    }

    public CompletableFuture<Task> createTask(Task task) {
        try {
            String body = objectMapper.writeValueAsString(task);
            HttpRequest request = baseBuilder("/api/tasks")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(resp -> {
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            try {
                                Task created = objectMapper.readValue(resp.body(), Task.class);
                                return CompletableFuture.completedFuture(created);
                            } catch (Exception e) {
                                return CompletableFuture.failedFuture(e);
                            }
                        }
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to create task: " + resp.statusCode()));
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}



