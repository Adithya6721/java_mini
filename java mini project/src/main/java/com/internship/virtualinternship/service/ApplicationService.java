package com.internship.virtualinternship.service;

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
    private final UserRepository userRepository; // Use UserRepository, as in your model
    private final InternshipRepository internshipRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository, // Use UserRepository
            InternshipRepository internshipRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
    }

    /**
     * Finds all applications for a specific student.
     */
    public List<Application> findByStudent(Long studentId) {
        // This requires the findByStudentId method in ApplicationRepository
        return applicationRepository.findByStudentId(studentId);
    }

    /**
     * Creates a new application.
     */
    public Application createApplication(Long studentId, Long internshipId, String coverLetter) {
        // Find the User (not Student)
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + studentId));
        
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found with id: " + internshipId));

        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setCoverLetter(coverLetter);
        
        // No need to setStatus or setAppliedDate, your model does it by default.
        // application.setStatus(Application.Status.APPLIED);
        // application.setAppliedDate(new Date());

        return applicationRepository.save(application);
    }

    /**
     * Withdraws an application, verifying it belongs to the student.
     */
    public void withdrawApplication(Long applicationId, Long studentId) {
        Application application = applicationRepository.findByIdAndStudentId(applicationId, studentId)
                .orElseThrow(() -> new RuntimeException("Application not found or does not belong to student"));
        
        applicationRepository.delete(application);
    }

    // --- Other methods your CompanyController might need ---

    public Application updateStatus(Long id, Application.Status status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status);
        application.setLastUpdated(new java.util.Date()); // Set last updated time
        return applicationRepository.save(application);
    }
    
    public List<Application> findByInternship(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        return applicationRepository.findByInternship(internship);
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }
}