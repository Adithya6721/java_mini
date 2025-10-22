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
    /*@GetMapping("/internships")
    public ResponseEntity<List<Internship>> getAllInternships() {
        try {
            List<Internship> internships = internshipService.findAll(); // Make sure InternshipService has findAll()
            return ResponseEntity.ok(internships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }*/

    // Get internship by id
    /*@GetMapping("/internships/{id}")
    public ResponseEntity<Internship> getInternshipById(@PathVariable Long id) {
        try {
            Internship internship = internshipService.findById(id); // Make sure InternshipService has findById()
            return ResponseEntity.ok(internship);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }*/
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
    public ResponseEntity<List<Application>> getMyApplications(@RequestParam Long studentId) {
        try {
            List<Application> applications = applicationService.findByStudent(studentId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
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
}