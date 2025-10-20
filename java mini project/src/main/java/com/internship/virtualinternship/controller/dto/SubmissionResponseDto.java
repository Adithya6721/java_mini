package com.internship.virtualinternship.controller.dto;

import java.time.LocalDate;

public class SubmissionResponseDto {
    private Long id;
    private String studentName;
    private String taskTitle;
    private String content;
    private LocalDate submissionDate;
    private Integer score;
    private String feedback;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
