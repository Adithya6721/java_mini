package com.internship.client.main;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AsyncTaskExecutor {
    private AsyncTaskExecutor() {}

    public static <T> void run(Supplier<T> supplier, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return supplier.get();
            }
        };

        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));

        Thread thread = new Thread(task, "async-exec");
        thread.setDaemon(true);
        thread.start();
    }

    public static <T> void fromFuture(CompletableFuture<T> future, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        future.whenComplete((res, ex) -> Platform.runLater(() -> {
            if (ex != null) {
                onError.accept(ex);
            } else {
                onSuccess.accept(res);
            }
        }));
    }
}


