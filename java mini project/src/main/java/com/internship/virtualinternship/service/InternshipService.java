package com.internship.virtualinternship.service;

import com.internship.virtualinternship.controller.dto.InternshipRequestDto;
import com.internship.virtualinternship.controller.dto.InternshipResponseDto;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.ApplicationRepository;
import com.internship.virtualinternship.repository.InternshipRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public InternshipService(InternshipRepository internshipRepository, UserRepository userRepository, ApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    public InternshipResponseDto create(InternshipRequestDto dto, Long companyId) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        Internship internship = new Internship();
        internship.setTitle(dto.getTitle());
        internship.setDescription(dto.getDescription());
        internship.setRequirements(dto.getRequirements());
        internship.setDeadline(Date.from(dto.getDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        internship.setCompany(company);
        
        Internship saved = internshipRepository.save(internship);
        return convertToResponseDto(saved);
    }

    public InternshipResponseDto findById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        return convertToResponseDto(internship);
    }

    public Page<InternshipResponseDto> findByCompany(Long companyId, Pageable pageable) {
        Page<Internship> internships = internshipRepository.findByCompany_Id(companyId, pageable);
        return internships.map(this::convertToResponseDto);
    }

    public Page<InternshipResponseDto> findAll(Pageable pageable) {
        Page<Internship> internships = internshipRepository.findAll(pageable);
        return internships.map(this::convertToResponseDto);
    }

    public InternshipResponseDto update(Long id, InternshipRequestDto dto) {
        Internship existing = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setRequirements(dto.getRequirements());
        existing.setDeadline(Date.from(dto.getDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        Internship saved = internshipRepository.save(existing);
        return convertToResponseDto(saved);
    }

    public void delete(Long id) {
        if (!internshipRepository.existsById(id)) {
            throw new RuntimeException("Internship not found");
        }
        internshipRepository.deleteById(id);
    }

    public List<InternshipResponseDto> findAll() {
        return internshipRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public Page<InternshipResponseDto> searchInternships(String searchTerm, Pageable pageable) {
        Page<Internship> internships = internshipRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable);
        return internships.map(this::convertToResponseDto);
    }

    private InternshipResponseDto convertToResponseDto(Internship internship) {
        InternshipResponseDto dto = new InternshipResponseDto();
        dto.setId(internship.getId());
        dto.setTitle(internship.getTitle());
        dto.setDescription(internship.getDescription());
        dto.setRequirements(internship.getRequirements());
        // Fix for java.sql.Date.toInstant() UnsupportedOperationException
        dto.setDeadline(new java.sql.Date(internship.getDeadline().getTime()).toLocalDate());
        dto.setCompanyName(internship.getCompany().getUsername());
        
        // Count applications for this internship
        long applicationCount = applicationRepository.findByInternship(internship).size();
        dto.setApplicationCount(applicationCount);
        
        return dto;
    }
}
