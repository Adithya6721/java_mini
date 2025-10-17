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
    @FXML private TextField searchField;
    @FXML private TextArea detailsArea;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final ObservableList<Internship> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRole.setCellValueFactory(c -> c.getValue().roleProperty());
        colCompany.setCellValueFactory(c -> c.getValue().companyProperty());
        colRequirements.setCellValueFactory(c -> c.getValue().requirementsProperty());
        internshipsTable.setItems(data);
        internshipsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> showDetails(sel));
        refreshButton.setOnAction(e -> loadInternships());
        logoutButton.setOnAction(e -> { AppContext.api().logout(); AppContext.getSceneManager().switchToLogin(); });
        if (searchField != null) attachSearch();
        loadInternships();
    }

    private void showDetails(Internship i) {
        if (i == null) {
            detailsArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Role: ").append(i.getRole()).append('\n');
        sb.append("Company: ").append(i.getCompany()).append('\n');
        sb.append("Requirements: ").append(i.getRequirements()).append('\n');
        sb.append("Description: ").append(i.getDescription() == null ? "" : i.getDescription());
        detailsArea.setText(sb.toString());
    }

    private void loadInternships() {
        refreshButton.setDisable(true);
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        AppContext.api().getInternships()
                .whenComplete((List<Internship> list, Throwable ex) -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Failed to load internships: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        return;
                    }
                    data.setAll(list);
                }));
    }

    private void attachSearch() {
        final long[] lastTypeAt = {0};
        searchField.textProperty().addListener((obs, o, n) -> {
            lastTypeAt[0] = System.currentTimeMillis();
            new Thread(() -> {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                if (System.currentTimeMillis() - lastTypeAt[0] >= 300) {
                    String q = n == null ? "" : n.trim().toLowerCase();
                    Platform.runLater(() -> {
                        internshipsTable.setItems(data.filtered(i ->
                                i.getRole().toLowerCase().contains(q) ||
                                i.getCompany().toLowerCase().contains(q) ||
                                i.getRequirements().toLowerCase().contains(q)
                        ));
                    });
                }
            }).start();
        });
    }
}



