package com.example.project.config;

import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role librarianRole = roleRepository.findByName("LIBRARIAN")
            .orElseGet(() -> roleRepository.save(new Role("LIBRARIAN")));
        
        Role memberRole = roleRepository.findByName("MEMBER")
            .orElseGet(() -> roleRepository.save(new Role("MEMBER")));
        
        // Create admin user if doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), 
                "Admin User", "admin@library.com");
            admin.getRoles().add(librarianRole);
            userRepository.save(admin);
            System.out.println("Admin user created: admin/admin123");
        }
        
        // Create member user if doesn't exist
        if (!userRepository.existsByUsername("user")) {
            User user = new User("user", passwordEncoder.encode("user123"), 
                "Regular User", "user@library.com");
            user.getRoles().add(memberRole);
            userRepository.save(user);
            System.out.println("Member user created: user/user123");
        }
    }
}