package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
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
import java.util.concurrent.CompletableFuture;

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
    @FXML private TableView<com.internship.client.model.Application> applicationsTable;
    @FXML private TableColumn<com.internship.client.model.Application, String> appCompanyCol;
    @FXML private TableColumn<com.internship.client.model.Application, String> appPositionCol;
    @FXML private TableColumn<com.internship.client.model.Application, String> appStatusCol;
    @FXML private TableColumn<com.internship.client.model.Application, LocalDate> appDateCol;
    @FXML private TableColumn<com.internship.client.model.Application, Void> appActionsCol;

    // Tasks Table
    @FXML private TableView<com.internship.client.model.Task> tasksTable;
    @FXML private TableColumn<com.internship.client.model.Task, String> taskTitleCol;
    @FXML private TableColumn<com.internship.client.model.Task, LocalDate> taskDeadlineCol;
    @FXML private TableColumn<com.internship.client.model.Task, String> taskStatusCol;
    @FXML private TableColumn<com.internship.client.model.Task, Void> taskActionsCol;

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
    @FXML private Button logoutBtn;

    // Other Controls
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final ObservableList<Internship> browseData = FXCollections.observableArrayList();
    private final ObservableList<com.internship.client.model.Application> applicationsData = FXCollections.observableArrayList();
    private final ObservableList<com.internship.client.model.Task> tasksData = FXCollections.observableArrayList();
    private final ObservableList<Object> submissionsData = FXCollections.observableArrayList();
    private final ApiService apiService = AppContext.api();
    private Long currentStudentId = 1L; // Student ID from login

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
        companyCol.setCellValueFactory(new PropertyValueFactory<>("company"));
        browseTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        durationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("3 months"));
        skillsCol.setCellValueFactory(new PropertyValueFactory<>("requirements"));

        // Browse actions column
        browseActionsCol.setCellFactory(param -> new TableCell<Internship, Void>() {
            private final Button applyBtn = new Button("Apply");
            {
                applyBtn.setOnAction(e -> handleApply(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : applyBtn);
            }
        });
        browseTable.setItems(browseData);

        // Applications table columns
        appCompanyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Company"));
        appPositionCol.setCellValueFactory(new PropertyValueFactory<>("internshipTitle"));
        appStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        appDateCol.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));

        appActionsCol.setCellFactory(param -> new TableCell<com.internship.client.model.Application, Void>() {
            private final Button withdrawBtn = new Button("Withdraw");
            {
                withdrawBtn.setOnAction(e -> handleWithdraw(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : withdrawBtn);
            }
        });
        applicationsTable.setItems(applicationsData);

        // Tasks table columns
        taskTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskDeadlineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now().plusDays(7)));
        taskStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("PENDING"));

        taskActionsCol.setCellFactory(param -> new TableCell<com.internship.client.model.Task, Void>() {
            private final Button submitBtn = new Button("Submit");
            private final Button viewBtn = new Button("View");
            {
                submitBtn.setOnAction(e -> handleSubmitTask(getTableView().getItems().get(getIndex())));
                viewBtn.setOnAction(e -> handleViewTask(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, submitBtn, viewBtn));
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
        appStatusFilterCombo.getItems().addAll("ALL", "PENDING", "ACCEPTED", "REJECTED");
        taskStatusFilterCombo.getItems().addAll("ALL", "PENDING", "SUBMITTED", "GRADED");
        submissionStatusFilterCombo.getItems().addAll("ALL", "GRADED", "PENDING");

        appStatusFilterCombo.getSelectionModel().select("ALL");
        taskStatusFilterCombo.getSelectionModel().select("ALL");
        submissionStatusFilterCombo.getSelectionModel().select("ALL");
    }

    private void loadStats() {
        apiService.getInternships().whenComplete((internships, ex) -> Platform.runLater(() -> {
            if (ex == null) {
                applicationsLabel.setText("0");
                acceptedInternshipsLabel.setText("0");
                tasksPendingLabel.setText("0");
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
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        if (refreshBtn != null) refreshBtn.setDisable(true);

        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    if (refreshBtn != null) refreshBtn.setDisable(false);
                    
                    if (ex == null) {
                        browseData.clear();
                        browseData.addAll(internships);
                        if (statusLabel != null) statusLabel.setText("Internships loaded");
                    } else {
                        ex.printStackTrace();
                        showError("Failed to load internships: " + ex.getMessage());
                    }
                }));
    }

    private void loadApplications() {
        apiService.getMyApplications(currentStudentId)
                .whenComplete((applications, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        applicationsData.clear();
                        applicationsData.addAll(applications);
                        if (statusLabel != null) statusLabel.setText("Applications loaded: " + applications.size());
                    } else {
                        ex.printStackTrace();
                        System.err.println("Failed to load applications: " + ex.getMessage());
                    }
                }));
    }

    private void loadTasks() {
        // Load tasks from accepted internships
        apiService.getMyApplications(currentStudentId)
                .thenCompose(applications -> {
                    // Find accepted internships
                    List<Long> acceptedInternshipIds = applications.stream()
                            .filter(app -> "ACCEPTED".equals(app.getStatus()))
                            .map(app -> {
                                // We need to get internship ID - for now use a workaround
                                return 1L; // This will be the first internship
                            })
                            .distinct()
                            .toList();
                    
                    if (acceptedInternshipIds.isEmpty()) {
                        return CompletableFuture.completedFuture(new java.util.ArrayList<com.internship.client.model.Task>());
                    }
                    
                    // Load tasks for first accepted internship
                    return apiService.getTasksByInternshipId(acceptedInternshipIds.get(0));
                })
                .whenComplete((tasks, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        tasksData.clear();
                        tasksData.addAll(tasks);
                        if (statusLabel != null) statusLabel.setText("Tasks loaded: " + tasks.size());
                    } else {
                        ex.printStackTrace();
                        System.err.println("Failed to load tasks: " + ex.getMessage());
                    }
                }));
    }

    private void loadSubmissions() { submissionsData.clear(); }

    private void setupEventHandlers() {
        refreshBtn.setOnAction(e -> handleRefresh());
        searchBtn.setOnAction(e -> handleSearch());
        logoutBtn.setOnAction(e -> handleLogout());
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
                    internship.getTitle().toLowerCase().contains(query) ||
                    internship.getCompany().toLowerCase().contains(query) ||
                    internship.getRequirements().toLowerCase().contains(query)
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
                    Platform.runLater(this::handleSearch);
                }
            }).start();
        });
    }

    private void handleApply(Internship internship) {
        statusLabel.setText("Applying to: " + internship.getTitle());
        
        // Create application via API
        apiService.applyForInternship(currentStudentId, internship.getId(), "I am very interested in this position.")
                .whenComplete((result, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        showSuccess("Application submitted successfully!");
                        loadApplications(); // Reload applications
                        loadStats(); // Update stats
                    } else {
                        showError("Failed to submit application: " + ex.getMessage());
                    }
                }));
    }

    private void handleWithdraw(com.internship.client.model.Application application) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Withdrawal");
        confirmDialog.setHeaderText("Withdraw Application");
        confirmDialog.setContentText("Are you sure you want to withdraw this application?");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                applicationsData.remove(application);
                statusLabel.setText("Application withdrawn");
            }
        });
    }

    private void handleSubmitTask(com.internship.client.model.Task task) {
        statusLabel.setText("Submitting task: " + task.getTitle());
    }

    private void handleViewTask(com.internship.client.model.Task task) {
        statusLabel.setText("Viewing task: " + task.getTitle());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Logout");
        confirmDialog.setHeaderText("Logout");
        confirmDialog.setContentText("Are you sure you want to logout?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AppContext.api().logout();
                    AppContext.getSceneManager().switchToLogin();
                } catch (Exception e) {
                    showError("Logout failed: " + e.getMessage());
                }
            }
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
