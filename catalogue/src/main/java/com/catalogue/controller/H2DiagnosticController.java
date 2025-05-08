package com.catalogue.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for diagnosing H2 database and connection issues
 */
@RestController
@RequestMapping("/api/diagnostics")
public class H2DiagnosticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2DiagnosticController.class);

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get detailed information about the database configuration and connection
     * @return Map containing database configuration details
     */
    @GetMapping("/db-info")
    public Map<String, Object> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();

        // Get active profiles
        info.put("activeProfiles", Arrays.asList(env.getActiveProfiles()));

        // Get database configuration from properties
        info.put("datasource.url", env.getProperty("spring.datasource.url"));
        info.put("datasource.driver", env.getProperty("spring.datasource.driver-class-name",
                env.getProperty("spring.datasource.driverClassName")));
        info.put("datasource.username", env.getProperty("spring.datasource.username"));
        info.put("h2console.enabled", env.getProperty("spring.h2.console.enabled"));
        info.put("h2console.path", env.getProperty("spring.h2.console.path"));

        // Get connection information
        addConnectionInfo(info);

        // Test query execution
        addQueryTestInfo(info);

        // Get H2 console status
        addH2ConsoleInfo(info);

        return info;
    }

    /**
     * Add database connection information to the info map
     * @param info The map to add information to
     */
    private void addConnectionInfo(Map<String, Object> info) {
        try (Connection conn = dataSource.getConnection()) {
            info.put("connection.url", conn.getMetaData().getURL());
            info.put("connection.username", conn.getMetaData().getUserName());
            info.put("connection.productName", conn.getMetaData().getDatabaseProductName());
            info.put("connection.productVersion", conn.getMetaData().getDatabaseProductVersion());

            // Add database properties
            try {
                Map<String, String> dbProps = new HashMap<>();
                conn.getMetaData().getDatabaseMajorVersion();
                dbProps.put("majorVersion", String.valueOf(conn.getMetaData().getDatabaseMajorVersion()));
                dbProps.put("minorVersion", String.valueOf(conn.getMetaData().getDatabaseMinorVersion()));
                dbProps.put("autoCommit", String.valueOf(conn.getAutoCommit()));
                dbProps.put("readOnly", String.valueOf(conn.isReadOnly()));
                dbProps.put("transactionIsolation", String.valueOf(conn.getTransactionIsolation()));
                info.put("connection.properties", dbProps);
            } catch (Exception e) {
                LOGGER.warn("Error getting database properties", e);
                info.put("connection.propertiesError", e.getMessage());
            }
        } catch (SQLException e) {
            LOGGER.error("Error accessing database connection", e);
            info.put("connection.error", e.getMessage());
            info.put("connection.stackTrace", getStackTraceAsString(e));
        }
    }

    /**
     * Add query test information to the info map
     * @param info The map to add information to
     */
    private void addQueryTestInfo(Map<String, Object> info) {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            info.put("query.testResult", result);
        } catch (Exception e) {
            LOGGER.error("Error executing test query", e);
            info.put("query.error", e.getMessage());
            info.put("query.stackTrace", getStackTraceAsString(e));
        }
    }

    /**
     * Add H2 console information to the info map
     * @param info The map to add information to
     */
    private void addH2ConsoleInfo(Map<String, Object> info) {
        if (Boolean.parseBoolean(env.getProperty("spring.h2.console.enabled", "false"))) {
            try {
                String h2ConsoleUrl = "http://localhost:" + env.getProperty("server.port", "8081") +
                        env.getProperty("spring.h2.console.path", "/h2-console");
                info.put("h2console.url", h2ConsoleUrl);
                info.put("h2console.status", "Enabled");

                // Provide connection instructions
                Map<String, String> connectionInstructions = new HashMap<>();
                connectionInstructions.put("jdbcUrl", info.get("datasource.url").toString());
                connectionInstructions.put("username", info.get("datasource.username").toString());
                connectionInstructions.put("password", "password (default)");
                info.put("h2console.connectionInstructions", connectionInstructions);
            } catch (Exception e) {
                LOGGER.warn("Error getting H2 console status", e);
                info.put("h2console.statusError", e.getMessage());
            }
        } else {
            info.put("h2console.status", "Disabled");
        }
    }

    /**
     * Utility method to get stack trace as string
     * @param e The exception to get the stack trace from
     * @return A string representation of the stack trace
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
