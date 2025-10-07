package com.internship.client.main;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        AppContext.setSceneManager(sceneManager);
        sceneManager.switchToLogin();
        primaryStage.setTitle("Virtual Internship");
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/internship/client/view/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception ignored) {
            // optional icon
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


