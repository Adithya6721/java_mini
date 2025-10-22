package com.internship.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private Integer maxScore;
    private String gradingCriteria;
    private List<String> attachmentUrls;
    private List<String> referenceLinks;

    // Getters and Setters
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