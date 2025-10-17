package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;

public class ApplicationRequestDto {
    @NotNull private Long internshipId;
    private String coverLetter;
    private String resumeUrl;

    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
}


