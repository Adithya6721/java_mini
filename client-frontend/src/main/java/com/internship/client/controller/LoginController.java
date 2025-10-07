package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.LoginRequest;
import com.internship.client.model.Role;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        loginButton.setOnAction(e -> onLogin());
        registerLink.setOnAction(e -> AppContext.getSceneManager().switchToRegister());
    }

    private void onLogin() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();
        loginButton.setDisable(true);
        AppContext.api().login(new LoginRequest(username, password))
                .whenComplete((resp, ex) -> Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    if (ex != null) {
                        errorLabel.setText("Login failed: " + ex.getMessage());
                        return;
                    }
                    Role role = resp.role();
                    if (role == Role.STUDENT) {
                        AppContext.getSceneManager().switchToStudentDashboard();
                    } else if (role == Role.COMPANY) {
                        AppContext.getSceneManager().switchToCompanyDashboard();
                    } else if (role == Role.MENTOR) {
                        AppContext.getSceneManager().switchToMentorDashboard();
                    } else {
                        errorLabel.setText("Unknown role");
                    }
                }));
    }
}



