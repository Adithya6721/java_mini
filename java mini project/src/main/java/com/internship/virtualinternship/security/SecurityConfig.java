package com.internship.virtualinternship.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the application.
 * This class sets up the security rules for API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Creates a BCryptPasswordEncoder bean to be used for hashing passwords.
     * @return A PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * This is the central place to define security rules.
     *
     * @param http The HttpSecurity object to configure.
     * @return A SecurityFilterChain instance.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable Cross-Site Request Forgery (CSRF) protection,
            // which is common for stateless REST APIs.
            .csrf(csrf -> csrf.disable())

            // Configure authorization rules for HTTP requests.
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access the registration endpoint.
                .requestMatchers("/api/auth/register").permitAll()
                // Require authentication for any other request.
                .anyRequest().authenticated()
            )

            // Configure session management to be stateless.
            // This means the server won't create or use HTTP sessions,
            // which is ideal for REST APIs that use tokens for authentication.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}

