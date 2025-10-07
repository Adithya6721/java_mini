package com.internship.virtualinternship.service;

import com.internship.virtualinternship.model.Assessment;
import com.internship.virtualinternship.model.Submission;
import com.internship.virtualinternship.model.Task;
import com.internship.virtualinternship.repository.AssessmentRepository;
import com.internship.virtualinternship.repository.SubmissionRepository;
import com.internship.virtualinternship.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling business logic related to assessments.
 * This includes plagiarism checks and grade adjustments.
 */
@Service
public class AssessmentService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentRepository assessmentRepository;
    private final TaskRepository taskRepository;
    private final PlagiarismService plagiarismService;

    // A threshold for plagiarism. If similarity is above this, we flag it.
    private static final double PLAGIARISM_THRESHOLD = 0.85;

    @Autowired
    public AssessmentService(SubmissionRepository submissionRepository,
                             AssessmentRepository assessmentRepository,
                             TaskRepository taskRepository,
                             PlagiarismService plagiarismService) {
        this.submissionRepository = submissionRepository;
        this.assessmentRepository = assessmentRepository;
        this.taskRepository = taskRepository;
        this.plagiarismService = plagiarismService;
    }

    /**
     * Assesses a submission, checks for plagiarism, adjusts grade if necessary, and saves the assessment.
     *
     * @param submissionId The ID of the submission to assess.
     * @param initialGrade The initial grade given by the mentor.
     * @param feedback     The feedback given by the mentor.
     * @return The saved Assessment object.
     */
    public Assessment assessSubmission(Long submissionId, int initialGrade, String feedback) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (submissionOptional.isEmpty()) {
            throw new IllegalStateException("Submission not found with ID: " + submissionId);
        }

        Submission currentSubmission = submissionOptional.get();
        Task associatedTask = currentSubmission.getTask();

        int finalGrade = initialGrade;
        String finalFeedback = feedback;
        double maxSimilarity = 0.0;

        // Fetch all submissions for the same task to check for plagiarism
        List<Submission> otherSubmissions = submissionRepository.findByTask(associatedTask);

        for (Submission other : otherSubmissions) {
            // Don't compare the submission with itself
            if (other.getId().equals(currentSubmission.getId())) {
                continue;
            }

            double similarity = plagiarismService.calculateSimilarity(currentSubmission.getContent(), other.getContent());
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
            }

            // If similarity exceeds the threshold, adjust the grade and feedback
            if (similarity > PLAGIARISM_THRESHOLD) {
                finalGrade = 0; // Penalize for plagiarism
                finalFeedback = feedback + " [PLAGIARISM DETECTED: Submission is " +
                        String.format("%.2f%%", similarity * 100) +
                        " similar to another submission.]";
                break; // Stop checking once plagiarism is confirmed
            }
        }
        
        // Create and save the new assessment
        Assessment assessment = new Assessment();
        assessment.setSubmission(currentSubmission);
        assessment.setGrade(finalGrade);
        assessment.setFeedback(finalFeedback);
        assessment.setPlagiarismScore(maxSimilarity);

        return assessmentRepository.save(assessment);
    }
}

