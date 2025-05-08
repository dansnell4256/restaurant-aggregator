package com.catalogue.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Very simple diagnostic controller for database issues
 */
@RestController
@RequestMapping("/api/simple-db")
@Profile("local")
public class SimpleDbDiagnostic {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbDiagnostic.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new HashMap<>();

        // Test 1: Direct connection check
        try (Connection conn = dataSource.getConnection()) {
            result.put("connection", "Success - connected to " + conn.getMetaData().getURL());
        } catch (Exception e) {
            LOGGER.error("Connection test failed", e);
            result.put("connection", "Failed: " + e.getMessage());
        }

        // Test 2: Simple query
        try {
            Integer queryResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            result.put("simpleQuery", "Success - result: " + queryResult);
        } catch (Exception e) {
            LOGGER.error("Simple query failed", e);
            result.put("simpleQuery", "Failed: " + e.getMessage());
        }

        // Test 3: Create a very simple table
        try {
            // Use executeUpdate for DDL statements to avoid ambiguity
            jdbcTemplate.update("CREATE TABLE IF NOT EXISTS SIMPLE_TEST (ID INT)");
            result.put("createTable", "Success");
        } catch (Exception e) {
            LOGGER.error("Create table failed", e);
            result.put("createTable", "Failed: " + e.getMessage());
        }

        // Test 4: Insert into the table
        try {
            // Use update instead of execute for DML operations
            jdbcTemplate.update("INSERT INTO SIMPLE_TEST VALUES (1)");
            result.put("insert", "Success");
        } catch (Exception e) {
            LOGGER.error("Insert failed", e);
            result.put("insert", "Failed: " + e.getMessage());
        }

        // Test 5: Query the table
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SIMPLE_TEST", Integer.class);
            result.put("queryTable", "Success - count: " + count);
        } catch (Exception e) {
            LOGGER.error("Query table failed", e);
            result.put("queryTable", "Failed: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}
