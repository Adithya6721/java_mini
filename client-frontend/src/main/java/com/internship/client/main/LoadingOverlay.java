package com.internship.client.main;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoadingOverlay extends StackPane {
    private final ProgressIndicator indicator;
    private final Label messageLabel;

    public LoadingOverlay() {
        indicator = new ProgressIndicator();
        messageLabel = new Label("Loading...");
        VBox content = new VBox(10, indicator, messageLabel);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("loading-overlay");
        getChildren().add(content);
        setVisible(false);
        setPickOnBounds(false);
    }

    public void show(String message) {
        messageLabel.setText(message);
        setVisible(true);
        setPickOnBounds(true);
    }

    public void hide() {
        setVisible(false);
        setPickOnBounds(false);
    }
}


