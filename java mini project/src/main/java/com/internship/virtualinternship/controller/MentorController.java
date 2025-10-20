package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.controller.dto.GradeRequestDto;
import com.internship.virtualinternship.controller.dto.SubmissionResponseDto;
import com.internship.virtualinternship.controller.dto.TaskRequestDto;
import com.internship.virtualinternship.controller.dto.TaskResponseDto;
import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.service.SubmissionService;
import com.internship.virtualinternship.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final TaskService taskService;
    private final SubmissionService submissionService;

    public MentorController(TaskService taskService, SubmissionService submissionService) {
        this.taskService = taskService;
        this.submissionService = submissionService;
    }

    @PostMapping("/internships/{id}/tasks")
    public ResponseEntity<?> createTask(@PathVariable("id") Long internshipId, @Valid @RequestBody TaskRequestDto dto) {
        try {
            Task task = taskService.create(dto, internshipId);
            TaskResponseDto response = convertToResponseDto(task);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Task creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/internships/{id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> listTasks(@PathVariable("id") Long internshipId) {
        try {
            List<Task> tasks = taskService.findByInternship(internshipId);
            List<TaskResponseDto> response = tasks.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch tasks: " + e.getMessage());
        }
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.findById(id);
            TaskResponseDto response = convertToResponseDto(task);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Task not found: " + e.getMessage());
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDto dto) {
        try {
            Task task = taskService.update(id, dto);
            TaskResponseDto response = convertToResponseDto(task);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Task update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Task deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/tasks/{id}/submissions")
    public ResponseEntity<List<SubmissionResponseDto>> listSubmissions(@PathVariable Long id) {
        try {
            List<Submission> submissions = submissionService.findByTask(id);
            List<SubmissionResponseDto> response = submissions.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch submissions: " + e.getMessage());
        }
    }

    @PostMapping("/submissions/{id}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long id, @Valid @RequestBody GradeRequestDto dto) {
        try {
            Submission submission = submissionService.findById(id);
            // Set the score and feedback properly
            submission.setScore(dto.getScore());
            submission.setFeedback(dto.getFeedback());
            
            // Update the submission
            Submission updated = submissionService.update(id, convertToSubmissionRequestDto(submission));
            SubmissionResponseDto response = convertToResponseDto(updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Grading failed: " + e.getMessage());
        }
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

    private com.internship.virtualinternship.controller.dto.SubmissionRequestDto convertToSubmissionRequestDto(Submission submission) {
        com.internship.virtualinternship.controller.dto.SubmissionRequestDto dto = new com.internship.virtualinternship.controller.dto.SubmissionRequestDto();
        dto.setTaskId(submission.getTask().getId());
        dto.setContent(submission.getContent());
        return dto;
    }
}