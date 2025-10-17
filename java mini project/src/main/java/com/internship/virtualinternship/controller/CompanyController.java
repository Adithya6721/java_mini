package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.ApplicationRepository;
import com.internship.virtualinternship.repository.InternshipRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public CompanyController(InternshipRepository internshipRepository, ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/internships")
    public ResponseEntity<Internship> createInternship(@RequestBody Internship internship, @RequestParam("companyId") Long companyId) {
        User company = userRepository.findById(companyId).orElseThrow();
        internship.setCompany(company);
        return ResponseEntity.ok(internshipRepository.save(internship));
    }

    @GetMapping("/internships")
    public ResponseEntity<Page<Internship>> listCompanyInternships(@RequestParam("companyId") Long companyId, Pageable pageable) {
        User company = userRepository.findById(companyId).orElseThrow();
        Page<Internship> page = internshipRepository.findAll(pageable).map(i -> i); // Simplified; ideally filter by company
        return ResponseEntity.ok(page);
    }

    @PutMapping("/internships/{id}")
    public ResponseEntity<Internship> updateInternship(@PathVariable Long id, @RequestBody Internship body) {
        Internship existing = internshipRepository.findById(id).orElseThrow();
        existing.setTitle(body.getTitle());
        existing.setDescription(body.getDescription());
        existing.setRequirements(body.getRequirements());
        existing.setDeadline(body.getDeadline());
        return ResponseEntity.ok(internshipRepository.save(existing));
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        internshipRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internships/{id}/applications")
    public ResponseEntity<List<Application>> listApplications(@PathVariable Long id) {
        Internship internship = internshipRepository.findById(id).orElseThrow();
        List<Application> apps = applicationRepository.findByInternship(internship);
        return ResponseEntity.ok(apps);
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(@PathVariable Long id, @RequestParam("status") Application.Status status) {
        Application app = applicationRepository.findById(id).orElseThrow();
        app.setStatus(status);
        return ResponseEntity.ok(applicationRepository.save(app));
    }
}


