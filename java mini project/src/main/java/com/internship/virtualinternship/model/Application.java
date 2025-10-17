package com.internship.virtualinternship.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "applications")
public class Application {

    public enum Status {
        APPLIED, UNDER_REVIEW, SHORTLISTED, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.APPLIED;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date appliedDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(columnDefinition = "TEXT")
    private String companyFeedback;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Internship getInternship() { return internship; }
    public void setInternship(Internship internship) { this.internship = internship; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Date getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Date appliedDate) { this.appliedDate = appliedDate; }
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
    public String getCompanyFeedback() { return companyFeedback; }
    public void setCompanyFeedback(String companyFeedback) { this.companyFeedback = companyFeedback; }
}


