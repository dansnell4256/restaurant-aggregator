package com.catalogue.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to diagnose and initialize the database
 * Only available in local development environment
 */
@RestController
@RequestMapping("/api/db-diagnostic")
@Profile("local")
public class DbControllerDiagnostic {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbControllerDiagnostic.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get information about the database tables
     */
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getTables() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            // List all tables in the database
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
            response.put("tables", tables);

            // For each table, count the number of rows
            for (Map<String, Object> table : tables) {
                String tableName = (String) table.get("TABLE_NAME");
                try {
                    Integer count = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM " + tableName, Integer.class);
                    table.put("rowCount", count);
                } catch (Exception e) {
                    LOGGER.warn("Error counting rows in table {}: {}", tableName, e.getMessage());
                    table.put("rowCount", "Error: " + e.getMessage());
                }
            }

            response.put("success", true);
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error listing tables", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Initialize the database with a simple diagnostic table
     */
    @PostMapping("/init-sample")
    public ResponseEntity<Map<String, Object>> initializeSampleTable() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            // Create a simple diagnostic table - use quoted identifiers to avoid case sensitivity issues
            LOGGER.info("Creating diagnostic_table");
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS \"diagnostic_table\" (id INT PRIMARY KEY, name VARCHAR(255))");

            // Insert some diagnostic data - use separate statements to identify which one might fail
            LOGGER.info("Clearing existing data from diagnostic_table");
            try {
                jdbcTemplate.execute("DELETE FROM \"diagnostic_table\"");
            } catch (Exception e) {
                LOGGER.warn("Error deleting from diagnostic_table: {}", e.getMessage());
                response.put("deleteError", e.getMessage());
            }

            processInsertOperations(response);

            // Check if the table was created and data was inserted
            try {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM \"diagnostic_table\"", Integer.class);
                response.put("rowCount", count);
            } catch (Exception e) {
                LOGGER.warn("Error counting rows: {}", e.getMessage());
                response.put("countError", e.getMessage());
            }

            response.put("success", true);
            response.put("message", "Diagnostic table initialized successfully");
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error initializing diagnostic table", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Helper method to process insert operations
     */
    private void processInsertOperations(Map<String, Object> response) {
        LOGGER.info("Inserting row 1");
        try {
            jdbcTemplate.execute("INSERT INTO \"diagnostic_table\" VALUES (1, 'Sample 1')");
        } catch (Exception e) {
            LOGGER.warn("Error inserting row 1: {}", e.getMessage());
            response.put("insert1Error", e.getMessage());
        }

        LOGGER.info("Inserting row 2");
        try {
            jdbcTemplate.execute("INSERT INTO \"diagnostic_table\" VALUES (2, 'Sample 2')");
        } catch (Exception e) {
            LOGGER.warn("Error inserting row 2: {}", e.getMessage());
            response.put("insert2Error", e.getMessage());
        }

        LOGGER.info("Inserting row 3");
        try {
            jdbcTemplate.execute("INSERT INTO \"diagnostic_table\" VALUES (3, 'Sample 3')");
        } catch (Exception e) {
            LOGGER.warn("Error inserting row 3: {}", e.getMessage());
            response.put("insert3Error", e.getMessage());
        }
    }

    /**
     * Try a direct SQL query to diagnose issues
     */
    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            // Execute a simple query
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");
            response.put("result", result);
            response.put("success", true);
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error executing query", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Get database metadata
     */
    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getDatabaseMetadata() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                try {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                    metadata.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
                    metadata.put("driverName", connection.getMetaData().getDriverName());
                    metadata.put("driverVersion", connection.getMetaData().getDriverVersion());
                    metadata.put("url", connection.getMetaData().getURL());
                    metadata.put("username", connection.getMetaData().getUserName());
                    response.put("metadata", metadata);
                } catch (SQLException e) {
                    LOGGER.error("Error getting database metadata", e);
                    response.put("metadataError", e.getMessage());
                }
                return null;
            });

            response.put("success", true);
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error getting database metadata", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Utility method to get stack trace as string
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append('\n');
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append('\n');
        }
        return sb.toString();
    }
}
