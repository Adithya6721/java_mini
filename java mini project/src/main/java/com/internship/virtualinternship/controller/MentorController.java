package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.repository.SubmissionRepository;
import com.internship.virtualinternship.repository.TaskRepository;
import com.internship.virtualinternship.repository.InternshipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final InternshipRepository internshipRepository;

    public MentorController(TaskRepository taskRepository, SubmissionRepository submissionRepository, InternshipRepository internshipRepository) {
        this.taskRepository = taskRepository;
        this.submissionRepository = submissionRepository;
        this.internshipRepository = internshipRepository;
    }

    @PostMapping("/internships/{id}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable("id") Long internshipId, @RequestBody Task task) {
        Internship internship = internshipRepository.findById(internshipId).orElseThrow();
        task.setInternship(internship);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping("/internships/{id}/tasks")
    public ResponseEntity<List<Task>> listTasks(@PathVariable("id") Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElseThrow();
        return ResponseEntity.ok(taskRepository.findByInternship(internship));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task body) {
        Task t = taskRepository.findById(id).orElseThrow();
        t.setTitle(body.getTitle());
        t.setDescription(body.getDescription());
        t.setDueDate(body.getDueDate());
        return ResponseEntity.ok(taskRepository.save(t));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{id}/submissions")
    public ResponseEntity<List<Submission>> listSubmissions(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(submissionRepository.findByTask(task));
    }

    @PostMapping("/submissions/{id}/grade")
    public ResponseEntity<Submission> gradeSubmission(@PathVariable Long id, @RequestParam("feedback") String feedback) {
        Submission s = submissionRepository.findById(id).orElseThrow();
        s.setContent((s.getContent() == null ? "" : s.getContent()) + "\n\n[Feedback] " + feedback);
        return ResponseEntity.ok(submissionRepository.save(s));
    }
}


