
package com.internship.virtualinternship.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an internship posting in the system.
 * This entity will be mapped to a database table named 'internships'.
 */
@Entity
@Table(name = "internships")
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // Specifies that this should be stored as a Large Object (e.g., TEXT type in SQL)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String requirements;

    @Temporal(TemporalType.DATE) // Specifies that we only want to store the date, not the time
    @Column(nullable = false)
    private Date deadline;

    // --- Relationships ---

    // Many-to-One relationship with User (Company)
    // One company can have many internship postings.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: Don't load the company unless explicitly requested
    @JoinColumn(name = "company_id", nullable = false)
    private User company;

    // Many-to-Many relationship with User (Students)
    // One student can apply to many internships, and one internship can have many student applicants.
    @ManyToMany
    @JoinTable(
        name = "internship_applicants", // Name of the intermediate table
        joinColumns = @JoinColumn(name = "internship_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> applicants = new HashSet<>();


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public User getCompany() {
        return company;
    }

    public void setCompany(User company) {
        this.company = company;
    }

    public Set<User> getApplicants() {
        return applicants;
    }

    public void setApplicants(Set<User> applicants) {
        this.applicants = applicants;
    }
}

