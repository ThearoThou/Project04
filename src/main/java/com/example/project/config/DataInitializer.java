package com.library.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        log.info("==============================================");
        log.info("Database initialized via Flyway migrations");
        log.info("Default users created:");
        log.info("  - Admin: admin/admin123 (LIBRARIAN role)");
        log.info("  - Member: user/user123 (MEMBER role)");
        log.info("Sample books have been added to the library");
        log.info("==============================================");
    }
}