package com.catalogue.controller;

import com.catalogue.model.EntityDiagnostic;
import com.catalogue.repository.RepositoryDiagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to diagnose JPA functionality.
 * Only available in local development environment.
 */
@RestController
@RequestMapping("/api/jpa-diagnostic")
@Profile("local")
public class JpaControllerDiagnostic {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaControllerDiagnostic.class);

    @Autowired
    private RepositoryDiagnostic repositoryDiagnostic;

    /**
     * Get all diagnostic entities.
     *
     * @return ResponseEntity containing the list of entities and metadata
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEntities() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            List<EntityDiagnostic> entities = repositoryDiagnostic.findAll();
            response.put("entities", entities);
            response.put("count", entities.size());
            response.put("success", true);
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error retrieving entities", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Create sample diagnostic entities.
     *
     * @return ResponseEntity containing the result of the operation
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDiagnosticEntities() {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        try {
            List<EntityDiagnostic> createdEntities = createAndSaveEntities();

            response.put("success", true);
            response.put("message", "Diagnostic entities created successfully");
            response.put("entitiesCreated", createdEntities.size());
            response.put("entityIds", extractEntityIds(createdEntities));
            responseEntity = ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error creating diagnostic entities", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return responseEntity;
    }

    /**
     * Helper method to create and save diagnostic entities.
     *
     * @return List of created entities
     */
    private List<EntityDiagnostic> createAndSaveEntities() {
        // Create sample diagnostic entities
        EntityDiagnostic entity1 = new EntityDiagnostic("Entity 1", "Description 1");
        EntityDiagnostic entity2 = new EntityDiagnostic("Entity 2", "Description 2");
        EntityDiagnostic entity3 = new EntityDiagnostic("Entity 3", "Description 3");

        // Save entities individually to ensure each one is saved even if others fail
        LOGGER.debug("Saving entity 1: {}", entity1.getName());
        repositoryDiagnostic.save(entity1);

        LOGGER.debug("Saving entity 2: {}", entity2.getName());
        repositoryDiagnostic.save(entity2);

        LOGGER.debug("Saving entity 3: {}", entity3.getName());
        repositoryDiagnostic.save(entity3);

        // Return all entities from the database to confirm they were saved
        return repositoryDiagnostic.findAll();
    }

    /**
     * Extract entity IDs from a list of entities.
     *
     * @param entities List of entities
     * @return List of entity IDs
     */
    private List<Long> extractEntityIds(List<EntityDiagnostic> entities) {
        return entities.stream()
                .map(EntityDiagnostic::getId)
                .toList();
    }

    /**
     * Utility method to get stack trace as string.
     *
     * @param e Exception to convert
     * @return String representation of the stack trace
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
