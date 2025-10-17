package com.internship.client.model;

import javafx.beans.property.*;

public class Task {
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> internshipId = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    public Task() {}

    public Task(Long id, Long internshipId, String title, String description) {
        setId(id);
        setInternshipId(internshipId);
        setTitle(title);
        setDescription(description);
    }

    public ObjectProperty<Long> idProperty() { return id; }
    public Long getId() { return id.get(); }
    public void setId(Long value) { this.id.set(value); }

    public ObjectProperty<Long> internshipIdProperty() { return internshipId; }
    public Long getInternshipId() { return internshipId.get(); }
    public void setInternshipId(Long value) { this.internshipId.set(value); }

    public StringProperty titleProperty() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { this.title.set(value); }

    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { this.description.set(value); }
}



