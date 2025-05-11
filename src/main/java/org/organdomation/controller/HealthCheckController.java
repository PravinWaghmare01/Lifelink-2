package org.organdomation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health checks and system status
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Basic health check endpoint that returns system status
     * 
     * @return Health status information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "LifeLink Organ Donation System");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Database health check endpoint
     * 
     * @return Database connection status
     */
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        // First try direct JDBC connection test
        try (Connection conn = dataSource.getConnection()) {
            boolean isValid = conn.isValid(5); // 5 second timeout
            
            response.put("status", isValid ? "UP" : "DOWN");
            response.put("database", "PostgreSQL");
            response.put("connection", isValid ? "Successful" : "Failed");
            response.put("connectionMethod", "Direct JDBC");
            
            logger.info("Database health check (JDBC): {}", isValid ? "SUCCESS" : "FAILED");
            
            if (isValid) {
                return ResponseEntity.ok(response);
            }
        } catch (SQLException e) {
            logger.warn("Direct JDBC connection test failed, falling back to JdbcTemplate", e);
        }
        
        // Fall back to JdbcTemplate if direct connection fails
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            boolean dbConnectionSuccess = result != null && result == 1;
            
            response.put("status", dbConnectionSuccess ? "UP" : "DOWN");
            response.put("database", "PostgreSQL");
            response.put("connection", dbConnectionSuccess ? "Successful" : "Failed");
            response.put("connectionMethod", "JdbcTemplate");
            
            logger.info("Database health check (JdbcTemplate): {}", dbConnectionSuccess ? "SUCCESS" : "FAILED");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database health check failed with both methods", e);
            
            response.put("status", "DOWN");
            response.put("database", "MySQL");
            response.put("connection", "Failed");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(503).body(response);
        }
    }
    
    /**
     * Database configuration check endpoint
     * 
     * @return Database configuration information
     */
    @GetMapping("/database/config")
    public ResponseEntity<Map<String, Object>> databaseConfigCheck() {
        Map<String, Object> response = new HashMap<>();
        
        // Safely retrieve configuration (excluding sensitive data)
        response.put("driverClass", env.getProperty("spring.datasource.driver-class-name"));
        response.put("host", env.getProperty("PGHOST")); 
        response.put("port", env.getProperty("PGPORT"));
        response.put("database", env.getProperty("PGDATABASE"));
        
        // Just indicate if credentials are set without exposing them
        response.put("usernameSet", env.getProperty("PGUSER") != null);
        response.put("passwordSet", env.getProperty("PGPASSWORD") != null);
        
        // Add Hibernate dialect information
        response.put("hibernateDialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        response.put("ddlAuto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Environment check endpoint
     * 
     * @return Environment information
     */
    @GetMapping("/env")
    public ResponseEntity<Map<String, Object>> environmentCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("java.version", System.getProperty("java.version"));
        response.put("os.name", System.getProperty("os.name")); 
        response.put("os.version", System.getProperty("os.version"));
        response.put("user.timezone", System.getProperty("user.timezone"));
        response.put("spring.profiles.active", env.getProperty("spring.profiles.active", "default"));
        response.put("application.name", env.getProperty("spring.application.name"));
        response.put("server.port", env.getProperty("server.port"));
        
        return ResponseEntity.ok(response);
    }
}