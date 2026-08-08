package com.school.management.config;

import com.school.management.entity.User;
import com.school.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User user = userRepository.findByEmail("admin@school.com").orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setActive(true);
            userRepository.save(user);
        } else {
            User admin = new User();
            admin.setEmail("admin@school.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
        }
    }
}
