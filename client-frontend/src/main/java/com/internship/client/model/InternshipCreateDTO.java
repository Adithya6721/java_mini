package com.internship.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class InternshipCreateDTO {
    private String title;
    private String description;
    private String duration;
    private Integer stipend;
    private String location;
    private String requirements;
    private String responsibilities;
    private List<String> skills;
    private List<String> perks;
    private Integer openings;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    public InternshipCreateDTO() {
        this.duration = "3 months";
        this.stipend = 0;
        this.location = "Remote";
        this.responsibilities = "Work on assigned tasks";
        this.skills = Arrays.asList("General");
        this.perks = Arrays.asList("Certificate");
        this.openings = 1;
    }

    // Getters and Setters
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