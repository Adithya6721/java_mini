package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.controller.dto.ApplicationRequestDto;
import com.internship.virtualinternship.controller.dto.ApplicationResponseDto;
import com.internship.virtualinternship.controller.dto.InternshipResponseDto;
import com.internship.virtualinternship.controller.dto.SubmissionRequestDto;
import com.internship.virtualinternship.controller.dto.SubmissionResponseDto;
import com.internship.virtualinternship.controller.dto.TaskResponseDto;
import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.service.ApplicationService;
import com.internship.virtualinternship.service.InternshipService;
import com.internship.virtualinternship.service.SubmissionService;
import com.internship.virtualinternship.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final ApplicationService applicationService;
    private final InternshipService internshipService;
    private final TaskService taskService;
    private final SubmissionService submissionService;

    public StudentController(ApplicationService applicationService, InternshipService internshipService, TaskService taskService, SubmissionService submissionService) {
        this.applicationService = applicationService;
        this.internshipService = internshipService;
        this.taskService = taskService;
        this.submissionService = submissionService;
    }

    @PostMapping("/applications")
    public ResponseEntity<?> apply(@RequestParam Long studentId, @Valid @RequestBody ApplicationRequestDto dto) {
        try {
            Application application = applicationService.create(dto, studentId);
            ApplicationResponseDto response = convertToResponseDto(application);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Application submission failed: " + e.getMessage());
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponseDto>> myApplications(@RequestParam Long studentId) {
        try {
            List<Application> applications = applicationService.findByStudent(studentId);
            List<ApplicationResponseDto> response = applications.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch applications: " + e.getMessage());
        }
    }

    @GetMapping("/internships/available")
    public ResponseEntity<List<InternshipResponseDto>> availableInternships() {
        try {
            List<InternshipResponseDto> response = internshipService.findAll();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch internships: " + e.getMessage());
        }
    }

    @GetMapping("/internships/search")
    public ResponseEntity<Page<InternshipResponseDto>> searchInternships(@RequestParam String search, Pageable pageable) {
        try {
            Page<InternshipResponseDto> response = internshipService.searchInternships(search, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to search internships: " + e.getMessage());
        }
    }

    @GetMapping("/internships/{id}")
    public ResponseEntity<InternshipResponseDto> getInternshipById(@PathVariable Long id) {
        try {
            InternshipResponseDto response = internshipService.findById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Internship not found: " + e.getMessage());
        }
    }

    @GetMapping("/internships/{id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> tasksByInternship(@PathVariable Long id) {
        try {
            List<Task> tasks = taskService.findByInternship(id);
            List<TaskResponseDto> response = tasks.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch tasks: " + e.getMessage());
        }
    }

    @PostMapping("/tasks/{id}/submit")
    public ResponseEntity<?> submitTask(@PathVariable Long id, @RequestParam Long studentId, @Valid @RequestBody SubmissionRequestDto dto) {
        try {
            Submission submission = submissionService.create(dto, studentId);
            SubmissionResponseDto response = convertToResponseDto(submission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Task submission failed: " + e.getMessage());
        }
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionResponseDto>> mySubmissions(@RequestParam Long studentId) {
        try {
            List<Submission> submissions = submissionService.findByStudent(studentId);
            List<SubmissionResponseDto> response = submissions.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch submissions: " + e.getMessage());
        }
    }

    private ApplicationResponseDto convertToResponseDto(Application application) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(application.getId());
        dto.setStudentName(application.getStudent().getUsername());
        dto.setInternshipTitle(application.getInternship().getTitle());
        dto.setStatus(application.getStatus().toString());
        dto.setApplicationDate(application.getAppliedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setResumeUrl(application.getResumeUrl());
        return dto;
    }

    private TaskResponseDto convertToResponseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        dto.setInternshipTitle(task.getInternship().getTitle());
        dto.setInternshipId(task.getInternship().getId());
        return dto;
    }

    private SubmissionResponseDto convertToResponseDto(Submission submission) {
        SubmissionResponseDto dto = new SubmissionResponseDto();
        dto.setId(submission.getId());
        dto.setStudentName(submission.getStudent().getUsername());
        dto.setTaskTitle(submission.getTask().getTitle());
        dto.setContent(submission.getContent());
        dto.setSubmissionDate(submission.getSubmissionDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        dto.setScore(submission.getScore());
        dto.setFeedback(submission.getFeedback());
        return dto;
    }
}


