package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Application;
import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByInternship(Internship internship);
    List<Application> findByStudent(User student);
}


