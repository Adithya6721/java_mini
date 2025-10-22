package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
import com.internship.client.model.Task; // <-- keep your model Task
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

public class MentorDashboardController {

    // Stats Labels
    @FXML private Label assignedStudentsLabel;
    @FXML private Label pendingTasksLabel;
    @FXML private Label completedSubmissionsLabel;
    @FXML private Label avgGradeLabel;

    // Main TabPane
    @FXML private TabPane mainTabPane;

    // Students Table
    @FXML private TableView<Object> studentsTable;
    @FXML private TableColumn<Object, String> studentNameCol;
    @FXML private TableColumn<Object, String> internshipCol;
    @FXML private TableColumn<Object, String> progressCol;
    @FXML private TableColumn<Object, LocalDate> lastActivityCol;
    @FXML private TableColumn<Object, Void> studentActionsCol;

    // Tasks Table
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> taskTitleCol;
    @FXML private TableColumn<Task, String> assignedToCol;
    @FXML private TableColumn<Task, LocalDate> deadlineCol;
    @FXML private TableColumn<Task, String> taskStatusCol;
    @FXML private TableColumn<Task, Void> taskActionsCol;

    // Task Form Fields
    @FXML private TextField taskTitleField;
    @FXML private TextArea taskDescriptionArea;
    @FXML private DatePicker taskDeadlinePicker;
    @FXML private ComboBox<String> studentCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextField maxScoreField;
    @FXML private TextArea gradingCriteriaArea;
    @FXML private TextField attachmentUrlsField;
    @FXML private TextField referenceLinksField;

    // Buttons
    @FXML private Button refreshBtn;
    @FXML private Button messageStudentsBtn;
    @FXML private Button createTaskBtn;
    @FXML private Button clearTaskFormBtn;
    @FXML private Button submitTaskFormBtn;
    @FXML private Button logoutBtn;

    // Other Controls
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final ObservableList<Object> studentsData = FXCollections.observableArrayList();
    private final ObservableList<Task> tasksData = FXCollections.observableArrayList();
    private final ApiService apiService = AppContext.api();

    @FXML
    public void initialize() {
        setupColumns();
        loadStats();
        loadData();
        setupEventHandlers();
        setupComboBoxes();
    }

    private void setupColumns() {
        // Students table columns
        studentNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Student Name"));
        internshipCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Internship Title"));
        progressCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("75%"));
        lastActivityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));

        // Student actions column
        studentActionsCol.setCellFactory(new Callback<TableColumn<Object, Void>, TableCell<Object, Void>>() {
            @Override
            public TableCell<Object, Void> call(TableColumn<Object, Void> param) {
                return new TableCell<Object, Void>() {
                    private final Button messageBtn = new Button("Message");

                    {
                        messageBtn.setOnAction(e -> handleMessageStudent(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(messageBtn);
                        }
                    }
                };
            }
        });

        studentsTable.setItems(studentsData);

        // Tasks table columns
        taskTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        assignedToCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Student Name"));
        deadlineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));
        taskStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Pending"));

        // Task actions column
        taskActionsCol.setCellFactory(new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button gradeBtn = new Button("Grade");
                    private final Button editBtn = new Button("Edit");

                    {
                        gradeBtn.setOnAction(e -> handleGradeTask(getTableView().getItems().get(getIndex())));
                        editBtn.setOnAction(e -> handleEditTask(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, gradeBtn, editBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        tasksTable.setItems(tasksData);
    }

    private void setupComboBoxes() {
        priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH", "URGENT");
        studentCombo.getItems().addAll("Student 1", "Student 2", "Student 3"); // TODO: Load dynamically
    }

    private void loadStats() {
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        assignedStudentsLabel.setText("0");
                        pendingTasksLabel.setText("0");
                        completedSubmissionsLabel.setText("0");
                        avgGradeLabel.setText("0.0");
                    }
                }));
    }

    private void loadData() {
        loadStudents();
        loadTasks();
    }

    private void loadStudents() {
        studentsData.clear(); // TODO: Load from API
    }

    private void loadTasks() {
        if (loadingIndicator != null) loadingIndicator.setVisible(true);
        if (refreshBtn != null) refreshBtn.setDisable(true);

        javafx.concurrent.Task<List<Internship>> internshipsTask = new javafx.concurrent.Task<>() {
            @Override
            protected List<Internship> call() {
                return apiService.getMentorInternships().join();
            }
        };

        internshipsTask.setOnSucceeded(e -> {
            List<Internship> internships = internshipsTask.getValue();
            if (internships != null && !internships.isEmpty()) {
                loadTasksForInternship(internships.get(0).getId());
            } else {
                if (loadingIndicator != null) loadingIndicator.setVisible(false);
                if (refreshBtn != null) refreshBtn.setDisable(false);
            }
        });

        internshipsTask.setOnFailed(e -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            Throwable ex = internshipsTask.getException();
            ex.printStackTrace();
            showError("Failed to load internships: " + ex.getMessage());
        });

        new Thread(internshipsTask).start();
    }

    private void loadTasksForInternship(long internshipId) {
        javafx.concurrent.Task<List<Task>> taskLoader = new javafx.concurrent.Task<>() {
            @Override
            protected List<Task> call() {
                return apiService.getTasksByInternshipId(internshipId).join();
            }
        };

        taskLoader.setOnSucceeded(e -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            tasksData.clear();
            tasksData.addAll(taskLoader.getValue());
            if (statusLabel != null) statusLabel.setText("Tasks loaded");
        });

        taskLoader.setOnFailed(e -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            Throwable ex = taskLoader.getException();
            ex.printStackTrace();
            showError("Failed to load tasks: " + ex.getMessage());
        });

        new Thread(taskLoader).start();
    }

    private void setupEventHandlers() {
        refreshBtn.setOnAction(e -> handleRefresh());
        messageStudentsBtn.setOnAction(e -> handleMessageStudents());
        createTaskBtn.setOnAction(e -> handleCreateTask());
        clearTaskFormBtn.setOnAction(e -> handleClearTaskForm());
        submitTaskFormBtn.setOnAction(e -> handleSubmitTaskForm());
        logoutBtn.setOnAction(e -> handleLogout());
    }

    @FXML
    private void handleRefresh() {
        loadData();
        loadStats();
        statusLabel.setText("Data refreshed");
    }

    @FXML
    private void handleMessageStudents() {
        statusLabel.setText("Messaging all students");
    }

    @FXML
    private void handleCreateTask() {
        mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    private void handleClearTaskForm() {
        taskTitleField.clear();
        taskDescriptionArea.clear();
        taskDeadlinePicker.setValue(null);
        studentCombo.getSelectionModel().clearSelection();
        priorityCombo.getSelectionModel().clearSelection();
        maxScoreField.clear();
        gradingCriteriaArea.clear();
        attachmentUrlsField.clear();
        referenceLinksField.clear();
    }

    @FXML
    private void handleSubmitTaskForm() {
        if (!validateTaskForm()) return;

        Task newTask = new Task(
                null,
                getSelectedInternshipId(),
                taskTitleField.getText(),
                taskDescriptionArea.getText()
        );

        submitTaskFormBtn.setDisable(true);
        apiService.createTask(newTask)
                .whenComplete((created, ex) -> Platform.runLater(() -> {
                    submitTaskFormBtn.setDisable(false);
                    if (ex == null) {
                        tasksData.add(0, created);
                        handleClearTaskForm();
                        mainTabPane.getSelectionModel().select(1);
                        statusLabel.setText("Task created successfully");
                        showSuccess("Task created successfully");
                    } else {
                        showError("Failed to create task: " + ex.getMessage());
                    }
                }));
    }

    private boolean validateTaskForm() {
        if (taskTitleField.getText().trim().isEmpty()) {
            showError("Title is required");
            return false;
        }
        if (taskDescriptionArea.getText().trim().isEmpty()) {
            showError("Description is required");
            return false;
        }
        if (taskDeadlinePicker.getValue() == null) {
            showError("Deadline is required");
            return false;
        }
        if (studentCombo.getSelectionModel().getSelectedItem() == null) {
            showError("Student selection is required");
            return false;
        }
        return true;
    }

    private Long getSelectedInternshipId() {
        return 1L; // TODO: Replace with actual internship ID
    }

    private void handleMessageStudent(Object student) {
        statusLabel.setText("Messaging student");
    }

    private void handleGradeTask(Task task) {
        statusLabel.setText("Grading task: " + task.getTitle());
    }

    private void handleEditTask(Task task) {
        statusLabel.setText("Editing task: " + task.getTitle());
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
                AppContext.api().logout();
                AppContext.getSceneManager().switchToLogin();
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
