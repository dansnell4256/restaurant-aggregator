package com.catalogue.util;

import com.catalogue.model.Category;
import com.catalogue.model.CategoryItem;
import com.catalogue.model.CategoryItemCustomization;
import com.catalogue.model.Component;
import com.catalogue.repository.CategoryItemCustomizationRepository;
import com.catalogue.repository.CategoryItemRepository;
import com.catalogue.repository.CategoryRepository;
import com.catalogue.repository.ComponentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to load sample data into the catalogue service database.
 * This will only run when using the "dev" or "test" profiles.
 *
 * Usage:
 * Add either --spring.profiles.active=dev or --spring.profiles.active=test
 * to your run configuration to enable this data loader.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
@org.springframework.stereotype.Component
@Profile({"dev", "test", "local"}) // Added "local" profile
public class SampleDataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataLoader.class);
    private static final String SAMPLE_DATA_FILE = "data/restaurant-test-data.json";

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryItemRepository categoryItemRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private CategoryItemCustomizationRepository customizationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Loads sample data on application startup.
     * This method is called by Spring Boot when the application starts.
     *
     * @param args Command line arguments
     */
    @Override
    public void run(String... args) {
        try {
            LOGGER.info("Checking if sample data should be loaded on startup...");

            // Verify database connection first
            checkDatabaseConnection();

            loadSampleData();
            LOGGER.info("Sample data loaded successfully on startup");
        } catch (Exception e) {
            LOGGER.error("Failed to load sample data on startup", e);
            // Don't rethrow the exception to allow the application to start
        }
    }

    /**
     * Check database connection before attempting to load data
     *
     * @throws SQLException if there is a database connection issue
     * @throws IOException if there is an I/O issue
     */
    private void checkDatabaseConnection() throws SQLException, IOException {
        LOGGER.info("Checking database connection...");

        DatabaseMetaData metaData;

        try (Connection conn = dataSource.getConnection()) {
            metaData = conn.getMetaData();
            LOGGER.info("Connected to database: {}, version: {}",
                    metaData.getDatabaseProductName(),
                    metaData.getDatabaseProductVersion());

            // Test a simple query
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            LOGGER.info("Database query test successful: {}", result);
        }
    }

    /**
     * Loads sample data into the database.
     * This method can be called manually to reload sample data without restarting the application.
     *
     * @throws IOException if there is an I/O issue
     * @throws SQLException if there is a database issue
     */
    @Transactional
    public void loadSampleData() throws IOException, SQLException {
        LOGGER.info("Loading sample data...");

        // Verify the sample data file exists
        Resource resource = new ClassPathResource(SAMPLE_DATA_FILE);
        if (!resource.exists()) {
            String error = "Sample data file not found: " + SAMPLE_DATA_FILE;
            LOGGER.error(error);
            throw new IOException(error);
        }

        LOGGER.info("Sample data file found: {}", resource.getFilename());

        // Clear existing data first
        clearExistingData();

        // Load sample data from JSON file
        JsonNode rootNode;
        try (InputStream inputStream = resource.getInputStream()) {
            LOGGER.info("Parsing JSON data file...");
            rootNode = objectMapper.readTree(inputStream);
            LOGGER.info("JSON data parsed successfully");
        }

        // Load components first
        LOGGER.info("Loading components...");
        Map<String, Component> componentMap = loadComponents(rootNode.get("components"));
        LOGGER.info("Loaded {} components", componentMap.size());

        // Load categories
        LOGGER.info("Loading categories...");
        Map<String, Category> categoryMap = loadCategories(rootNode.get("categories"));
        LOGGER.info("Loaded {} categories", categoryMap.size());

        // Load category items
        LOGGER.info("Loading category items...");
        Map<String, CategoryItem> categoryItemMap = loadCategoryItems(
                rootNode.get("categoryItems"), categoryMap, componentMap);
        LOGGER.info("Loaded {} category items", categoryItemMap.size());

        // Load customizations
        LOGGER.info("Loading customizations...");
        int customizationsCount = loadCustomizations(
                rootNode.get("categoryItemCustomizations"), categoryItemMap, componentMap);
        LOGGER.info("Loaded {} customizations", customizationsCount);

        LOGGER.info("Sample data loaded successfully");
    }

    private void clearExistingData() {
        LOGGER.info("Clearing existing data...");

        // Use try-catch blocks to handle errors for each repository separately
        try {
            LOGGER.info("Clearing customizations...");
            customizationRepository.deleteAll();
            LOGGER.info("Customizations cleared successfully");
        } catch (Exception e) {
            LOGGER.warn("Error clearing customizations: {}", e.getMessage());
            // Continue with other clearings
        }

        try {
            LOGGER.info("Clearing category items...");
            categoryItemRepository.deleteAll();
            LOGGER.info("Category items cleared successfully");
        } catch (Exception e) {
            LOGGER.warn("Error clearing category items: {}", e.getMessage());
        }

        try {
            LOGGER.info("Clearing categories...");
            categoryRepository.deleteAll();
            LOGGER.info("Categories cleared successfully");
        } catch (Exception e) {
            LOGGER.warn("Error clearing categories: {}", e.getMessage());
        }

        try {
            LOGGER.info("Clearing components...");
            componentRepository.deleteAll();
            LOGGER.info("Components cleared successfully");
        } catch (Exception e) {
            LOGGER.warn("Error clearing components: {}", e.getMessage());
        }

        LOGGER.info("Data clearing completed");
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private Map<String, Component> loadComponents(JsonNode componentsNode) {
        Map<String, Component> componentMap = new HashMap<>();

        if (componentsNode == null || !componentsNode.isArray() || componentsNode.isEmpty()) {
            LOGGER.warn("No components found in sample data or invalid format");
            return componentMap;
        }

        LOGGER.info("Found {} components in sample data", componentsNode.size());

        List<Component> components = new ArrayList<>();
        for (JsonNode node : componentsNode) {
            try {
                Component component = new Component();
                component.setTenantId(node.get("tenantId").asText());
                component.setName(node.get("name").asText());
                component.setDescription(node.get("description").asText());
                component.setCost(new BigDecimal(node.get("cost").asText()));
                component.setIsAllergenic(node.get("isAllergenic").asBoolean());

                if (node.has("allergenInfo") && !node.get("allergenInfo").isNull()) {
                    component.setAllergenInfo(node.get("allergenInfo").asText());
                }

                components.add(component);
            } catch (Exception e) {
                LOGGER.warn("Error processing component: {}", e.getMessage());
                // Continue with next component
            }
        }

        LOGGER.info("Saving {} components to database", components.size());
        List<Component> savedComponents = componentRepository.saveAll(components);

        for (Component component : savedComponents) {
            componentMap.put(component.getTenantId() + ":" + component.getName(), component);
        }

        LOGGER.info("Loaded {} components", savedComponents.size());
        return componentMap;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private Map<String, Category> loadCategories(JsonNode categoriesNode) {
        Map<String, Category> categoryMap = new HashMap<>();

        if (categoriesNode == null || !categoriesNode.isArray() || categoriesNode.isEmpty()) {
            LOGGER.warn("No categories found in sample data or invalid format");
            return categoryMap;
        }

        LOGGER.info("Found {} categories in sample data", categoriesNode.size());

        List<Category> categories = new ArrayList<>();
        for (JsonNode node : categoriesNode) {
            try {
                Category category = new Category();
                category.setTenantId(node.get("tenantId").asText());
                category.setName(node.get("name").asText());
                category.setDescription(node.get("description").asText());
                category.setDisplayOrder(node.get("displayOrder").asInt());
                category.setActive(node.get("active").asBoolean());

                categories.add(category);
            } catch (Exception e) {
                LOGGER.warn("Error processing category: {}", e.getMessage());
                // Continue with next category
            }
        }

        LOGGER.info("Saving {} categories to database", categories.size());
        List<Category> savedCategories = categoryRepository.saveAll(categories);

        for (Category category : savedCategories) {
            categoryMap.put(category.getTenantId() + ":" + category.getName(), category);
        }

        LOGGER.info("Loaded {} categories", savedCategories.size());
        return categoryMap;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private Map<String, CategoryItem> loadCategoryItems(JsonNode itemsNode, Map<String, Category> categoryMap,
                                                        Map<String, Component> componentMap) {
        Map<String, CategoryItem> itemMap = new HashMap<>();

        if (itemsNode == null || !itemsNode.isArray() || itemsNode.isEmpty()) {
            LOGGER.warn("No category items found in sample data or invalid format");
            return itemMap;
        }

        LOGGER.info("Found {} category items in sample data", itemsNode.size());

        // Create and save the category items
        List<CategoryItem> items = createCategoryItems(itemsNode, categoryMap);
        LOGGER.info("Created {} category items, saving to database", items.size());

        List<CategoryItem> savedItems = categoryItemRepository.saveAll(items);
        LOGGER.info("Saved {} category items to database", savedItems.size());

        // Create a map of the saved items for easy lookup
        for (CategoryItem item : savedItems) {
            itemMap.put(item.getTenantId() + ":" + item.getName(), item);
        }

        // Add components to the items
        LOGGER.info("Adding components to category items");
        addComponentsToCategoryItems(itemsNode, itemMap, componentMap);

        LOGGER.info("Loaded {} category items with components", itemMap.size());
        return itemMap;
    }

    /**
     * Create the list of CategoryItem objects from JSON data.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    private List<CategoryItem> createCategoryItems(JsonNode itemsNode, Map<String, Category> categoryMap) {
        List<CategoryItem> items = new ArrayList<>();

        for (JsonNode node : itemsNode) {
            try {
                String tenantId = node.get("tenantId").asText();
                String categoryName = node.get("categoryName").asText();
                String itemName = node.get("name").asText();

                Category category = categoryMap.get(tenantId + ":" + categoryName);
                if (category == null) {
                    LOGGER.warn("Category not found for item {}: {}", itemName, categoryName);
                    continue;
                }

                CategoryItem item = new CategoryItem();
                item.setTenantId(tenantId);
                item.setCategory(category);
                item.setName(itemName);
                item.setDescription(node.get("description").asText());
                item.setBasePrice(new BigDecimal(node.get("basePrice").asText()));
                item.setImageUrl(node.get("imageUrl").asText());
                item.setSku(node.get("sku").asText());
                item.setDisplayOrder(node.get("displayOrder").asInt());
                item.setActive(node.get("active").asBoolean());

                items.add(item);
            } catch (Exception e) {
                LOGGER.warn("Error processing category item: {}", e.getMessage());
                // Continue with next item
            }
        }

        return items;
    }

    /**
     * Add components to the category items and save them.
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private void addComponentsToCategoryItems(JsonNode itemsNode, Map<String, CategoryItem> itemMap,
                                              Map<String, Component> componentMap) {
        for (JsonNode node : itemsNode) {
            try {
                String tenantId = node.get("tenantId").asText();
                String itemName = node.get("name").asText();

                CategoryItem item = itemMap.get(tenantId + ":" + itemName);
                if (item == null) {
                    LOGGER.warn("Category item not found when adding components: {}", itemName);
                    continue;
                }

                addComponentsToItem(node, item, tenantId, componentMap);
            } catch (Exception e) {
                LOGGER.warn("Error adding components to category item: {}", e.getMessage());
                // Continue with next item
            }
        }

        LOGGER.info("Saving category items with their components");
        // Save the items with their components
        categoryItemRepository.saveAll(itemMap.values());
    }

    /**
     * Helper method to add components to a specific item
     */
    private void addComponentsToItem(JsonNode node, CategoryItem item, String tenantId,
                                     Map<String, Component> componentMap) {
        JsonNode componentsNode = node.get("components");
        if (componentsNode != null && componentsNode.isArray()) {
            for (JsonNode componentName : componentsNode) {
                Component component = componentMap.get(tenantId + ":" + componentName.asText());
                if (component != null) {
                    item.getComponents().add(component);
                } else {
                    LOGGER.warn("Component not found for item {}: {}",
                            item.getName(), componentName.asText());
                }
            }
        }
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private int loadCustomizations(JsonNode customizationsNode, Map<String, CategoryItem> itemMap,
                                   Map<String, Component> componentMap) {
        if (customizationsNode == null || !customizationsNode.isArray() || customizationsNode.isEmpty()) {
            LOGGER.warn("No customizations found in sample data or invalid format");
            return 0;
        }

        LOGGER.info("Found {} customizations in sample data", customizationsNode.size());

        // Create and save the customizations
        List<CategoryItemCustomization> customizations = createCustomizations(customizationsNode, itemMap);
        LOGGER.info("Created {} customizations, saving to database", customizations.size());

        List<CategoryItemCustomization> savedCustomizations = customizationRepository.saveAll(customizations);
        LOGGER.info("Saved {} customizations to database", savedCustomizations.size());

        // Add components to the customizations
        LOGGER.info("Adding components to customizations");
        addComponentsToCustomizations(customizationsNode, savedCustomizations, componentMap);

        return savedCustomizations.size();
    }

    /**
     * Create the list of CategoryItemCustomization objects from JSON data.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    private List<CategoryItemCustomization> createCustomizations(JsonNode customizationsNode,
                                                                 Map<String, CategoryItem> itemMap) {
        List<CategoryItemCustomization> customizations = new ArrayList<>();

        for (JsonNode node : customizationsNode) {
            try {
                String tenantId = node.get("tenantId").asText();
                String categoryItemName = node.get("categoryItemName").asText();

                CategoryItem item = itemMap.get(tenantId + ":" + categoryItemName);
                if (item == null) {
                    LOGGER.warn("Category item not found for customization: {}", categoryItemName);
                    continue;
                }

                CategoryItemCustomization customization = new CategoryItemCustomization();
                customization.setTenantId(tenantId);
                customization.setCategoryItem(item);
                customization.setName(node.get("name").asText());
                customization.setPriceAdjustment(new BigDecimal(node.get("priceAdjustment").asText()));
                customization.setActive(node.get("active").asBoolean());

                customizations.add(customization);
            } catch (Exception e) {
                LOGGER.warn("Error processing customization: {}", e.getMessage());
                // Continue with next customization
            }
        }

        return customizations;
    }

    /**
     * Add components to the customizations and save them.
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private void addComponentsToCustomizations(JsonNode customizationsNode,
                                               List<CategoryItemCustomization> savedCustomizations,
                                               Map<String, Component> componentMap) {
        int index = 0;
        for (JsonNode node : customizationsNode) {
            try {
                if (index >= savedCustomizations.size()) {
                    break;
                }

                CategoryItemCustomization customization = savedCustomizations.get(index++);
                String tenantId = node.get("tenantId").asText();

                addComponentsToCustomization(node, customization, tenantId, componentMap);
            } catch (Exception e) {
                LOGGER.warn("Error adding components to customization: {}", e.getMessage());
                // Continue with next customization
            }
        }

        LOGGER.info("Saving customizations with their components");
        // Save the customizations with their components
        customizationRepository.saveAll(savedCustomizations);
    }

    /**
     * Helper method to add components to a specific customization
     */
    private void addComponentsToCustomization(JsonNode node, CategoryItemCustomization customization,
                                              String tenantId, Map<String, Component> componentMap) {
        JsonNode componentsNode = node.get("components");
        if (componentsNode != null && componentsNode.isArray()) {
            for (JsonNode componentName : componentsNode) {
                Component component = componentMap.get(tenantId + ":" + componentName.asText());
                if (component != null) {
                    customization.getComponents().add(component);
                } else {
                    LOGGER.warn("Component not found for customization {}: {}",
                            customization.getName(), componentName.asText());
                }
            }
        }
    }
}
