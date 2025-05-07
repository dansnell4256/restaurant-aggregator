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
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
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
@org.springframework.stereotype.Component
@Profile({"dev", "test"})
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

    /**
     * Loads sample data on application startup.
     * This method is called by Spring Boot when the application starts.
     *
     * @param args Command line arguments
     * @throws Exception If there's an error loading sample data
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LOGGER.info("Loading sample data on startup...");
        loadSampleData();
    }

    /**
     * Loads sample data into the database.
     * This method can be called manually to reload sample data without restarting the application.
     *
     * @throws Exception If there's an error loading sample data
     */
    @Transactional
    public void loadSampleData() throws Exception {
        LOGGER.info("Loading sample data...");

        // Clear existing data
        clearExistingData();

        // Load sample data from JSON file
        try (InputStream inputStream = new ClassPathResource(SAMPLE_DATA_FILE).getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Load components first
            Map<String, Component> componentMap = loadComponents(rootNode.get("components"));

            // Load categories
            Map<String, Category> categoryMap = loadCategories(rootNode.get("categories"));

            // Load category items
            Map<String, CategoryItem> categoryItemMap = loadCategoryItems(
                    rootNode.get("categoryItems"), categoryMap, componentMap);

            // Load customizations
            loadCustomizations(rootNode.get("categoryItemCustomizations"), categoryItemMap, componentMap);

            LOGGER.info("Sample data loaded successfully");
        } catch (Exception e) {
            LOGGER.error("Error loading sample data", e);
            throw e;
        }
    }

    private void clearExistingData() {
        LOGGER.info("Clearing existing data...");
        customizationRepository.deleteAll();
        categoryItemRepository.deleteAll();
        categoryRepository.deleteAll();
        componentRepository.deleteAll();
    }

    private Map<String, Component> loadComponents(JsonNode componentsNode) {
        LOGGER.info("Loading components...");
        Map<String, Component> componentMap = new HashMap<>();

        List<Component> components = new ArrayList<>();
        for (JsonNode node : componentsNode) {
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
        }

        List<Component> savedComponents = componentRepository.saveAll(components);

        for (Component component : savedComponents) {
            componentMap.put(component.getTenantId() + ":" + component.getName(), component);
        }

        LOGGER.info("Loaded {} components", savedComponents.size());
        return componentMap;
    }

    private Map<String, Category> loadCategories(JsonNode categoriesNode) {
        LOGGER.info("Loading categories...");
        Map<String, Category> categoryMap = new HashMap<>();

        List<Category> categories = new ArrayList<>();
        for (JsonNode node : categoriesNode) {
            Category category = new Category();
            category.setTenantId(node.get("tenantId").asText());
            category.setName(node.get("name").asText());
            category.setDescription(node.get("description").asText());
            category.setDisplayOrder(node.get("displayOrder").asInt());
            category.setActive(node.get("active").asBoolean());

            categories.add(category);
        }

        List<Category> savedCategories = categoryRepository.saveAll(categories);

        for (Category category : savedCategories) {
            categoryMap.put(category.getTenantId() + ":" + category.getName(), category);
        }

        LOGGER.info("Loaded {} categories", savedCategories.size());
        return categoryMap;
    }

    private Map<String, CategoryItem> loadCategoryItems(JsonNode itemsNode, Map<String, Category> categoryMap,
                                                        Map<String, Component> componentMap) {
        LOGGER.info("Loading category items...");

        // Create and save the category items
        List<CategoryItem> items = createCategoryItems(itemsNode, categoryMap);
        List<CategoryItem> savedItems = categoryItemRepository.saveAll(items);

        // Create a map of the saved items for easy lookup
        Map<String, CategoryItem> itemMap = new HashMap<>();
        for (CategoryItem item : savedItems) {
            itemMap.put(item.getTenantId() + ":" + item.getName(), item);
        }

        // Add components to the items
        addComponentsToCategoryItems(itemsNode, itemMap, componentMap);

        LOGGER.info("Loaded {} category items", savedItems.size());
        return itemMap;
    }

    /**
     * Create the list of CategoryItem objects from JSON data.
     */
    private List<CategoryItem> createCategoryItems(JsonNode itemsNode, Map<String, Category> categoryMap) {
        List<CategoryItem> items = new ArrayList<>();

        for (JsonNode node : itemsNode) {
            String tenantId = node.get("tenantId").asText();
            String categoryName = node.get("categoryName").asText();
            String itemName = node.get("name").asText();

            Category category = categoryMap.get(tenantId + ":" + categoryName);
            if (category == null) {
                LOGGER.warn("Category not found: {}", categoryName);
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
        }

        return items;
    }

    /**
     * Add components to the category items and save them.
     */
    private void addComponentsToCategoryItems(JsonNode itemsNode, Map<String, CategoryItem> itemMap,
                                              Map<String, Component> componentMap) {
        for (JsonNode node : itemsNode) {
            String tenantId = node.get("tenantId").asText();
            String itemName = node.get("name").asText();

            CategoryItem item = itemMap.get(tenantId + ":" + itemName);
            if (item == null) {
                continue;
            }

            JsonNode componentsNode = node.get("components");
            if (componentsNode != null && componentsNode.isArray()) {
                for (JsonNode componentName : componentsNode) {
                    Component component = componentMap.get(tenantId + ":" + componentName.asText());
                    if (component != null) {
                        item.getComponents().add(component);
                    }
                }
            }
        }

        // Save the items with their components
        categoryItemRepository.saveAll(itemMap.values());
    }

    private void loadCustomizations(JsonNode customizationsNode, Map<String, CategoryItem> itemMap,
                                    Map<String, Component> componentMap) {
        LOGGER.info("Loading customizations...");

        // Create and save the customizations
        List<CategoryItemCustomization> customizations = createCustomizations(customizationsNode, itemMap);
        List<CategoryItemCustomization> savedCustomizations = customizationRepository.saveAll(customizations);

        // Add components to the customizations
        addComponentsToCustomizations(customizationsNode, savedCustomizations, componentMap);

        LOGGER.info("Loaded {} customizations", savedCustomizations.size());
    }

    /**
     * Create the list of CategoryItemCustomization objects from JSON data.
     */
    private List<CategoryItemCustomization> createCustomizations(JsonNode customizationsNode,
                                                                 Map<String, CategoryItem> itemMap) {
        List<CategoryItemCustomization> customizations = new ArrayList<>();

        for (JsonNode node : customizationsNode) {
            String tenantId = node.get("tenantId").asText();
            String categoryItemName = node.get("categoryItemName").asText();

            CategoryItem item = itemMap.get(tenantId + ":" + categoryItemName);
            if (item == null) {
                LOGGER.warn("Category item not found: {}", categoryItemName);
                continue;
            }

            CategoryItemCustomization customization = new CategoryItemCustomization();
            customization.setTenantId(tenantId);
            customization.setCategoryItem(item);
            customization.setName(node.get("name").asText());
            customization.setPriceAdjustment(new BigDecimal(node.get("priceAdjustment").asText()));
            customization.setActive(node.get("active").asBoolean());

            customizations.add(customization);
        }

        return customizations;
    }

    /**
     * Add components to the customizations and save them.
     */
    private void addComponentsToCustomizations(JsonNode customizationsNode,
                                               List<CategoryItemCustomization> savedCustomizations,
                                               Map<String, Component> componentMap) {
        int index = 0;
        for (JsonNode node : customizationsNode) {
            if (index >= savedCustomizations.size()) {
                break;
            }

            CategoryItemCustomization customization = savedCustomizations.get(index++);
            String tenantId = node.get("tenantId").asText();

            JsonNode componentsNode = node.get("components");
            if (componentsNode != null && componentsNode.isArray()) {
                for (JsonNode componentName : componentsNode) {
                    Component component = componentMap.get(tenantId + ":" + componentName.asText());
                    if (component != null) {
                        customization.getComponents().add(component);
                    }
                }
            }
        }

        // Save the customizations with their components
        customizationRepository.saveAll(savedCustomizations);
    }
}
