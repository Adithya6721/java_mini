package com.internship.virtualinternship.controller;

import com.internship.virtualinternship.model.Notification;
import com.internship.virtualinternship.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> listUnread(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}


