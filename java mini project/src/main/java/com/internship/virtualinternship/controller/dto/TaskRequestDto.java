package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class TaskRequestDto {
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull private LocalDate dueDate;
    @NotBlank private String priority;
    @NotNull @Min(0) private Integer maxScore;
    @NotBlank private String gradingCriteria;
    @NotNull private List<String> attachmentUrls;
    @NotNull private List<String> referenceLinks;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    public String getGradingCriteria() { return gradingCriteria; }
    public void setGradingCriteria(String gradingCriteria) { this.gradingCriteria = gradingCriteria; }
    public List<String> getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(List<String> attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public List<String> getReferenceLinks() { return referenceLinks; }
    public void setReferenceLinks(List<String> referenceLinks) { this.referenceLinks = referenceLinks; }
}


