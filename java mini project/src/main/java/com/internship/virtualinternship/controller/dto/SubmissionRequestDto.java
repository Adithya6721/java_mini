package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;

public class SubmissionRequestDto {
    @NotNull private Long taskId;
    @NotBlank private String content;
    private String attachmentUrl;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}


