package com.internship.virtualinternship.service;

import com.internship.virtualinternship.controller.dto.SubmissionRequestDto;
import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.SubmissionRepository;
import com.internship.virtualinternship.repository.TaskRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Submission create(SubmissionRequestDto dto, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setTask(task);
        submission.setContent(dto.getContent());
        submission.setSubmissionDate(new Date());
        
        return submissionRepository.save(submission);
    }

    public Submission findById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    public List<Submission> findByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return submissionRepository.findAll().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Submission> findByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return submissionRepository.findByTask(task);
    }

    public Submission update(Long id, SubmissionRequestDto dto) {
        Submission existing = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        existing.setContent(dto.getContent());
        
        return submissionRepository.save(existing);
    }

    // Method to update a submission directly (used for grading)
    public Submission updateSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new RuntimeException("Submission not found");
        }
        submissionRepository.deleteById(id);
    }

    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }
}