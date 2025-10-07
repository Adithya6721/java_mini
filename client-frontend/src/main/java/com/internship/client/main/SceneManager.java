package com.internship.client.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    private void load(String fxmlPath, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            String css = "/com/internship/client/view/styles.css";
            scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public void switchToLogin() {
        load("/com/internship/client/view/login.fxml", 420, 560);
    }

    public void switchToRegister() {
        load("/com/internship/client/view/register.fxml", 480, 640);
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



