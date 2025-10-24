package com.internship.client.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Application {
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty studentName = new SimpleStringProperty("");
    private final StringProperty internshipTitle = new SimpleStringProperty("");
    private final StringProperty status = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> applicationDate = new SimpleObjectProperty<>();
    private final StringProperty coverLetter = new SimpleStringProperty("");
    private final StringProperty resumeUrl = new SimpleStringProperty("");

    public Application() {}

    public Application(Long id, String studentName, String internshipTitle, String status, LocalDate applicationDate) {
        setId(id);
        setStudentName(studentName);
        setInternshipTitle(internshipTitle);
        setStatus(status);
        setApplicationDate(applicationDate);
    }

    // ID
    public ObjectProperty<Long> idProperty() { return id; }
    public Long getId() { return id.get(); }
    public void setId(Long value) { this.id.set(value); }

    // Student Name
    public StringProperty studentNameProperty() { return studentName; }
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String value) { this.studentName.set(value); }

    // Internship Title
    public StringProperty internshipTitleProperty() { return internshipTitle; }
    public String getInternshipTitle() { return internshipTitle.get(); }
    public void setInternshipTitle(String value) { this.internshipTitle.set(value); }

    // Status
    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { this.status.set(value); }

    // Application Date
    public ObjectProperty<LocalDate> applicationDateProperty() { return applicationDate; }
    public LocalDate getApplicationDate() { return applicationDate.get(); }
    public void setApplicationDate(LocalDate value) { this.applicationDate.set(value); }

    // Cover Letter
    public StringProperty coverLetterProperty() { return coverLetter; }
    public String getCoverLetter() { return coverLetter.get(); }
    public void setCoverLetter(String value) { this.coverLetter.set(value); }

    // Resume URL
    public StringProperty resumeUrlProperty() { return resumeUrl; }
    public String getResumeUrl() { return resumeUrl.get(); }
    public void setResumeUrl(String value) { this.resumeUrl.set(value); }
}
