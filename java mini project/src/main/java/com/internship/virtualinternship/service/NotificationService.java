package com.internship.virtualinternship.service;

import com.internship.virtualinternship.model.Notification;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.NotificationRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(Long userId, String message, Notification.Type type) {
        User user = userRepository.findById(userId).orElseThrow();
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setType(type);
        return notificationRepository.save(n);
    }

    public List<Notification> getUnread(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return notificationRepository.findByUserAndIsReadIsFalse(user);
    }

    public long getUnreadCount(Long userId) {
        return getUnread(userId).size();
    }

    public Notification markAsRead(Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setIsRead(true);
        return notificationRepository.save(n);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> list = getUnread(userId);
        for (Notification n : list) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(list);
    }
}


