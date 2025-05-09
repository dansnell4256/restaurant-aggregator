package com.catalogue.controller.admin;

import com.catalogue.dto.ApiResponse;
import com.catalogue.model.Category;
import com.catalogue.model.Component;
import com.catalogue.repository.CategoryRepository;
import com.catalogue.repository.ComponentRepository;
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
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for diagnosing issues with the database and data loading
 */
@RestController
@RequestMapping("/api/v1/admin/diagnostic")
@Profile({"dev", "test", "local"})
public class DiagnosticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticController.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ComponentRepository componentRepository;

    /**
     * Diagnose database connection issues
     */
    @GetMapping("/db-connection")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkDbConnection() {
        Map<String, Object> diagnosticInfo = new HashMap<>();
        ResponseEntity<ApiResponse<Map<String, Object>>> response;

        try {
            // Test direct connection
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                diagnosticInfo.put("dbConnection", "Success");
                diagnosticInfo.put("dbProductName", metaData.getDatabaseProductName());
                diagnosticInfo.put("dbVersion", metaData.getDatabaseProductVersion());
                diagnosticInfo.put("driverName", metaData.getDriverName());
                diagnosticInfo.put("url", metaData.getURL());
            }

            // Test simple query
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            diagnosticInfo.put("simpleQuery", "Success: " + result);

            // Test table existence
            try {
                List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
                diagnosticInfo.put("tables", tables);
            } catch (Exception e) {
                LOGGER.error("Error querying tables", e);
                diagnosticInfo.put("tablesError", e.getMessage());
            }

            response = ResponseEntity.ok(
                    ApiResponse.success("Database connection diagnostics", diagnosticInfo));
        } catch (Exception e) {
            LOGGER.error("Database connection diagnostic failed", e);
            diagnosticInfo.put("error", e.getMessage());
            diagnosticInfo.put("stackTrace", getStackTrace(e));
            response = ResponseEntity.ok(
                    ApiResponse.error(500, "Database connection diagnostic failed: " + e.getMessage()));
        }

        return response;
    }

    /**
     * Check repository functionality
     */
    @GetMapping("/repositories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkRepositories() {
        Map<String, Object> diagnosticInfo = new HashMap<>();
        ResponseEntity<ApiResponse<Map<String, Object>>> response;

        try {
            // Test category repository
            try {
                List<Category> categories = categoryRepository.findAll();
                diagnosticInfo.put("categoriesCount", categories.size());
            } catch (Exception e) {
                LOGGER.error("Error accessing category repository", e);
                diagnosticInfo.put("categoryRepoError", e.getMessage());
                diagnosticInfo.put("categoryRepoStackTrace", getStackTrace(e));
            }

            // Test component repository
            try {
                List<Component> components = componentRepository.findAll();
                diagnosticInfo.put("componentsCount", components.size());
            } catch (Exception e) {
                LOGGER.error("Error accessing component repository", e);
                diagnosticInfo.put("componentRepoError", e.getMessage());
                diagnosticInfo.put("componentRepoStackTrace", getStackTrace(e));
            }

            response = ResponseEntity.ok(ApiResponse.success("Repository diagnostics", diagnosticInfo));
        } catch (Exception e) {
            LOGGER.error("Repository diagnostic failed", e);
            diagnosticInfo.put("error", e.getMessage());
            diagnosticInfo.put("stackTrace", getStackTrace(e));
            response = ResponseEntity.ok(ApiResponse.error(
                    500, "Repository diagnostic failed: " + e.getMessage()));
        }

        return response;
    }

    /**
     * Get a formatted stack trace for improved diagnostics
     */
    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(e.toString()).append('\n');

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("    at ").append(element.toString()).append('\n');
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            sb.append("Caused by: ").append(cause.toString()).append('\n');
            for (StackTraceElement element : cause.getStackTrace()) {
                sb.append("    at ").append(element.toString()).append('\n');
            }
        }

        return sb.toString();
    }
}
