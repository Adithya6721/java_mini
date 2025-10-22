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
import javafx.concurrent.Task;

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
    @FXML private TableView<Internship> tasksTable; // Changed to Internship temporarily if you don’t have a model class
    @FXML private TableColumn<Internship, String> taskTitleCol;
    @FXML private TableColumn<Internship, LocalDate> taskDeadlineCol;
    @FXML private TableColumn<Internship, String> taskStatusCol;
    @FXML private TableColumn<Internship, Void> taskActionsCol;

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
    private final ObservableList<Object> applicationsData = FXCollections.observableArrayList();
    private final ObservableList<Internship> tasksData = FXCollections.observableArrayList();
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
        appCompanyCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Company Name"));
        appPositionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Position Title"));
        appStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("PENDING"));
        appDateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));

        appActionsCol.setCellFactory(param -> new TableCell<Object, Void>() {
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
        taskDeadlineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        taskStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("PENDING"));

        taskActionsCol.setCellFactory(param -> new TableCell<Internship, Void>() {
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

        Task<List<Internship>> task = new Task<>() {
            @Override
            protected List<Internship> call() {
                return apiService.getInternships().join();
            }
        };

        task.setOnSucceeded(_ -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            browseData.clear();
            browseData.addAll(task.getValue());
            if (statusLabel != null) statusLabel.setText("Internships loaded");
        });

        task.setOnFailed(_ -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            Throwable ex = task.getException();
            ex.printStackTrace();
            showError("Failed to load internships: " + ex.getMessage());
        });

        new Thread(task).start();
    }

    private void loadApplications() { applicationsData.clear(); }

    private void loadTasks() { tasksData.clear(); }

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
        showSuccess("Application submitted successfully");
    }

    private void handleWithdraw(Object application) {
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

    private void handleSubmitTask(Internship task) {
        statusLabel.setText("Submitting task: " + task.getTitle());
    }

    private void handleViewTask(Internship task) {
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
                    AppContext.getSceneManager().switchToLogin(); // Changed from showLogin
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
