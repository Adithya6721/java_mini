package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    Page<Internship> findByCompany_Id(Long companyId, Pageable pageable);

    @Query("SELECT i FROM Internship i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Internship> searchInternships(@Param("search") String search, Pageable pageable);
}

