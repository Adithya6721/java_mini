package com.internship.client.main;

import com.internship.client.model.User;
import com.internship.client.service.ApiService;
import com.internship.client.service.TokenManager;

public final class AppContext {
    private static SceneManager sceneManager;
    private static User currentUser;

    private AppContext() {}

    public static void setSceneManager(SceneManager manager) {
        sceneManager = manager;
    }

    public static SceneManager getSceneManager() {
        if (sceneManager == null) {
            throw new IllegalStateException("SceneManager not initialized");
        }
        return sceneManager;
    }

    public static ApiService api() {
        return ApiService.getInstance();
    }

    public static TokenManager tokens() {
        return TokenManager.getInstance();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}



