package com.internship.client.main;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

public final class NotificationToast {
    private NotificationToast() {}

    public static void show(Scene scene, String message) { show(scene, message, null); }

    public static void show(Scene scene, String message, String styleClass) {
        Platform.runLater(() -> {
            Popup popup = new Popup();
            Label label = new Label(message);
            label.getStyleClass().add("toast");
            if (styleClass != null) label.getStyleClass().add(styleClass);
            StackPane container = new StackPane(label);
            container.setPadding(new Insets(8));

            VBox box = new VBox(container);
            box.setFillWidth(false);
            box.setAlignment(Pos.TOP_RIGHT);
            box.setPadding(new Insets(16));
            popup.getContent().add(box);
            popup.setAutoHide(true);
            popup.show(scene.getWindow());

            TranslateTransition slide = new TranslateTransition(Duration.millis(200), box);
            slide.setFromY(-20);
            slide.setToY(0);
            slide.play();

            FadeTransition fade = new FadeTransition(Duration.millis(250), box);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setDelay(Duration.seconds(2.5));
            fade.setOnFinished(e -> popup.hide());
            fade.play();
        });
    }
}


