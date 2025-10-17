package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;

public class GradeRequestDto {
    @NotNull @Min(0) private Integer score;
    @NotBlank private String feedback;
    @NotBlank private String status; // PASS/FAIL/RESUBMIT

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


