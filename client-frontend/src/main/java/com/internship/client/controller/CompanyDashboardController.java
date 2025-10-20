package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
import com.internship.client.model.Task;
import com.internship.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

// Add at top:
import javafx.scene.layout.HBox;
public class CompanyDashboardController {
    
    // Stats Labels
    @FXML private Label activeInternshipsLabel;
    @FXML private Label totalApplicationsLabel;
    @FXML private Label pendingReviewsLabel;
    @FXML private Label activeTasksLabel;
    
    // Main TabPane
    @FXML private TabPane mainTabPane;
    
    // Internships Table
    @FXML private TableView<Internship> internshipsTable;
    @FXML private TableColumn<Internship, String> titleCol;
    @FXML private TableColumn<Internship, String> statusCol;
    @FXML private TableColumn<Internship, Integer> applicationsCol;
    @FXML private TableColumn<Internship, LocalDate> startDateCol;
    @FXML private TableColumn<Internship, Void> actionsCol;
    
    // Applications Table
    @FXML private TableView<Object> applicationsTable;
    @FXML private TableColumn<Object, String> studentCol;
    @FXML private TableColumn<Object, String> internshipCol;
    @FXML private TableColumn<Object, String> appStatusCol;
    @FXML private TableColumn<Object, LocalDate> appDateCol;
    @FXML private TableColumn<Object, Void> appActionsCol;
    
    // Form Fields
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField durationField;
    @FXML private TextArea requirementsArea;
    @FXML private TextField skillsField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField stipendField;
    @FXML private TextField locationField;
    @FXML private TextField openingsField;
    @FXML private TextArea responsibilitiesArea;
    @FXML private TextField perksField;
    
    // Buttons
    @FXML private Button refreshBtn;
    @FXML private Button exportBtn;
    @FXML private Button createInternshipBtn;
    @FXML private Button clearFormBtn;
    @FXML private Button submitFormBtn;
    
    // Other Controls
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Label statusLabel;

    private final ObservableList<Internship> internshipsData = FXCollections.observableArrayList();
    private final ObservableList<Object> applicationsData = FXCollections.observableArrayList();
    private final ApiService apiService = AppContext.api();

    @FXML
    public void initialize() {
        setupColumns();
        loadStats();
        loadData();
        setupEventHandlers();
    }
    
    private void setupColumns() {
        // Internships table columns
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        applicationsCol.setCellValueFactory(new PropertyValueFactory<>("applicationCount"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        
        // Actions column with buttons
        actionsCol.setCellFactory(new Callback<TableColumn<Internship, Void>, TableCell<Internship, Void>>() {
            @Override
            public TableCell<Internship, Void> call(TableColumn<Internship, Void> param) {
                return new TableCell<Internship, Void>() {
                    private final Button viewBtn = new Button("View");
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    
                    {
                        viewBtn.setOnAction(e -> handleViewInternship(getTableView().getItems().get(getIndex())));
                        editBtn.setOnAction(e -> handleEditInternship(getTableView().getItems().get(getIndex())));
                        deleteBtn.setOnAction(e -> handleDeleteInternship(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, viewBtn, editBtn, deleteBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        internshipsTable.setItems(internshipsData);
        
        // Applications table columns
        studentCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Student Name"));
        internshipCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Internship Title"));
        appStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Status"));
        appDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        
        // Application actions column
        appActionsCol.setCellFactory(new Callback<TableColumn<Object, Void>, TableCell<Object, Void>>() {
            @Override
            public TableCell<Object, Void> call(TableColumn<Object, Void> param) {
                return new TableCell<Object, Void>() {
                    private final Button reviewBtn = new Button("Review");
                    
                    {
                        reviewBtn.setOnAction(e -> handleReviewApplication(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(reviewBtn);
                        }
                    }
                };
            }
        });
        
        applicationsTable.setItems(applicationsData);
    }
    
    private void loadStats() {
        // Load statistics from API
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        activeInternshipsLabel.setText(String.valueOf(internships.size()));
                        // Calculate other stats
                        totalApplicationsLabel.setText("0"); // TODO: Calculate from applications
                        pendingReviewsLabel.setText("0"); // TODO: Calculate from applications
                        activeTasksLabel.setText("0"); // TODO: Calculate from tasks
                    }
                }));
    }
    
    private void loadData() {
        loadInternships();
        loadApplications();
    }
    
    private void loadInternships() {
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        internshipsData.setAll(internships);
                    } else {
                        showError("Failed to load internships: " + ex.getMessage());
                    }
                }));
    }
    
    private void loadApplications() {
        // TODO: Load applications from API
        applicationsData.clear();
    }
    
    private void setupEventHandlers() {
        refreshBtn.setOnAction(e -> handleRefresh());
        exportBtn.setOnAction(e -> handleExport());
        createInternshipBtn.setOnAction(e -> handleCreateInternship());
        clearFormBtn.setOnAction(e -> handleClearForm());
        submitFormBtn.setOnAction(e -> handleSubmitForm());
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadStats();
        statusLabel.setText("Data refreshed");
    }
    
    @FXML
    private void handleExport() {
        // TODO: Implement export functionality
        statusLabel.setText("Export functionality not implemented yet");
    }
    
    @FXML
    private void handleCreateInternship() {
        mainTabPane.getSelectionModel().select(2); // Switch to Create New tab
    }
    
    @FXML
    private void handleClearForm() {
        titleField.clear();
        descriptionArea.clear();
        durationField.clear();
        requirementsArea.clear();
        skillsField.clear();
        deadlinePicker.setValue(null);
        stipendField.clear();
        locationField.clear();
        openingsField.clear();
        responsibilitiesArea.clear();
        perksField.clear();
    }
    
    @FXML
    private void handleSubmitForm() {
        if (!validateForm()) {
            return;
        }
        
        Internship newInternship = new Internship(
            null,
            titleField.getText(),
            "Company Name", // Company name
            requirementsArea.getText(),
            descriptionArea.getText()
        );
        
        submitFormBtn.setDisable(true);
        apiService.postInternship(newInternship)
                .whenComplete((created, ex) -> Platform.runLater(() -> {
                    submitFormBtn.setDisable(false);
                    if (ex == null) {
                        internshipsData.add(0, created);
                        handleClearForm();
                        mainTabPane.getSelectionModel().select(0); // Switch to My Internships tab
                        statusLabel.setText("Internship created successfully");
                        showSuccess("Internship created successfully");
                    } else {
                        showError("Failed to create internship: " + ex.getMessage());
                    }
                }));
    }
    
    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Title is required");
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            showError("Description is required");
            return false;
        }
        if (requirementsArea.getText().trim().isEmpty()) {
            showError("Requirements are required");
            return false;
        }
        if (deadlinePicker.getValue() == null) {
            showError("Deadline is required");
            return false;
        }
        return true;
    }
    
    private void handleViewInternship(Internship internship) {
        // TODO: Show internship details dialog
        statusLabel.setText("Viewing internship: " + internship.getRole());
    }
    
    private void handleEditInternship(Internship internship) {
        // TODO: Open edit dialog or switch to edit mode
        statusLabel.setText("Editing internship: " + internship.getRole());
    }
    
    private void handleDeleteInternship(Internship internship) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Internship");
        confirmDialog.setContentText("Are you sure you want to delete this internship?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement delete functionality
                internshipsData.remove(internship);
                statusLabel.setText("Internship deleted");
            }
        });
    }
    
    private void handleReviewApplication(Object application) {
        // TODO: Open application review dialog
        statusLabel.setText("Reviewing application");
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



