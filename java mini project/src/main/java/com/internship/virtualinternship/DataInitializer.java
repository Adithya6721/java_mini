package com.internship.virtualinternship;

import com.internship.virtualinternship.model.Internship;
import com.internship.virtualinternship.model.Role;
import com.internship.virtualinternship.model.User;
import com.internship.virtualinternship.repository.InternshipRepository;
import com.internship.virtualinternship.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          InternshipRepository internshipRepository, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Database already has data
        }

        // Create sample users
        User student = createUser("student", "student@example.com", "password", Role.STUDENT);
        User company = createUser("company", "company@example.com", "password", Role.COMPANY);
        User mentor = createUser("mentor", "mentor@example.com", "password", Role.MENTOR);

        userRepository.save(student);
        userRepository.save(company);
        userRepository.save(mentor);

        // Create sample internships
        Internship internship1 = createInternship("Software Engineering Intern", 
            "Work on cutting-edge software development projects", 
            "Java, Spring Boot, SQL", company);
        
        Internship internship2 = createInternship("Data Science Intern", 
            "Analyze data and build machine learning models", 
            "Python, Machine Learning, Statistics", company);

        internshipRepository.save(internship1);
        internshipRepository.save(internship2);
    }

    private User createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private Internship createInternship(String title, String description, String requirements, User company) {
        Internship internship = new Internship();
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setRequirements(requirements);
        internship.setDeadline(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // 30 days from now
        internship.setCompany(company);
        return internship;
    }
}


