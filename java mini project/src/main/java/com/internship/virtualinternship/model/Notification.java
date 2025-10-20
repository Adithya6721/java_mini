package com.internship.virtualinternship.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {

    public enum Type { INFO, SUCCESS, WARNING, ERROR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.INFO;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    private boolean isRead;    

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}


