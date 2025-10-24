package com.internship.virtualinternship.controller.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class InternshipRequestDto {
    // Keep the truly required fields minimal to match the client
    @NotBlank private String title;
    @NotBlank private String description;
    private String duration; // optional
    private Integer stipend; // optional
    private String location; // optional
    @NotBlank private String requirements;
    private String responsibilities; // optional
    private List<String> skills; // optional
    private List<String> perks; // optional
    private Integer openings; // optional
    @NotNull private LocalDate deadline;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Integer getStipend() { return stipend; }
    public void setStipend(Integer stipend) { this.stipend = stipend; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<String> getPerks() { return perks; }
    public void setPerks(List<String> perks) { this.perks = perks; }
    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}


