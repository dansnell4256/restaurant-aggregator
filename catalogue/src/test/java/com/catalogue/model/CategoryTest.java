package com.catalogue.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Category Entity Tests")
public class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Nested
    @DisplayName("Property Tests")
    class PropertyTests {

        @Test
        @DisplayName("Should set and get name properly")
        void testNameProperty() {
            // Given
            String expected = "Appetizers";

            // When
            category.setName(expected);
            String actual = category.getName();

            // Then
            assertEquals(expected, actual, "Category name should match the value that was set");
        }

        @Test
        @DisplayName("Should have null name by default")
        void testDefaultNameIsNull() {
            // Then
            assertNull(category.getName(), "Default category name should be null");
        }

        @Test
        @DisplayName("Should set and get description properly")
        void testDescriptionProperty() {
            // Given
            String expected = "Starter dishes";

            // When
            category.setDescription(expected);
            String actual = category.getDescription();

            // Then
            assertEquals(expected, actual, "Category description should match the value that was set");
        }

        @Test
        @DisplayName("Should have null description by default")
        void testDefaultDescriptionIsNull() {
            // Then
            assertNull(category.getDescription(), "Default category description should be null");
        }

        @Test
        @DisplayName("Should set and get display order properly")
        void testDisplayOrderProperty() {
            // Given
            Integer expected = 1;

            // When
            category.setDisplayOrder(expected);
            Integer actual = category.getDisplayOrder();

            // Then
            assertEquals(expected, actual, "Category display order should match the value that was set");
        }

        @Test
        @DisplayName("Should have null display order by default")
        void testDefaultDisplayOrderIsNull() {
            // Then
            assertNull(category.getDisplayOrder(), "Default category display order should be null");
        }

        @Test
        @DisplayName("Should set and get active flag properly")
        void testActiveProperty() {
            // Given
            Boolean expected = true;

            // When
            category.setActive(expected);
            Boolean actual = category.getActive();

            // Then
            assertEquals(expected, actual, "Category active flag should match the value that was set");
        }

        @Test
        @DisplayName("Should have null active flag by default")
        void testDefaultActiveIsNull() {
            // Then
            assertNull(category.getActive(), "Default category active flag should be null");
        }

        @Test
        @DisplayName("Should set and get tenant ID properly")
        void testTenantIdProperty() {
            // Given
            String expected = "tenant1";

            // When
            category.setTenantId(expected);
            String actual = category.getTenantId();

            // Then
            assertEquals(expected, actual, "Category tenant ID should match the value that was set");
        }

        @Test
        @DisplayName("Should have null tenant ID by default")
        void testDefaultTenantIdIsNull() {
            // Then
            assertNull(category.getTenantId(), "Default category tenant ID should be null");
        }
    }
}
