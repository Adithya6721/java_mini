package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Task entity.
 * This interface provides CRUD operations for the Task model.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // We don't need to write any code here. Spring Data JPA provides all the
    // necessary methods to interact with the 'tasks' table in the database.
}

