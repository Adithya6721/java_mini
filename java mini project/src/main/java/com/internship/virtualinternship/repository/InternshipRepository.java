package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    Page<Internship> findByCompany_Id(Long companyId, Pageable pageable);

    // This is the new, correct method. Spring Data JPA writes the query for you.
    Page<Internship> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
}