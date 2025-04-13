package com.catalogue.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Component Entity Tests")
public class ComponentTest {

    private Component component;

    @BeforeEach
    void setUp() {
        component = new Component();
    }

    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertyTests {

        @Test
        @DisplayName("Should set and get name properly")
        void testNameProperty() {
            String expected = "Chicken Wing";

            component.setName(expected);

            assertEquals(expected, component.getName(),
                    "Component name should match the value that was set");
        }

        @Test
        @DisplayName("Should have null name by default")
        void testDefaultNameIsNull() {
            assertNull(component.getName(), "Default component name should be null");
        }

        @Test
        @DisplayName("Should set and get description properly")
        void testDescriptionProperty() {
            String expected = "Chicken wing piece";

            component.setDescription(expected);

            assertEquals(expected, component.getDescription(),
                    "Component description should match the value that was set");
        }

        @Test
        @DisplayName("Should have null description by default")
        void testDefaultDescriptionIsNull() {
            assertNull(component.getDescription(), "Default component description should be null");
        }

        @Test
        @DisplayName("Should set and get cost properly")
        void testCostProperty() {
            BigDecimal expected = new BigDecimal("0.50");

            component.setCost(expected);

            assertEquals(expected, component.getCost(),
                    "Component cost should match the value that was set");
        }

        @Test
        @DisplayName("Should have null cost by default")
        void testDefaultCostIsNull() {
            assertNull(component.getCost(), "Default component cost should be null");
        }

        @Test
        @DisplayName("Should set and get isAllergenic properly")
        void testIsAllergenicProperty() {
            Boolean expected = false;

            component.setIsAllergenic(expected);

            assertEquals(expected, component.getIsAllergenic(),
                    "Component isAllergenic should match the value that was set");
        }

        @Test
        @DisplayName("Should have null isAllergenic by default")
        void testDefaultIsAllergenicIsNull() {
            assertNull(component.getIsAllergenic(), "Default component isAllergenic should be null");
        }

        @Test
        @DisplayName("Should set and get tenant ID properly")
        void testTenantIdProperty() {
            String expected = "tenant1";

            component.setTenantId(expected);

            assertEquals(expected, component.getTenantId(),
                    "Component tenant ID should match the value that was set");
        }

        @Test
        @DisplayName("Should have null tenant ID by default")
        void testDefaultTenantIdIsNull() {
            assertNull(component.getTenantId(), "Default component tenant ID should be null");
        }
    }

    @Nested
    @DisplayName("Allergen Information Tests")
    class AllergenTests {

        @Test
        @DisplayName("Should set and get allergen information properly")
        void testAllergenInfoProperty() {
            String expected = "Contains peanuts, may contain traces of tree nuts";

            component.setAllergenInfo(expected);

            assertEquals(expected, component.getAllergenInfo(),
                    "Component allergen info should match the value that was set");
        }

        @Test
        @DisplayName("Should have null allergen info by default")
        void testDefaultAllergenInfoIsNull() {
            assertNull(component.getAllergenInfo(), "Default component allergen info should be null");
        }

        @Test
        @DisplayName("Should correctly identify allergenic components")
        void testAllergenicComponent() {
            // Setup an allergenic component
            component.setName("Peanut Sauce");
            component.setDescription("Thai-style peanut sauce");
            component.setCost(new BigDecimal("1.20"));
            component.setIsAllergenic(true);
            component.setAllergenInfo("Contains peanuts, may contain traces of tree nuts");

            assertTrue(component.getIsAllergenic(), "Component should be identified as allergenic");
//            assertEquals("Contains peanuts, may contain traces of tree nuts", component.getAllergenInfo(),
//                    "Component should have correct allergen information");
        }
    }
}
