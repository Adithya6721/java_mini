package com.internship.virtualinternship.repository;

import com.internship.virtualinternship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email The email to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     * This is needed for the registration check to prevent duplicate usernames.
     * @param username The username to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByUsername(String username);
}

