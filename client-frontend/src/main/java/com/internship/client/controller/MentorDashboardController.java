package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MentorDashboardController {

    @FXML private Label assignedStudentsLabel;
    @FXML private Label pendingTasksLabel;
    @FXML private Label completedSubmissionsLabel;
    @FXML private Label avgGradeLabel;
    @FXML private TabPane mainTabPane;

    @FXML private TableView<Object> studentsTable;
    @FXML private TableColumn<Object, String> studentNameCol;
    @FXML private TableColumn<Object, String> internshipCol;
    @FXML private TableColumn<Object, String> progressCol;
    @FXML private TableColumn<Object, LocalDate> lastActivityCol;
    @FXML private TableColumn<Object, Void> studentActionsCol;

    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> taskTitleCol;
    @FXML private TableColumn<Task, String> assignedToCol;
    @FXML private TableColumn<Task, LocalDate> deadlineCol;
    @FXML private TableColumn<Task, String> taskStatusCol;
    @FXML private TableColumn<Task, Void> taskActionsCol;

    @FXML private TextField taskTitleField;
    @FXML private TextArea taskDescriptionArea;
    @FXML private DatePicker taskDeadlinePicker;
    @FXML private ComboBox<String> studentCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextField maxScoreField;
    @FXML private TextArea gradingCriteriaArea;
    @FXML private TextField attachmentUrlsField;
    @FXML private TextField referenceLinksField;

    @FXML private Button refreshBtn;
    @FXML private Button messageStudentsBtn;
    @FXML private Button createTaskBtn;
    @FXML private Button clearTaskFormBtn;
    @FXML private Button submitTaskFormBtn;
    @FXML private Button logoutBtn;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final ObservableList<Object> studentsData = FXCollections.observableArrayList();
    private final ObservableList<Task> tasksData = FXCollections.observableArrayList();
    private final ApiService apiService = AppContext.api();
    private Long currentInternshipId = null;

    @FXML
    public void initialize() {
        setupColumns();
        loadStats();
        loadData();
        setupEventHandlers();
        setupComboBoxes();
    }

    private void setupColumns() {
        studentNameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("Student Name"));
        internshipCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("Internship Title"));
        progressCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("75%"));
        lastActivityCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now()));

        studentActionsCol.setCellFactory(param -> new TableCell<Object, Void>() {
            private final Button messageBtn = new Button("Message");

            {
                messageBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        Object item = getTableView().getItems().get(getIndex());
                        handleMessageStudent(item);
                    }
                });
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
        });

        studentsTable.setItems(studentsData);

        taskTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        assignedToCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("All Students"));
        deadlineCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(LocalDate.now().plusDays(7)));
        taskStatusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("Pending"));

        taskActionsCol.setCellFactory(param -> new TableCell<Task, Void>() {
            private final Button gradeBtn = new Button("Grade");
            private final Button editBtn = new Button("Edit");

            {
                gradeBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        Task item = getTableView().getItems().get(getIndex());
                        handleGradeTask(item);
                    }
                });
                editBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        Task item = getTableView().getItems().get(getIndex());
                        handleEditTask(item);
                    }
                });
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
        });

        tasksTable.setItems(tasksData);
    }

    private void setupComboBoxes() {
        if (priorityCombo != null) {
            priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH", "URGENT");
            priorityCombo.getSelectionModel().select("MEDIUM");
        }
        if (studentCombo != null) {
            studentCombo.getItems().addAll("All Students", "Student 1", "Student 2", "Student 3");
            studentCombo.getSelectionModel().select("All Students");
        }
    }

    private void loadStats() {
        apiService.getMentorInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null && internships != null) {
                        assignedStudentsLabel.setText("0");
                        pendingTasksLabel.setText(String.valueOf(tasksData.size()));
                        completedSubmissionsLabel.setText("0");
                        avgGradeLabel.setText("0.0");
                    } else {
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
        studentsData.clear();
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
                currentInternshipId = internships.get(0).getId();
                loadTasksForInternship(currentInternshipId);
            } else {
                if (loadingIndicator != null) loadingIndicator.setVisible(false);
                if (refreshBtn != null) refreshBtn.setDisable(false);
                if (statusLabel != null) statusLabel.setText("No internships found");
            }
        });

        internshipsTask.setOnFailed(e -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            Throwable ex = internshipsTask.getException();
            if (ex != null) {
                ex.printStackTrace();
                showError("Failed to load internships: " + ex.getMessage());
            }
        });

        new Thread(internshipsTask).start();
    }

    private void loadTasksForInternship(Long internshipId) {
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
            List<Task> result = taskLoader.getValue();
            if (result != null) {
                tasksData.addAll(result);
            }
            if (statusLabel != null) statusLabel.setText("Tasks loaded: " + tasksData.size());
        });

        taskLoader.setOnFailed(e -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            if (refreshBtn != null) refreshBtn.setDisable(false);
            Throwable ex = taskLoader.getException();
            if (ex != null) {
                ex.printStackTrace();
                showError("Failed to load tasks: " + ex.getMessage());
            }
        });

        new Thread(taskLoader).start();
    }

    private void setupEventHandlers() {
        if (refreshBtn != null) refreshBtn.setOnAction(e -> handleRefresh());
        if (messageStudentsBtn != null) messageStudentsBtn.setOnAction(e -> handleMessageStudents());
        if (createTaskBtn != null) createTaskBtn.setOnAction(e -> handleCreateTask());
        if (clearTaskFormBtn != null) clearTaskFormBtn.setOnAction(e -> handleClearTaskForm());
        if (submitTaskFormBtn != null) submitTaskFormBtn.setOnAction(e -> handleSubmitTaskForm());
        if (logoutBtn != null) logoutBtn.setOnAction(e -> handleLogout());
    }

    @FXML
    private void handleRefresh() {
        loadData();
        loadStats();
        if (statusLabel != null) statusLabel.setText("Data refreshed");
    }

    @FXML
    private void handleMessageStudents() {
        if (statusLabel != null) statusLabel.setText("Messaging all students");
    }

    @FXML
    private void handleCreateTask() {
        if (mainTabPane != null) mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    private void handleClearTaskForm() {
        if (taskTitleField != null) taskTitleField.clear();
        if (taskDescriptionArea != null) taskDescriptionArea.clear();
        if (taskDeadlinePicker != null) taskDeadlinePicker.setValue(null);
        if (studentCombo != null) studentCombo.getSelectionModel().clearSelection();
        if (priorityCombo != null) priorityCombo.getSelectionModel().select("MEDIUM");
        if (maxScoreField != null) maxScoreField.clear();
        if (gradingCriteriaArea != null) gradingCriteriaArea.clear();
        if (attachmentUrlsField != null) attachmentUrlsField.clear();
        if (referenceLinksField != null) referenceLinksField.clear();
    }

    @FXML
    private void handleSubmitTaskForm() {
        if (!validateTaskForm()) return;

        if (currentInternshipId == null) {
            showError("No internship selected. Please load internships first.");
            return;
        }

        TaskDTO taskDto = new TaskDTO();
        taskDto.setTitle(taskTitleField.getText().trim());
        taskDto.setDescription(taskDescriptionArea.getText().trim());
        taskDto.setDueDate(taskDeadlinePicker.getValue());
        taskDto.setPriority(priorityCombo.getValue() != null ? priorityCombo.getValue() : "MEDIUM");
        
        // Set optional fields
        if (maxScoreField != null && !maxScoreField.getText().trim().isEmpty()) {
            try {
                taskDto.setMaxScore(Integer.parseInt(maxScoreField.getText().trim()));
            } catch (NumberFormatException e) {
                taskDto.setMaxScore(100);
            }
        } else {
            taskDto.setMaxScore(100);
        }
        
        if (gradingCriteriaArea != null && !gradingCriteriaArea.getText().trim().isEmpty()) {
            taskDto.setGradingCriteria(gradingCriteriaArea.getText().trim());
        } else {
            taskDto.setGradingCriteria("Complete the task as described");
        }
        
        if (attachmentUrlsField != null && !attachmentUrlsField.getText().trim().isEmpty()) {
            taskDto.setAttachmentUrls(Arrays.asList(attachmentUrlsField.getText().split(",")));
        } else {
            taskDto.setAttachmentUrls(new ArrayList<>());
        }
        
        if (referenceLinksField != null && !referenceLinksField.getText().trim().isEmpty()) {
            taskDto.setReferenceLinks(Arrays.asList(referenceLinksField.getText().split(",")));
        } else {
            taskDto.setReferenceLinks(new ArrayList<>());
        }

        submitTaskFormBtn.setDisable(true);
        apiService.createTask(taskDto, currentInternshipId)
                .whenComplete((created, ex) -> Platform.runLater(() -> {
                    submitTaskFormBtn.setDisable(false);
                    if (ex == null && created != null) {
                        tasksData.add(0, created);
                        handleClearTaskForm();
                        if (mainTabPane != null) mainTabPane.getSelectionModel().select(1);
                        if (statusLabel != null) statusLabel.setText("Task created successfully");
                        showSuccess("Task created successfully");
                        loadStats();
                    } else {
                        String errorMsg = ex != null ? ex.getMessage() : "Unknown error";
                        showError("Failed to create task: " + errorMsg);
                        if (ex != null) ex.printStackTrace();
                    }
                }));
    }

    private boolean validateTaskForm() {
        if (taskTitleField == null || taskTitleField.getText().trim().isEmpty()) {
            showError("Title is required");
            return false;
        }
        if (taskDescriptionArea == null || taskDescriptionArea.getText().trim().isEmpty()) {
            showError("Description is required");
            return false;
        }
        if (taskDeadlinePicker == null || taskDeadlinePicker.getValue() == null) {
            showError("Deadline is required");
            return false;
        }
        if (taskDeadlinePicker.getValue().isBefore(LocalDate.now())) {
            showError("Deadline must be in the future");
            return false;
        }
        if (studentCombo == null || studentCombo.getSelectionModel().getSelectedItem() == null) {
            showError("Student selection is required");
            return false;
        }
        return true;
    }

    private void handleMessageStudent(Object student) {
        if (statusLabel != null) statusLabel.setText("Messaging student");
    }

    private void handleGradeTask(Task task) {
        if (statusLabel != null) statusLabel.setText("Grading task: " + task.getTitle());
    }

    private void handleEditTask(Task task) {
        if (statusLabel != null) statusLabel.setText("Editing task: " + task.getTitle());
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