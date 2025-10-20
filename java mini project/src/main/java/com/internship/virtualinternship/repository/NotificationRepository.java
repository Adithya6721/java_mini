package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.Notification;
import com.internship.virtualinternship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);
}


