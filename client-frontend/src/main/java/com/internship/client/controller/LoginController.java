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
    @FXML private ProgressIndicator loginSpinner;

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
        loginSpinner.setVisible(true);
        AppContext.api().login(new LoginRequest(username, password))
                .whenComplete((resp, ex) -> Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginSpinner.setVisible(false);
                    if (ex != null) {
                        errorLabel.setText("Login failed: " + ex.getMessage());
                        return;
                    }
                    Role role = resp.role();
                    String userRole = role != null ? role.name() : "";
                    switch (userRole) {
                        case "STUDENT":
                            AppContext.getSceneManager().switchToStudentDashboard();
                            break;
                        case "COMPANY":
                            AppContext.getSceneManager().switchToCompanyDashboard();
                            break;
                        case "MENTOR":
                            AppContext.getSceneManager().switchToMentorDashboard();
                            break;
                        default:
                            errorLabel.setText("Invalid role");
                    }
                }));
    }
}



