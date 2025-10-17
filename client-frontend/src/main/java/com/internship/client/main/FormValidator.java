package com.internship.client.main;

import javafx.scene.control.TextField;

public final class FormValidator {
    private FormValidator() {}

    public static boolean validateNotEmpty(TextField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showFieldError(field);
            return false;
        }
        clearFieldError(field);
        return true;
    }

    public static boolean validateEmail(TextField field) {
        String email = field.getText() == null ? "" : field.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showFieldError(field);
            return false;
        }
        clearFieldError(field);
        return true;
    }

    private static void showFieldError(TextField field) {
        if (!field.getStyleClass().contains("error")) {
            field.getStyleClass().add("error");
        }
    }

    private static void clearFieldError(TextField field) {
        field.getStyleClass().remove("error");
    }
}


