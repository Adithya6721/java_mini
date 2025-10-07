package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Internship entity.
 * This interface provides CRUD operations for the Internship model.
 */
@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    // By extending JpaRepository, we get methods like findAll(), findById(), save(), delete()
    // for the Internship entity without writing any implementation code.
    // We can add custom query methods here if needed in the future.
}

