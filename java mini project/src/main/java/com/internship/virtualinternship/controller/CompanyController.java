package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.controller.dto.ApplicationResponseDto;
import com.internship.virtualinternship.controller.dto.InternshipRequestDto;
import com.internship.virtualinternship.controller.dto.InternshipResponseDto;
import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.service.ApplicationService;
import com.internship.virtualinternship.service.InternshipService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final InternshipService internshipService;
    private final ApplicationService applicationService;

    public CompanyController(InternshipService internshipService, ApplicationService applicationService) {
        this.internshipService = internshipService;
        this.applicationService = applicationService;
    }

    @PostMapping("/internships")
    public ResponseEntity<?> createInternship(@Valid @RequestBody InternshipRequestDto dto, @RequestParam("companyId") Long companyId) {
        try {
            InternshipResponseDto response = internshipService.create(dto, companyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Internship creation failed: " + e.getMessage());
        }
    }

    @GetMapping("/internships")
    public ResponseEntity<Page<InternshipResponseDto>> listCompanyInternships(@RequestParam("companyId") Long companyId, Pageable pageable) {
        try {
            Page<InternshipResponseDto> response = internshipService.findByCompany(companyId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch internships: " + e.getMessage());
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

    @PutMapping("/internships/{id}")
    public ResponseEntity<?> updateInternship(@PathVariable Long id, @Valid @RequestBody InternshipRequestDto dto) {
        try {
            InternshipResponseDto response = internshipService.update(id, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Internship update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        try {
            internshipService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Internship deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/internships/{id}/applications")
    public ResponseEntity<List<ApplicationResponseDto>> listApplications(@PathVariable Long id) {
        try {
            List<Application> applications = applicationService.findByInternship(id);
            List<ApplicationResponseDto> response = applications.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch applications: " + e.getMessage());
        }
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponseDto> updateApplicationStatus(@PathVariable Long id, @RequestParam("status") Application.Status status) {
        try {
            Application application = applicationService.updateStatus(id, status);
            ApplicationResponseDto response = convertToResponseDto(application);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Application status update failed: " + e.getMessage());
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
}


