package com.internship.virtualinternship.service;

import com.internship.virtualinternship.controller.dto.TaskRequestDto;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.repository.InternshipRepository;
import com.internship.virtualinternship.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final InternshipRepository internshipRepository;

    public TaskService(TaskRepository taskRepository, InternshipRepository internshipRepository) {
        this.taskRepository = taskRepository;
        this.internshipRepository = internshipRepository;
    }

    public Task create(TaskRequestDto dto, Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(Date.from(dto.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        task.setInternship(internship);
        
        return taskRepository.save(task);
    }

    
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> findByInternship(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        return taskRepository.findByInternship(internship);
    }

    public Task update(Long id, TaskRequestDto dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDueDate(Date.from(dto.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
