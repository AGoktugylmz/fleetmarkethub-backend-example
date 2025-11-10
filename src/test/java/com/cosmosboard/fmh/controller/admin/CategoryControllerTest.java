package com.cosmosboard.fmh.controller.admin;

import com.cosmosboard.fmh.dto.request.category.CreateCategoryRequest;
import com.cosmosboard.fmh.dto.request.category.UpdateCategoryRequest;
import com.cosmosboard.fmh.dto.response.SuccessResponse;
import com.cosmosboard.fmh.dto.response.category.CategoriesPaginationResponse;
import com.cosmosboard.fmh.dto.response.category.CategoryResponse;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.specification.criteria.CategoryCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.service.CategoryService;
import com.cosmosboard.fmh.service.MessageSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Admin - CategoryController")
public class CategoryControllerTest {
    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("Test class for category list scenarios")
    public class ListTest {
        @Test
        void givenIsActiveAndQAndPageAndSizeAndSortByAndSort_whenListWithInvalidSortBy_thenBadRequestExceptionThrown() {
            // Given
            when(messageSourceService.get("invalid_sort_column")).thenReturn("TEST Invalid Sort Column");
            // When
            Executable response = () -> categoryController.list(null, null, null, null, "invalidSortBy", null);

            // Then
            BadRequestException exception = assertThrows(BadRequestException.class, response);
            assertEquals("TEST Invalid Sort Column", exception.getMessage());
        }

        @Test
        void givenIsActiveAndQAndPageAndSizeAndSortByAndSort_whenList_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            Page<Category> page = new PageImpl<>(List.of(category));
            when(categoryService.findAll(any(CategoryCriteria.class), any(PaginationCriteria.class)))
                    .thenReturn(page);
            // When
            CategoriesPaginationResponse categoriesResponse = categoryController.list(null, null, null, null, null, null);
            // Then
            assertNotNull(categoriesResponse);
            assertEquals(1, categoriesResponse.getPage());
            assertEquals(1, categoriesResponse.getPages());
            assertEquals(1, categoriesResponse.getTotal());
            assertEquals(1, categoriesResponse.getItems().size());
            assertEquals(category.getId(), categoriesResponse.getItems().get(0).getId());
            assertEquals(category.getName(), categoriesResponse.getItems().get(0).getName());
            assertEquals(category.getDescription(), categoriesResponse.getItems().get(0).getDescription());
            assertEquals(category.getIsActive(), categoriesResponse.getItems().get(0).getIsActive());
        }
    }

    @Nested
    @DisplayName("Test class for category sort scenarios")
    public class SortTest {
        @Test
        void givenIdAndType_whenSort_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            doNothing().when(categoryService).sort(Mockito.any(String.class), Mockito.any(String.class));
            // When
            SuccessResponse response = categoryController.sort(category.getId(), "up");
            // Then
            assertNotNull(response);
            assertEquals(messageSourceService.get("category_sort_updated"), response.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for category create scenarios")
    public class CreateTest {
        @Test
        void givenCreateCategoryRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCategoryRequest request = Factory.createCreateCategoryRequest();
            Category category = Factory.createCategory();

            when(categoryService.create(request)).thenReturn(category);

            // When
            CategoryResponse response = categoryController.create(request);

            // Then
            assertNotNull(response);
            assertEquals(category.getId(), response.getId());
            assertEquals(category.getName(), response.getName());
        }

        @Test
        void givenInvalidRequest_whenCreate_thenThrowBadRequestException() {
            // Given
            CreateCategoryRequest invalidRequest = new CreateCategoryRequest(); // Boş veya hatalı bir istek

            when(categoryService.create(invalidRequest)).thenThrow(new BadRequestException("Invalid category data"));

            // When / Then
            assertThrows(BadRequestException.class, () -> categoryController.create(invalidRequest));
        }
    }

    @Nested
    @DisplayName("Test class for category show scenarios")
    public class ShowTest {
        @Test
        void givenId_whenShow_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryService.findOneById(category.getId())).thenReturn(category);
            // When
            CategoryResponse response = categoryController.show(category.getId());
            // Then
            assertNotNull(response);
            assertEquals(category.getId(), response.getId());
            assertEquals(category.getName(), response.getName());
            assertEquals(category.getDescription(), response.getDescription());
            assertEquals(category.getIsActive(), response.getIsActive());
        }
    }

    @Nested
    @DisplayName("Test class for category update scenarios")
    public class UpdateTest {
        @Test
        void givenIdAndUpdateCategoryRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateCategoryRequest request = Factory.createUpdateCategoryRequest();
            Category category = Factory.createCategory();
            when(categoryService.update(category.getId(), request)).thenReturn(category);

            // When
            CategoryResponse response = categoryController.update(category.getId(), request);

            // Then
            assertNotNull(response);
            assertEquals(category.getId(), response.getId());
            assertEquals(category.getName(), response.getName());
            assertEquals(category.getDescription(), response.getDescription());
            assertEquals(category.getIsActive(), response.getIsActive());
        }
    }

    @Nested
    @DisplayName("Test class for category delete scenarios")
    public class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            // When
            ResponseEntity<Void> response = categoryController.delete(category.getId());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }
}
