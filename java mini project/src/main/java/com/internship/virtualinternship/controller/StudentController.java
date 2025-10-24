package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.model.*;
import com.internship.virtualinternship.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final ApplicationService applicationService;
    private final InternshipService internshipService;
    private final TaskService taskService;
    private final SubmissionService submissionService;

    public StudentController(
            ApplicationService applicationService,
            InternshipService internshipService,
            TaskService taskService,
            SubmissionService submissionService) {
        this.applicationService = applicationService;
        this.internshipService = internshipService;
        this.taskService = taskService;
        this.submissionService = submissionService;
    }

    // Get all available internships
    @GetMapping("/internships/available")
    public ResponseEntity<List<com.internship.virtualinternship.controller.dto.InternshipResponseDto>> getAllInternships() {
        try {
            List<com.internship.virtualinternship.controller.dto.InternshipResponseDto> internships = internshipService.findAll();
            return ResponseEntity.ok(internships);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get internship by id
    @GetMapping("/internships/{id}")
    public ResponseEntity<com.internship.virtualinternship.controller.dto.InternshipResponseDto> getInternshipById(@PathVariable Long id) {
        try {
            com.internship.virtualinternship.controller.dto.InternshipResponseDto internship = internshipService.findById(id);
            return ResponseEntity.ok(internship);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Apply for an internship
    @PostMapping("/applications")
    public ResponseEntity<Application> applyForInternship(
            @RequestParam Long studentId,
            @RequestParam Long internshipId,
            @RequestBody String coverLetter) { // coverLetter comes as a plain string
        try {
            Application application = applicationService.createApplication(studentId, internshipId, coverLetter);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Return null body on error
        }
    }

    // Get student's applications
    // FIXED: Changed URL to /applications/my to avoid conflict
    @GetMapping("/applications/my") 
    public ResponseEntity<List<com.internship.virtualinternship.controller.dto.ApplicationResponseDto>> getMyApplications(@RequestParam Long studentId) {
        try {
            List<Application> applications = applicationService.findByStudent(studentId);
            List<com.internship.virtualinternship.controller.dto.ApplicationResponseDto> response = applications.stream()
                    .map(this::convertToApplicationResponseDto)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Withdraw an application
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable Long id,
            @RequestParam Long studentId) {
        try {
            applicationService.withdrawApplication(id, studentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ... (Your other Task and Submission methods) ...
    
    // Get tasks assigned to student
    /*@GetMapping("/tasks")
    public ResponseEntity<List<Task>> getMyTasks(@RequestParam Long studentId) {
        try {
            List<Task> tasks = taskService.findByStudent(studentId); // Make sure TaskService has findByStudent()
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }*/

    // Submit a task
    /*@PostMapping("/tasks/{taskId}/submit")
    public ResponseEntity<Submission> submitTask(
            @PathVariable Long taskId,
            @RequestParam Long studentId,
            @RequestBody String content) {
        try {
            Submission submission = submissionService.create(studentId, taskId, content); // Make sure SubmissionService has createSubmission()
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }*/

    // Get student's submissions
    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getMySubmissions(@RequestParam Long studentId) {
        try {
            List<Submission> submissions = submissionService.findByStudent(studentId); // Make sure SubmissionService has findByStudent()
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get tasks for a specific internship
    @GetMapping("/internships/{id}/tasks")
    public ResponseEntity<List<com.internship.virtualinternship.controller.dto.TaskResponseDto>> getTasksForInternship(@PathVariable Long id) {
        try {
            List<Task> tasks = taskService.findByInternship(id);
            List<com.internship.virtualinternship.controller.dto.TaskResponseDto> response = tasks.stream()
                    .map(this::convertToTaskResponseDto)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private com.internship.virtualinternship.controller.dto.TaskResponseDto convertToTaskResponseDto(Task task) {
        com.internship.virtualinternship.controller.dto.TaskResponseDto dto = new com.internship.virtualinternship.controller.dto.TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(new java.sql.Date(task.getDueDate().getTime()).toLocalDate());
        dto.setInternshipTitle(task.getInternship().getTitle());
        dto.setInternshipId(task.getInternship().getId());
        return dto;
    }
    
    private com.internship.virtualinternship.controller.dto.ApplicationResponseDto convertToApplicationResponseDto(Application application) {
        com.internship.virtualinternship.controller.dto.ApplicationResponseDto dto = new com.internship.virtualinternship.controller.dto.ApplicationResponseDto();
        dto.setId(application.getId());
        dto.setStudentName(application.getStudent().getUsername());
        dto.setInternshipTitle(application.getInternship().getTitle());
        dto.setStatus(application.getStatus().toString());
        dto.setApplicationDate(new java.sql.Date(application.getAppliedDate().getTime()).toLocalDate());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setResumeUrl(application.getResumeUrl());
        return dto;
    }
}