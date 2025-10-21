package com.internship.client.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void load(String fxml, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }

    public void switchToLogin() {
        load("/com/internship/client/view/login.fxml", 800, 600);
    }

    public void switchToStudentDashboard() {
        load("/com/internship/client/view/student_dashboard.fxml", 1024, 720);
    }

    public void switchToCompanyDashboard() {
        load("/com/internship/client/view/company_dashboard.fxml", 1024, 720);
    }

    public void switchToMentorDashboard() {
        load("/com/internship/client/view/mentor_dashboard.fxml", 1024, 720);
    }
}