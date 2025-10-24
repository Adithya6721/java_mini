package com.internship.virtualinternship;

import com.internship.virtualinternship.model.*;
import com.internship.virtualinternship.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Calendar;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          InternshipRepository internshipRepository,
                          ApplicationRepository applicationRepository,
                          TaskRepository taskRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Always reinitialize for testing (comment out this condition to keep existing data)
        if (userRepository.count() > 0) {
            System.out.println("Database already has data. Skipping initialization.");
            return;
        }

        System.out.println("Initializing database with sample data...");

        // Create sample users
        User student1 = createUser("student", "student@example.com", "password", Role.STUDENT);
        User student2 = createUser("student2", "student2@example.com", "password", Role.STUDENT);
        User company = createUser("company", "company@example.com", "password", Role.COMPANY);
        User mentor1 = createUser("mentor", "mentor@example.com", "password", Role.MENTOR);
        User mentor2 = createUser("mentor2", "mentor2@example.com", "password", Role.MENTOR);

        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(company);
        userRepository.save(mentor1);
        userRepository.save(mentor2);
        System.out.println("Created users: student (ID: " + student1.getId() + "), company (ID: " + company.getId() + "), mentor (ID: " + mentor1.getId() + ")");

        // Create sample internships
        Internship internship1 = createInternship(
            "Software Engineering Intern", 
            "Work on cutting-edge software development projects using Java and Spring Boot. Build RESTful APIs and work with databases.", 
            "Java, Spring Boot, SQL, Git", 
            company,
            30
        );
        
        Internship internship2 = createInternship(
            "Data Science Intern", 
            "Analyze data and build machine learning models. Work with Python, Pandas, and Scikit-learn.", 
            "Python, Machine Learning, Statistics, Pandas", 
            company,
            45
        );

        Internship internship3 = createInternship(
            "Full Stack Developer Intern", 
            "Develop web applications using React and Node.js. Work on both frontend and backend.", 
            "React, Node.js, JavaScript, MongoDB", 
            company,
            60
        );

        internshipRepository.save(internship1);
        internshipRepository.save(internship2);
        internshipRepository.save(internship3);
        System.out.println("Created internships: " + internship1.getTitle() + " (ID: " + internship1.getId() + ")");

        // Create applications from students
        Application app1 = createApplication(
            student1, 
            internship1, 
            "I am very interested in this software engineering position and have strong Java skills.",
            Application.Status.ACCEPTED
        );
        
        Application app2 = createApplication(
            student2, 
            internship1, 
            "I would love to join your team and contribute to your projects.",
            Application.Status.UNDER_REVIEW
        );
        
        Application app3 = createApplication(
            student1, 
            internship2, 
            "I have experience with Python and machine learning projects.",
            Application.Status.APPLIED
        );

        applicationRepository.save(app1);
        applicationRepository.save(app2);
        applicationRepository.save(app3);
        System.out.println("Created applications for students");

        // Create sample tasks for accepted internship
        Task task1 = createTask(
            "Setup Development Environment",
            "Install Java 17, Spring Boot, and MySQL. Clone the project repository and run it locally.",
            internship1,
            mentor1,
            7 // 7 days from now
        );

        Task task2 = createTask(
            "Build REST API for User Management",
            "Create CRUD endpoints for user management. Include proper validation and error handling.",
            internship1,
            mentor1,
            14 // 14 days from now
        );

        Task task3 = createTask(
            "Implement Database Integration",
            "Connect the application to MySQL database. Create entity models and repositories using Spring Data JPA.",
            internship1,
            mentor1,
            21 // 21 days from now
        );

        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        System.out.println("Created tasks for internship: " + internship1.getTitle());

        System.out.println("Database initialization completed successfully!");
        System.out.println("\nLogin credentials:");
        System.out.println("  Student: student / password");
        System.out.println("  Company: company / password");
        System.out.println("  Mentor: mentor / password");
    }

    private User createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }

    private Internship createInternship(String title, String description, String requirements, User company, int daysFromNow) {
        Internship internship = new Internship();
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setRequirements(requirements);
        internship.setDeadline(new Date(System.currentTimeMillis() + daysFromNow * 24L * 60 * 60 * 1000));
        internship.setCompany(company);
        return internship;
    }

    private Application createApplication(User student, Internship internship, String coverLetter, Application.Status status) {
        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setCoverLetter(coverLetter);
        application.setStatus(status);
        application.setAppliedDate(new Date());
        application.setResumeUrl("https://example.com/resume.pdf");
        return application;
    }

    private Task createTask(String title, String description, Internship internship, User mentor, int daysFromNow) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setInternship(internship);
        task.setMentor(mentor);
        task.setDueDate(new Date(System.currentTimeMillis() + daysFromNow * 24L * 60 * 60 * 1000));
        return task;
    }
}


