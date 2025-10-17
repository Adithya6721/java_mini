package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;

public class SubmissionRequestDto {
    @NotNull private Long taskId;
    private String submissionText;
    private String attachmentUrl;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getSubmissionText() { return submissionText; }
    public void setSubmissionText(String submissionText) { this.submissionText = submissionText; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}


