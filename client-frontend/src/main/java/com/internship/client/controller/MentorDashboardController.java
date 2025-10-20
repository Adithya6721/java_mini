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
    
    // Other Controls
    @FXML private Label statusLabel;

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
        // Setup priority combo box
        priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH", "URGENT");
        
        // Setup student combo box - TODO: Load from API
        studentCombo.getItems().addAll("Student 1", "Student 2", "Student 3");
    }
    
    private void loadStats() {
        // Load statistics from API
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null) {
                        assignedStudentsLabel.setText("0"); // TODO: Calculate from students
                        pendingTasksLabel.setText("0"); // TODO: Calculate from tasks
                        completedSubmissionsLabel.setText("0"); // TODO: Calculate from submissions
                        avgGradeLabel.setText("0.0"); // TODO: Calculate from grades
                    }
                }));
    }
    
    private void loadData() {
        loadStudents();
        loadTasks();
    }
    
    private void loadStudents() {
        // TODO: Load students from API
        studentsData.clear();
    }
    
    private void loadTasks() {
        apiService.getInternships()
                .whenComplete((internships, ex) -> Platform.runLater(() -> {
                    if (ex == null && !internships.isEmpty()) {
                        // Load tasks for the first internship
                        apiService.getTasksByInternshipId(internships.get(0).getId())
                                .whenComplete((tasks, taskEx) -> Platform.runLater(() -> {
                                    if (taskEx == null) {
                                        tasksData.setAll(tasks);
                                    } else {
                                        showError("Failed to load tasks: " + taskEx.getMessage());
                                    }
                                }));
                    }
                }));
    }
    
    private void setupEventHandlers() {
        refreshBtn.setOnAction(e -> handleRefresh());
        messageStudentsBtn.setOnAction(e -> handleMessageStudents());
        createTaskBtn.setOnAction(e -> handleCreateTask());
        clearTaskFormBtn.setOnAction(e -> handleClearTaskForm());
        submitTaskFormBtn.setOnAction(e -> handleSubmitTaskForm());
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadStats();
        statusLabel.setText("Data refreshed");
    }
    
    @FXML
    private void handleMessageStudents() {
        // TODO: Open message dialog for all students
        statusLabel.setText("Messaging all students");
    }
    
    @FXML
    private void handleCreateTask() {
        mainTabPane.getSelectionModel().select(2); // Switch to Create Task tab
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
        if (!validateTaskForm()) {
            return;
        }
        
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
                        mainTabPane.getSelectionModel().select(1); // Switch to Tasks tab
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
        // TODO: Get selected internship ID from combo box or context
        return 1L; // Placeholder
    }
    
    private void handleMessageStudent(Object student) {
        // TODO: Open message dialog for specific student
        statusLabel.setText("Messaging student");
    }
    
    private void handleGradeTask(Task task) {
        // TODO: Open grading dialog
        statusLabel.setText("Grading task: " + task.getTitle());
    }
    
    private void handleEditTask(Task task) {
        // TODO: Open edit dialog
        statusLabel.setText("Editing task: " + task.getTitle());
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



