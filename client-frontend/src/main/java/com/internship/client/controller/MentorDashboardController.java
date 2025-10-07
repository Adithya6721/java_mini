package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.Internship;
import com.internship.client.model.Task;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MentorDashboardController {
    @FXML private ComboBox<Internship> internshipCombo;
    @FXML private ListView<Task> tasksList;
    @FXML private TextField taskTitleField;
    @FXML private TextArea taskDescArea;
    @FXML private Button createTaskButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    private final ObservableList<Internship> internships = FXCollections.observableArrayList();
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        internshipCombo.setItems(internships);
        internshipCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Internship item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.role() + " @ " + item.company());
            }
        });
        internshipCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Internship item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.role() + " @ " + item.company());
            }
        });
        tasksList.setItems(tasks);
        tasksList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.title());
            }
        });
        refreshButton.setOnAction(e -> loadInternships());
        logoutButton.setOnAction(e -> { AppContext.api().logout(); AppContext.getSceneManager().switchToLogin(); });
        createTaskButton.setOnAction(e -> createTask());
        loadInternships();
    }

    private void loadInternships() {
        refreshButton.setDisable(true);
        AppContext.api().getInternships()
                .whenComplete((list, ex) -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Failed to load: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        return;
                    }
                    internships.setAll(list);
                    if (!internships.isEmpty()) {
                        internshipCombo.getSelectionModel().selectFirst();
                        loadTasks();
                    }
                }));
    }

    private void loadTasks() {
        Internship sel = internshipCombo.getSelectionModel().getSelectedItem();
        if (sel == null) {
            tasks.clear();
            return;
        }
        AppContext.api().getTasksByInternshipId(sel.id())
                .whenComplete((list, ex) -> Platform.runLater(() -> {
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Failed to load tasks: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        return;
                    }
                    tasks.setAll(list);
                }));
    }

    private void createTask() {
        Internship sel = internshipCombo.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Task toCreate = new Task(null, sel.id(), taskTitleField.getText(), taskDescArea.getText());
        createTaskButton.setDisable(true);
        AppContext.api().createTask(toCreate)
                .whenComplete((created, ex) -> Platform.runLater(() -> {
                    createTaskButton.setDisable(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR, "Create failed: " + ex.getMessage(), ButtonType.OK).showAndWait();
                    } else {
                        tasks.add(0, created);
                        taskTitleField.clear();
                        taskDescArea.clear();
                        new Alert(Alert.AlertType.INFORMATION, "Task created", ButtonType.OK).showAndWait();
                    }
                }));
    }
}



