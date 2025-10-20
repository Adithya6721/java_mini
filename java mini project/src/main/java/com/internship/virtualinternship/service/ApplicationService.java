package com.internship.virtualinternship.service;

import com.internship.virtualinternship.controller.dto.ApplicationRequestDto;
import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.ApplicationRepository;
import com.internship.virtualinternship.repository.InternshipRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, InternshipRepository internshipRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    public Application create(ApplicationRequestDto dto, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Internship internship = internshipRepository.findById(dto.getInternshipId())
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        
        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setCoverLetter(dto.getCoverLetter());
        application.setResumeUrl(dto.getResumeUrl());
        application.setStatus(Application.Status.APPLIED);
        
        return applicationRepository.save(application);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public List<Application> findByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return applicationRepository.findByStudent(student);
    }

    public List<Application> findByInternship(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        return applicationRepository.findByInternship(internship);
    }

    public Application updateStatus(Long id, Application.Status status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new RuntimeException("Application not found");
        }
        applicationRepository.deleteById(id);
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }
}
