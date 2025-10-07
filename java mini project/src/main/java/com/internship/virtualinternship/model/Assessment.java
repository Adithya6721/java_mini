package com.internship.virtualinternship.model;

import jakarta.persistence.*;

/**
 * Represents a mentor's assessment of a student's submission.
 * This entity will be mapped to a database table named 'assessments'.
 */
@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer grade; // The original grade given by the mentor (e.g., out of 100)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String feedback; // Written feedback from the mentor

    private Double plagiarismScore; // The calculated similarity score (e.g., 0.0 to 1.0)

    private Integer adjustedGrade; // The final grade after applying plagiarism penalties

    // --- Relationships ---

    // One-to-One relationship with Submission
    // Each submission will have exactly one assessment.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    // Many-to-One relationship with User (Mentor)
    // One mentor can perform many assessments.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Double getPlagiarismScore() {
        return plagiarismScore;
    }

    public void setPlagiarismScore(Double plagiarismScore) {
        this.plagiarismScore = plagiarismScore;
    }

    public Integer getAdjustedGrade() {
        return adjustedGrade;
    }

    public void setAdjustedGrade(Integer adjustedGrade) {
        this.adjustedGrade = adjustedGrade;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }
}

