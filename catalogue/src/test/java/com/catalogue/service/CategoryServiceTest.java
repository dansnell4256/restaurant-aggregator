package com.catalogue.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalogue.dto.CategoryDTO;
import com.catalogue.exception.DuplicateResourceException;
import com.catalogue.exception.ResourceNotFoundException;
import com.catalogue.model.Category;
import com.catalogue.repository.CategoryRepository;
import com.common.tenant.TenantContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TenantContextHolder tenantContextHolder;

    @InjectMocks
    private CategoryService categoryService;

    private static String tenantId = "tenant1";
    private Category category1;
    private Category category2;

    @BeforeEach
    public void setup() {
        // Set up test data
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Appetizers");
        category1.setDescription("Starter dishes");
        category1.setDisplayOrder(1);
        category1.setActive(true);
        category1.setTenantId(tenantId);

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Main Courses");
        category2.setDescription("Main dishes");
        category2.setDisplayOrder(2);
        category2.setActive(true);
        category2.setTenantId(tenantId);

        // Mock tenant context
        when(tenantContextHolder.getTenantId()).thenReturn(tenantId);
    }

    @Nested
    @DisplayName("getAllCategories Tests")
    class GetAllCategoriesTests {

        @BeforeEach
        void setUp() {
            when(categoryRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId))
                    .thenReturn(Arrays.asList(category1, category2));
        }

        @Test
        @DisplayName("Should return the correct number of categories")
        void testGetAllCategoriesCount() {
            // When
            List<CategoryDTO> categories = categoryService.getAllCategories();

            // Then
            assertEquals(2, categories.size(),
                    "Should return exactly 2 categories");
        }

        @Test
        @DisplayName("Should return the first category with correct name")
        void testGetAllCategoriesFirstCategoryName() {
            // When
            List<CategoryDTO> categories = categoryService.getAllCategories();

            // Then
            assertEquals("Appetizers", categories.get(0).getName(),
                    "First category should be Appetizers");
        }

        @Test
        @DisplayName("Should return the second category with correct name")
        void testGetAllCategoriesSecondCategoryName() {
            // When
            List<CategoryDTO> categories = categoryService.getAllCategories();

            // Then
            assertEquals("Main Courses", categories.get(1).getName(),
                    "Second category should be Main Courses");
        }

        @Test
        @DisplayName("Should call tenant context holder")
        void testGetAllCategoriesCallsTenantContextHolder() {
            // When
            categoryService.getAllCategories();

            // Then
            verify(tenantContextHolder, times(1)).getTenantId();
        }

        @Test
        @DisplayName("Should call repository with correct tenant ID")
        void testGetAllCategoriesCallsRepositoryWithCorrectTenantId() {
            // When
            categoryService.getAllCategories();

            // Then
            verify(categoryRepository, times(1)).findByTenantIdOrderByDisplayOrderAsc(tenantId);
        }
    }

    @Nested
    @DisplayName("getCategory Tests")
    class GetCategoryTests {

        @Test
        @DisplayName("Should return category DTO when found")
        void testGetCategoryReturnsCategoryDTO() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertNotNull(categoryDTO,
                    "Should return a non-null category DTO");
        }

        @Test
        @DisplayName("Should return category with correct ID")
        void testGetCategoryReturnsCorrectId() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertEquals(1L, categoryDTO.getId(),
                    "Category ID should be 1");
        }

        @Test
        @DisplayName("Should return category with correct name")
        void testGetCategoryReturnsCorrectName() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertEquals("Appetizers", categoryDTO.getName(),
                    "Category name should be 'Appetizers'");
        }

        @Test
        @DisplayName("Should return category with correct description")
        void testGetCategoryReturnsCorrectDescription() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertEquals("Starter dishes", categoryDTO.getDescription(),
                    "Category description should be 'Starter dishes'");
        }

        @Test
        @DisplayName("Should return category with correct display order")
        void testGetCategoryReturnsCorrectDisplayOrder() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertEquals(1, categoryDTO.getDisplayOrder(),
                    "Category display order should be 1");
        }

        @Test
        @DisplayName("Should return category with correct active status")
        void testGetCategoryReturnsCorrectActiveStatus() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            CategoryDTO categoryDTO = categoryService.getCategory(1L);

            // Then
            assertEquals(true, categoryDTO.getActive(),
                    "Category active flag should be true");
        }

        @Test
        @DisplayName("Should call tenant context holder")
        void testGetCategoryCallsTenantContextHolder() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            categoryService.getCategory(1L);

            // Then
            verify(tenantContextHolder, times(1)).getTenantId();
        }

        @Test
        @DisplayName("Should call repository with correct ID and tenant ID")
        void testGetCategoryCallsRepositoryWithCorrectParams() {
            // Given
            when(categoryRepository.findByIdAndTenantId(1L, tenantId))
                    .thenReturn(Optional.of(category1));

            // When
            categoryService.getCategory(1L);

            // Then
            verify(categoryRepository, times(1)).findByIdAndTenantId(1L, tenantId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void testGetCategoryThrowsExceptionWhenNotFound() {
            // Given
            when(categoryRepository.findByIdAndTenantId(anyLong(), anyString()))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> {
                categoryService.getCategory(999L);
            }, "Should throw ResourceNotFoundException when category not found");
        }
    }

    @Nested
    @DisplayName("createCategory Tests")
    class CreateCategoryTests {

        private CategoryDTO inputDTO;
        private Category newCategory;

        @BeforeEach
        void setUp() {
            inputDTO = new CategoryDTO();
            inputDTO.setName("Desserts");
            inputDTO.setDescription("Sweet treats");
            inputDTO.setDisplayOrder(3);
            inputDTO.setActive(true);

            newCategory = new Category();
            newCategory.setId(3L);
            newCategory.setName("Desserts");
            newCategory.setDescription("Sweet treats");
            newCategory.setDisplayOrder(3);
            newCategory.setActive(true);
            newCategory.setTenantId(tenantId);
        }

        @Test
        @DisplayName("Should return a non-null DTO when created successfully")
        void testCreateCategoryReturnsNonNullDTO() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertNotNull(createdDTO,
                    "Should return a non-null category DTO");
        }

        @Test
        @DisplayName("Should return DTO with correct ID")
        void testCreateCategoryReturnsCorrectId() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals(3L, createdDTO.getId(),
                    "Created category ID should be 3");
        }

        @Test
        @DisplayName("Should return DTO with correct name")
        void testCreateCategoryReturnsCorrectName() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals("Desserts", createdDTO.getName(),
                    "Created category name should be 'Desserts'");
        }

        @Test
        @DisplayName("Should return DTO with correct description")
        void testCreateCategoryReturnsCorrectDescription() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals("Sweet treats", createdDTO.getDescription(),
                    "Created category description should be 'Sweet treats'");
        }

        @Test
        @DisplayName("Should return DTO with correct display order")
        void testCreateCategoryReturnsCorrectDisplayOrder() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals(3, createdDTO.getDisplayOrder(),
                    "Created category display order should be 3");
        }

        @Test
        @DisplayName("Should return DTO with correct active status")
        void testCreateCategoryReturnsCorrectActiveStatus() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals(true, createdDTO.getActive(),
                    "Created category active flag should be true");
        }

        @Test
        @DisplayName("Should call tenant context holder")
        void testCreateCategoryCallsTenantContextHolder() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            categoryService.createCategory(inputDTO);

            // Then
            verify(tenantContextHolder, times(1)).getTenantId();
        }

        @Test
        @DisplayName("Should check if name already exists")
        void testCreateCategoryChecksIfNameExists() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            categoryService.createCategory(inputDTO);

            // Then
            verify(categoryRepository, times(1)).existsByNameAndTenantId("Desserts", tenantId);
        }

        @Test
        @DisplayName("Should save category with correct data")
        void testCreateCategorySavesCategory() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            categoryService.createCategory(inputDTO);

            // Then
            verify(categoryRepository, times(1)).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw exception when category name already exists")
        void testCreateCategoryThrowsExceptionWhenNameExists() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(true);

            // When & Then
            assertThrows(DuplicateResourceException.class, () -> {
                categoryService.createCategory(inputDTO);
            }, "Should throw DuplicateResourceException when category name already exists");
        }

/*
        @Test
        @DisplayName("Should not call save when category name already exists")
        void testCreateCategoryDoesNotCallSaveWhenNameExists() {
            // Given
            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(true);

            // When
            try {
                categoryService.createCategory(inputDTO);
            } catch (DuplicateResourceException e) {
                // Expected exception - we're testing that save is not called
            }

            // Then
            verify(categoryRepository, never()).save(any(Category.class));
        }
*/

        @Test
        @DisplayName("Should set active flag to true by default if not provided")
        void testCreateCategorySetsDefaultActiveFlag() {
            // Given
            inputDTO.setActive(null); // No active flag set

            when(categoryRepository.existsByNameAndTenantId("Desserts", tenantId))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(newCategory);

            // When
            CategoryDTO createdDTO = categoryService.createCategory(inputDTO);

            // Then
            assertEquals(true, createdDTO.getActive(),
                    "Created category active flag should default to true");
        }
    }
}
