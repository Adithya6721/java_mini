package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Finds all submissions associated with a specific task.
     * This is crucial for the plagiarism check to compare all submissions for a given task.
     * @param task The task entity to find submissions for.
     * @return A list of submissions for the specified task.
     */
    List<Submission> findByTask(Task task);
}

