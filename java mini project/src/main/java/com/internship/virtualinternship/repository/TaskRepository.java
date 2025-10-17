package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByInternship(Internship internship);
}

