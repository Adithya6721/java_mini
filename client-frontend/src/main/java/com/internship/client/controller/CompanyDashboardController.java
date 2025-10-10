package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CompanyDashboardController {
    @FXML private TableView<Internship> internshipsTable;
    @FXML private TableColumn<Internship, String> colRole;
    @FXML private TableColumn<Internship, String> colCompany;
    @FXML private TableColumn<Internship, String> colRequirements;
    @FXML private TextField roleField;
    @FXML private TextField companyField;
    @FXML private TextField requirementsField;
    @FXML private TextArea descriptionArea;
    @FXML private Button postButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final ObservableList<Internship> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRole.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().role()));
        colCompany.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().company()));
        colRequirements.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().requirements()));
        internshipsTable.setItems(data);
        refreshButton.setOnAction(e -> loadInternships());
        logoutButton.setOnAction(e -> { AppContext.api().logout(); AppContext.getSceneManager().switchToLogin(); });
        postButton.setOnAction(e -> postInternship());
        loadInternships();
    }

    private void loadInternships() {
        refreshButton.setDisable(true);
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        AppContext.api().getInternships()
                .whenComplete((list, ex) -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Failed to load: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        return;
                    }
                    data.setAll(list);
                }));
    }

    private void postInternship() {
        Internship toCreate = new Internship(null, roleField.getText(), companyField.getText(), requirementsField.getText(), descriptionArea.getText());
        postButton.setDisable(true);
        AppContext.api().postInternship(toCreate)
                .whenComplete((created, ex) -> Platform.runLater(() -> {
                    postButton.setDisable(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Create failed: " + ex.getMessage(), ButtonType.OK).showAndWait();
                    } else {
                        data.add(0, created);
                        roleField.clear(); companyField.clear(); requirementsField.clear(); descriptionArea.clear();
                        new Alert(Alert.AlertType.INFORMATION, "Internship posted", ButtonType.OK).showAndWait();
                    }
                }));
    }
}



