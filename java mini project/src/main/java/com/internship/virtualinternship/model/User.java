

package com.internship.virtualinternship.model;

import jakarta.persistence.*;

/**
 * Represents a user in the system (Student, Mentor, or Company).
 * This class is an @Entity, meaning it will be mapped to a database table named 'users'.
 */
@Entity
@Table(name = "users") // Specifies the table name in the database
public class User {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the way the ID is generated (auto-increment)
    private Long id;

    @Column(nullable = false, unique = true) // Ensures the username is never null and is unique
    private String username;

    @Column(nullable = false) // Ensures the password is never null
    private String password;

    @Column(nullable = false, unique = true) // Ensures the email is never null and is unique
    private String email;

    @Enumerated(EnumType.STRING) // Specifies that the enum should be stored as a string (e.g., "ROLE_STUDENT")
    @Column(nullable = false) // Ensures the role is never null
    private Role role;

    // --- Getters and Setters ---
    // These methods are used to access and modify the private fields of the class,
    // a core principle of Encapsulation in OOP.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}


