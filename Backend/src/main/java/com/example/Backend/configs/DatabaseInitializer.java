package com.example.Backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Check if database is already initialized
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users LIMIT 1", Integer.class);
            System.out.println("Database already initialized, skipping schema creation");
            return;
        } catch (Exception e) {
            System.out.println("Database not initialized, creating schema...");
        }

        // Read and execute schema.sql
        try {
            ClassPathResource resource = new ClassPathResource("schema.sql");
            String sql = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            // Execute the SQL script
            jdbcTemplate.execute(sql);
            System.out.println("Database schema created successfully!");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize database schema: " + e.getMessage());
            throw e;
        }
    }
}
