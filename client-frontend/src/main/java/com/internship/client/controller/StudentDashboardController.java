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
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

public class StudentDashboardController {
    
    // Stats Labels
    @FXML private Label applicationsLabel;
    @FXML private Label acceptedInternshipsLabel;
    @FXML private Label tasksPendingLabel;
    
    // Main TabPane
    @FXML private TabPane mainTabPane;
    
    // Browse Table
    @FXML private TableView<Internship> browseTable;
    @FXML private TableColumn<Internship, String> companyCol;
    @FXML private TableColumn<Internship, String> browseTitleCol;
    @FXML private TableColumn<Internship, String> durationCol;
    @FXML private TableColumn<Internship, String> skillsCol;
    @FXML private TableColumn<Internship, Void> browseActionsCol;
    
    // Applications Table
    @FXML private TableView<Object> applicationsTable;
    @FXML private TableColumn<Object, String> appCompanyCol;
    @FXML private TableColumn<Object, String> appPositionCol;
    @FXML private TableColumn<Object, String> appStatusCol;
    @FXML private TableColumn<Object, LocalDate> appDateCol;
    @FXML private TableColumn<Object, Void> appActionsCol;
    
    // Tasks Table
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> taskTitleCol;
    @FXML private TableColumn<Task, LocalDate> taskDeadlineCol;
    @FXML private TableColumn<Task, String> taskStatusCol;
    @FXML private TableColumn<Task, Void> taskActionsCol;
    
    // Submissions Table
    @FXML private TableView<Object> submissionsTable;
    @FXML private TableColumn<Object, String> submissionTaskCol;
    @FXML private TableColumn<Object, LocalDate> submissionDateCol;
    @FXML private TableColumn<Object, Integer> submissionGradeCol;
    @FXML private TableColumn<Object, String> submissionFeedbackCol;
    
    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private ComboBox<String> appStatusFilterCombo;
    @FXML private ComboBox<String> taskStatusFilterCombo;
    @FXML private ComboBox<String> submissionStatusFilterCombo;
    
    // Buttons
    @FXML private Button refreshBtn;
    
    // Other Controls
    @FXML private Label statusLabel;

    private final ObservableList<Internship> browseData = FXCollections.observableArrayList();
    private final ObservableList<Object> applicationsData = FXCollections.observableArrayList();
    private final ObservableList<Task> tasksData = FXCollections.observableArrayList();
    private final ObservableList<Object> submissionsData = FXCollections.observableArrayList();
    private final ApiService apiService = AppContext.api();

    @FXML
    public void initialize() {
        setupColumns();
        loadStats();
        loadData();
        setupEventHandlers();
        setupFilters();
        attachSearch();
    }
    
    private void setupColumns() {
        // Browse table columns
        companyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Company Name"));
        browseTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        durationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("3 months"));
        skillsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Java, Spring"));
        
        // Browse actions column
        browseActionsCol.setCellFactory(new Callback<TableColumn<Internship, Void>, TableCell<Internship, Void>>() {
            @Override
            public TableCell<Internship, Void> call(TableColumn<Internship, Void> param) {
                return new TableCell<Internship, Void>() {
                    private final Button applyBtn = new Button("Apply");
                    
                    {
                        applyBtn.setOnAction(e -> handleApply(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(applyBtn);
                        }
                    }
                };
            }
        });
        
        browseTable.setItems(browseData);
        
        // Applications table columns
        appCompanyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Company Name"));
        appPositionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Position Title"));
        appStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("PENDING"));
        appDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        
        // Application actions column
        appActionsCol.setCellFactory(new Callback<TableColumn<Object, Void>, TableCell<Object, Void>>() {
            @Override
            public TableCell<Object, Void> call(TableColumn<Object, Void> param) {
                return new TableCell<Object, Void>() {
                    private final Button withdrawBtn = new Button("Withdraw");
                    
                    {
                        withdrawBtn.setOnAction(e -> handleWithdraw(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(withdrawBtn);
                        }
                    }
                };
            }
        });
        
        applicationsTable.setItems(applicationsData);
        
        // Tasks table columns
        taskTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskDeadlineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        taskStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("PENDING"));
        
        // Task actions column
        taskActionsCol.setCellFactory(new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button submitBtn = new Button("Submit");
                    private final Button viewBtn = new Button("View");
                    
                    {
                        submitBtn.setOnAction(e -> handleSubmitTask(getTableView().getItems().get(getIndex())));
                        viewBtn.setOnAction(e -> handleViewTask(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, submitBtn, viewBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        tasksTable.setItems(tasksData);
        
        // Submissions table columns
        submissionTaskCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Task Title"));
        submissionDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        submissionGradeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(85));
        submissionFeedbackCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Good work!"));
        
        submissionsTable.setItems(submissionsData);
    }
    
    private void setupFilters() {
        // Setup status filter combo boxes
        appStatusFilterCombo.getItems().addAll("ALL", "PENDING", "ACCEPTED", "REJECTED");
        taskStatusFilterCombo.getItems().addAll("ALL", "PENDING", "SUBMITTED", "GRADED");
        submissionStatusFilterCombo.getItems().addAll("ALL", "GRADED", "PENDING");
        
        // Set default selections
        appStatusFilterCombo.getSelectionModel().select("ALL");
        taskStatusFilterCombo.getSelectionModel().select("ALL");
        submissionStatusFilterCombo.getSelectionModel().select("ALL");
    }
    
    private void loadStats() {
        // Load statistics from API
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        applicationsLabel.setText("0"); // TODO: Calculate from applications
                        acceptedInternshipsLabel.setText("0"); // TODO: Calculate from accepted applications
                        tasksPendingLabel.setText("0"); // TODO: Calculate from pending tasks
                    }
                }));
    }
    
    private void loadData() {
        loadBrowseInternships();
        loadApplications();
        loadTasks();
        loadSubmissions();
    }
    
    private void loadBrowseInternships() {
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        browseData.setAll(internships);
                    } else {
                        showError("Failed to load internships: " + ex.getMessage());
                    }
                }));
    }
    
    private void loadApplications() {
        // TODO: Load applications from API
        applicationsData.clear();
    }
    
    private void loadTasks() {
        // TODO: Load tasks from API
        tasksData.clear();
    }
    
    private void loadSubmissions() {
        // TODO: Load submissions from API
        submissionsData.clear();
    }
    
    private void setupEventHandlers() {
        refreshBtn.setOnAction(e -> handleRefresh());
        searchBtn.setOnAction(e -> handleSearch());
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadStats();
        statusLabel.setText("Data refreshed");
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            browseTable.setItems(browseData);
        } else {
            browseTable.setItems(browseData.filtered(internship ->
                internship.title().toLowerCase().contains(query) ||
                internship.company().toLowerCase().contains(query) ||
                internship.requirements().toLowerCase().contains(query)
            ));
        }
        statusLabel.setText("Search completed");
    }
    
    private void attachSearch() {
        final long[] lastTypeAt = {0};
        searchField.textProperty().addListener((obs, o, n) -> {
            lastTypeAt[0] = System.currentTimeMillis();
            new Thread(() -> {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                if (System.currentTimeMillis() - lastTypeAt[0] >= 300) {
                    Platform.runLater(() -> handleSearch());
                }
            }).start();
        });
    }
    
    private void handleApply(Internship internship) {
        // TODO: Open application dialog
        statusLabel.setText("Applying to: " + internship.title());
        showSuccess("Application submitted successfully");
    }
    
    private void handleWithdraw(Object application) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Withdrawal");
        confirmDialog.setHeaderText("Withdraw Application");
        confirmDialog.setContentText("Are you sure you want to withdraw this application?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement withdrawal functionality
                applicationsData.remove(application);
                statusLabel.setText("Application withdrawn");
            }
        });
    }
    
    private void handleSubmitTask(Task task) {
        // TODO: Open submission dialog
        statusLabel.setText("Submitting task: " + task.title());
    }
    
    private void handleViewTask(Task task) {
        // TODO: Open task details dialog
        statusLabel.setText("Viewing task: " + task.title());
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



