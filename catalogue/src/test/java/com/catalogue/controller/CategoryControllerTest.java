package com.catalogue.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.catalogue.dto.CategoryDTO;
import com.catalogue.exception.DuplicateResourceException;
import com.catalogue.exception.GlobalExceptionHandler;
//import com.catalogue.exception.ValidationException;
import com.catalogue.service.CategoryService;
import com.common.tenant.TenantContextHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Controller Tests")
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TenantContextHolder tenantContextHolder;

    @InjectMocks
    private CategoryController categoryController;

    private ObjectMapper objectMapper;
    private String tenantId;
    private CategoryDTO categoryDTO1;
    private CategoryDTO categoryDTO2;

    @BeforeEach
    void setUp() {
        // Set up MockMvc with our controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        tenantId = "tenant1";

        // Create test data
        categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1L);
        categoryDTO1.setName("Appetizers");
        categoryDTO1.setDescription("Starter dishes");
        categoryDTO1.setDisplayOrder(1);
        categoryDTO1.setActive(true);

        categoryDTO2 = new CategoryDTO();
        categoryDTO2.setId(2L);
        categoryDTO2.setName("Main Courses");
        categoryDTO2.setDescription("Main dishes");
        categoryDTO2.setDisplayOrder(2);
        categoryDTO2.setActive(true);

        // Set up tenant context holder behavior
        doNothing().when(tenantContextHolder).setTenantId(tenantId);
        doNothing().when(tenantContextHolder).clear();
    }

    @Nested
    @DisplayName("GET /categories")
    class GetAllCategoriesTests {

        @BeforeEach
        void setUp() {
            List<CategoryDTO> categories = Arrays.asList(categoryDTO1, categoryDTO2);
            when(categoryService.getAllCategories()).thenReturn(categories);
        }

        @Test
        @DisplayName("Should return 200 OK status")
        void testGetAllCategoriesReturnsOkStatus() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Assert
            assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(),
                    "Response status should be 200 OK for successful GET request");
        }

        @Test
        @DisplayName("Should return success status in response")
        void testGetAllCategoriesReturnsSuccessStatus() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals(200, responseJson.get("status").asInt(),
                    "Response JSON status field should be 200 for successful request");
        }

        @Test
        @DisplayName("Should return success message in response")
        void testGetAllCategoriesReturnsSuccessMessage() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Categories retrieved successfully", responseJson.get("message").asText(),
                    "Response message should indicate categories were successfully retrieved");
        }

        @Test
        @DisplayName("Should return correct number of categories")
        void testGetAllCategoriesReturnsCorrectCount() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals(2, responseJson.get("data").size(),
                    "Response should contain exactly 2 categories in the data array");
        }

        @Test
        @DisplayName("Should return first category with correct name")
        void testGetAllCategoriesReturnsFirstCategoryName() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Appetizers", responseJson.get("data").get(0).get("name").asText(),
                    "First category in response should be named 'Appetizers'");
        }

        @Test
        @DisplayName("Should return second category with correct name")
        void testGetAllCategoriesReturnsSecondCategoryName() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Main Courses", responseJson.get("data").get(1).get("name").asText(),
                    "Second category in response should be named 'Main Courses'");
        }

        @Test
        @DisplayName("Should set tenant context")
        void testGetAllCategoriesSetsTenantContext() throws Exception {
            // Act
            mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Assert
            verify(tenantContextHolder, times(1)).setTenantId(tenantId);
        }

        @Test
        @DisplayName("Should clear tenant context")
        void testGetAllCategoriesClearsTenantContext() throws Exception {
            // Act
            mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Assert
            verify(tenantContextHolder, times(1)).clear();
        }
    }

    @Nested
    @DisplayName("POST /categories - Success")
    class CreateCategorySuccessTests {

        private CategoryDTO inputDTO;

        @BeforeEach
        void setUp() {
            inputDTO = new CategoryDTO();
            inputDTO.setName("Desserts");
            inputDTO.setDescription("Sweet treats");
            inputDTO.setDisplayOrder(3);
            inputDTO.setActive(true);

            CategoryDTO createdDTO = new CategoryDTO();
            createdDTO.setId(3L);
            createdDTO.setName("Desserts");
            createdDTO.setDescription("Sweet treats");
            createdDTO.setDisplayOrder(3);
            createdDTO.setActive(true);

            when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(createdDTO);
        }

        @Test
        @DisplayName("Should return 201 Created status")
        void testCreateCategoryReturnsCreatedStatus() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Assert
            assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus(),
                    "Response status should be 201 Created for successful category creation");
        }

        @Test
        @DisplayName("Should return success status in response")
        void testCreateCategoryReturnsSuccessStatus() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals(200, responseJson.get("status").asInt(),
                    "Response JSON status field should be 200 for successful category creation");
        }

        @Test
        @DisplayName("Should return success message in response")
        void testCreateCategoryReturnsSuccessMessage() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Category created successfully", responseJson.get("message").asText(),
                    "Response message should indicate category was successfully created");
        }

        @Test
        @DisplayName("Should return created category with correct ID")
        void testCreateCategoryReturnsCorrectId() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals(3, responseJson.get("data").get("id").asInt(),
                    "Created category should have ID 3");
        }

        @Test
        @DisplayName("Should return created category with correct name")
        void testCreateCategoryReturnsCorrectName() throws Exception {
            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Desserts", responseJson.get("data").get("name").asText(),
                    "Created category should have name 'Desserts'");
        }

        @Test
        @DisplayName("Should set tenant context")
        void testCreateCategorySetsTenantContext() throws Exception {
            // Act
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Assert
            verify(tenantContextHolder, times(1)).setTenantId(tenantId);
        }

        @Test
        @DisplayName("Should clear tenant context")
        void testCreateCategoryClearsTenantContext() throws Exception {
            // Act
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(inputDTO)))
                    .andReturn();

            // Assert
            verify(tenantContextHolder, times(1)).clear();
        }
    }
    /*
        @Nested
        @DisplayName("POST /categories - Validation Failure")
        class CreateCategoryValidationFailureTests {

            private CategoryDTO invalidDTO;

            @BeforeEach
            void setUp() {
                invalidDTO = new CategoryDTO();
                // Missing required name field
            }

            @Test
            @DisplayName("Should return 400 Bad Request status")
            void testCreateCategoryValidationFailureReturnsBadRequestStatus() throws Exception {
                // Setup - the stub needs to be in the test rather than in setUp
                when(categoryService.createCategory(any(CategoryDTO.class)))
                        .thenThrow(new ValidationException("Category validation failed",
                                Arrays.asList("Category name is required")));

                // Act
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidDTO)))
                        .andReturn();

                // Assert
                assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus(),
                        "Response status should be 400 Bad Request for validation failure");
            }

            @Test
            @DisplayName("Should return error status in response")
            void testCreateCategoryValidationFailureReturnsErrorStatus() throws Exception {
                // Setup - the stub needs to be in the test
                when(categoryService.createCategory(any(CategoryDTO.class)))
                        .thenThrow(new ValidationException("Category validation failed",
                                Arrays.asList("Category name is required")));

                // Act
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidDTO)))
                        .andReturn();

                // Parse response
                String responseBody = result.getResponse().getContentAsString();
                JsonNode responseJson = objectMapper.readTree(responseBody);

                // Assert
                assertEquals(400, responseJson.get("status").asInt(),
                        "Response JSON status field should be 400 for validation failure");
            }

            @Test
            @DisplayName("Should return error message in response")
            void testCreateCategoryValidationFailureReturnsErrorMessage() throws Exception {
                // Setup - the stub needs to be in the test
                when(categoryService.createCategory(any(CategoryDTO.class)))
                        .thenThrow(new ValidationException("Category validation failed",
                                Arrays.asList("Category name is required")));

                // Act
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidDTO)))
                        .andReturn();

                // Parse response
                String responseBody = result.getResponse().getContentAsString();
                JsonNode responseJson = objectMapper.readTree(responseBody);

                // Assert
                assertEquals("Category validation failed", responseJson.get("message").asText(),
                        "Response message should indicate validation failure");
            }

            @Test
            @DisplayName("Should return validation errors in response")
            void testCreateCategoryValidationFailureReturnsErrors() throws Exception {
                // Setup - the stub needs to be in the test
                when(categoryService.createCategory(any(CategoryDTO.class)))
                        .thenThrow(new ValidationException("Category validation failed",
                                Arrays.asList("Category name is required")));

                // Act
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidDTO)))
                        .andReturn();

                // Parse response
                String responseBody = result.getResponse().getContentAsString();
                JsonNode responseJson = objectMapper.readTree(responseBody);

                // Assert
                assertEquals("Category name is required",
                        responseJson.get("data").get("errors").get(0).asText(),
                        "Response should include the specific validation error message");
            }
        }
    */
    @Nested
    @DisplayName("POST /categories - Duplicate Name")
    class CreateCategoryDuplicateNameTests {

        private CategoryDTO duplicateDTO;

        @BeforeEach
        void setUp() {
            duplicateDTO = new CategoryDTO();
            duplicateDTO.setName("Appetizers"); // This name already exists
            duplicateDTO.setDescription("Duplicate name test");
            duplicateDTO.setDisplayOrder(3);
            duplicateDTO.setActive(true);
        }

        @Test
        @DisplayName("Should return 409 Conflict status")
        void testCreateCategoryDuplicateNameReturnsConflictStatus() throws Exception {
            // Setup - move the stub to the test method
            when(categoryService.createCategory(any(CategoryDTO.class)))
                    .thenThrow(new DuplicateResourceException("Category with name 'Appetizers' already exists"));

            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(duplicateDTO)))
                    .andReturn();

            // Assert
            assertEquals(HttpStatus.CONFLICT.value(), result.getResponse().getStatus(),
                    "Response status should be 409 Conflict for duplicate category name");
        }

        @Test
        @DisplayName("Should return error status in response")
        void testCreateCategoryDuplicateNameReturnsErrorStatus() throws Exception {
            // Setup - move the stub to the test method
            when(categoryService.createCategory(any(CategoryDTO.class)))
                    .thenThrow(new DuplicateResourceException("Category with name 'Appetizers' already exists"));

            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(duplicateDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals(409, responseJson.get("status").asInt(),
                    "Response JSON status field should be 409 for duplicate category name");
        }

        @Test
        @DisplayName("Should return error message in response")
        void testCreateCategoryDuplicateNameReturnsErrorMessage() throws Exception {
            // Setup - move the stub to the test method
            when(categoryService.createCategory(any(CategoryDTO.class)))
                    .thenThrow(new DuplicateResourceException("Category with name 'Appetizers' already exists"));

            // Act
            MvcResult result = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/tenants/{tenantId}/catalogue/categories", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(duplicateDTO)))
                    .andReturn();

            // Parse response
            String responseBody = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Assert
            assertEquals("Category with name 'Appetizers' already exists",
                    responseJson.get("message").asText(),
                    "Response message should indicate the specific category name that already exists");
        }
    }
}
