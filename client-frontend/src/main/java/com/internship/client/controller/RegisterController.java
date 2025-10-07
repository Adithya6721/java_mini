package com.internship.client.controller;

import com.internship.client.main.AppContext;
import com.internship.client.model.RegisterRequest;
import com.internship.client.model.Role;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        messageLabel.setText("");
        registerButton.setOnAction(e -> onRegister());
        loginLink.setOnAction(e -> AppContext.getSceneManager().switchToLogin());
    }

    private void onRegister() {
        messageLabel.setText("");
        RegisterRequest req = new RegisterRequest(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                roleCombo.getValue()
        );
        registerButton.setDisable(true);
        AppContext.api().register(req)
                .whenComplete((ignored, ex) -> Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    if (ex != null) {
                        messageLabel.setText("Registration failed: " + ex.getMessage());
                    } else {
                        messageLabel.setText("Registration successful. Please login.");
                    }
                }));
    }
}



