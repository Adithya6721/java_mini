package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public StudentController(ApplicationRepository applicationRepository, InternshipRepository internshipRepository, TaskRepository taskRepository, SubmissionRepository submissionRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.taskRepository = taskRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/applications")
    public ResponseEntity<Application> apply(@RequestParam Long studentId, @RequestParam Long internshipId, @RequestParam(required = false) String coverLetter, @RequestParam(required = false) String resumeUrl) {
        User student = userRepository.findById(studentId).orElseThrow();
        Internship internship = internshipRepository.findById(internshipId).orElseThrow();
        Application app = new Application();
        app.setStudent(student);
        app.setInternship(internship);
        app.setCoverLetter(coverLetter);
        app.setResumeUrl(resumeUrl);
        return ResponseEntity.ok(applicationRepository.save(app));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> myApplications(@RequestParam Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow();
        return ResponseEntity.ok(applicationRepository.findByStudent(student));
    }

    @GetMapping("/internships/available")
    public ResponseEntity<List<Internship>> availableInternships() {
        return ResponseEntity.ok(internshipRepository.findAll());
    }

    @GetMapping("/internships/{id}/tasks")
    public ResponseEntity<List<Task>> tasksByInternship(@PathVariable Long id) {
        Internship internship = internshipRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(taskRepository.findByInternship(internship));
    }

    @PostMapping("/tasks/{id}/submit")
    public ResponseEntity<Submission> submitTask(@PathVariable Long id, @RequestParam Long studentId, @RequestParam String content) {
        Task task = taskRepository.findById(id).orElseThrow();
        User student = userRepository.findById(studentId).orElseThrow();
        Submission s = new Submission();
        s.setTask(task);
        s.setStudent(student);
        s.setContent(content);
        return ResponseEntity.ok(submissionRepository.save(s));
    }
}


