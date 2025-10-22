package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Needed for ApplicationService.findByStudent()
    List<Application> findByStudentId(Long studentId);

    // Needed for ApplicationService.withdrawApplication()
    Optional<Application> findByIdAndStudentId(Long id, Long studentId);

    // Needed for ApplicationService.findByInternship()
    List<Application> findByInternship(Internship internship);
    
    // This is an alternative if you pass the full User object
    List<Application> findByStudent(User student);
}