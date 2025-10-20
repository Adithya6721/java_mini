package com.internship.client.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;

public class Internship {
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty company = new SimpleStringProperty("");
    private final StringProperty requirements = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    public Internship() {}

    public Internship(Long id, String role, String company, String requirements, String description) {
        setId(id);
        setRole(role);
        setCompany(company);
        setRequirements(requirements);
        setDescription(description);
    }

    public ObjectProperty<Long> idProperty() { return id; }
    public Long getId() { return id.get(); }
    public void setId(Long value) { this.id.set(value); }

    public StringProperty titleProperty() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { this.title.set(value); }

    public StringProperty companyProperty() { return company; }
    public String getCompany() { return company.get(); }
    public void setCompany(String value) { this.company.set(value); }

    public StringProperty requirementsProperty() { return requirements; }
    public String getRequirements() { return requirements.get(); }
    public void setRequirements(String value) { this.requirements.set(value); }

    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { this.description.set(value); }
}



