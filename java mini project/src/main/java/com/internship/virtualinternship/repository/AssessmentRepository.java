package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Assessment entity.
 */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    // Spring Data JPA provides all the necessary CRUD methods.
    // We can add custom query methods here if needed in the future.
}
