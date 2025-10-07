package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class StudentDashboardController {
    @FXML private TableView<Internship> internshipsTable;
    @FXML private TableColumn<Internship, String> colRole;
    @FXML private TableColumn<Internship, String> colCompany;
    @FXML private TableColumn<Internship, String> colRequirements;
    @FXML private TextArea detailsArea;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    private final ObservableList<Internship> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRole.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().role()));
        colCompany.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().company()));
        colRequirements.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().requirements()));
        internshipsTable.setItems(data);
        internshipsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> showDetails(sel));
        refreshButton.setOnAction(e -> loadInternships());
        logoutButton.setOnAction(e -> { AppContext.api().logout(); AppContext.getSceneManager().switchToLogin(); });
        loadInternships();
    }

    private void showDetails(Internship i) {
        if (i == null) {
            detailsArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Role: ").append(i.role()).append('\n');
        sb.append("Company: ").append(i.company()).append('\n');
        sb.append("Requirements: ").append(i.requirements()).append('\n');
        sb.append("Description: ").append(i.description() == null ? "" : i.description());
        detailsArea.setText(sb.toString());
    }

    private void loadInternships() {
        refreshButton.setDisable(true);
        AppContext.api().getInternships()
                .whenComplete((List<Internship> list, Throwable ex) -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Failed to load internships: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        return;
                    }
                    data.setAll(list);
                }));
    }
}



