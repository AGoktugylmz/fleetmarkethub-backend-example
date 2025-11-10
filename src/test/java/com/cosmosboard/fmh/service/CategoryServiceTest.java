package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.category.CreateCategoryRequest;
import com.cosmosboard.fmh.dto.request.category.UpdateCategoryRequest;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.specification.CategoryFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.factory.Factory;
import com.cosmosboard.fmh.repository.jpa.CategoryRepository;
import com.cosmosboard.fmh.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CategoryService")
public class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    SlugService slugService;

    @Mock
    private MessageSourceService messageSourceService;

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        void given_whenCount_thenAssertBody() {
            // Given
            when(categoryRepository.count()).thenReturn(1L);
            // When
            Long count = categoryService.count();
            // Then
            assertNotNull(count);
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findAll scenarios")
    class FindAllTest {
        @Test
        void given_whenFindAll_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            List<Category> categories = List.of(category);
            when(categoryService.findAll()).thenReturn(categories);
            // When
            List<Category> result = categoryService.findAll();
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(category, result.get(0));
            assertEquals(category.getId(), result.get(0).getId());
            assertEquals(category.getName(), result.get(0).getName());
            assertEquals(category.getDescription(), result.get(0).getDescription());
            assertEquals(category.getContent(), result.get(0).getContent());
            assertEquals(category.getIsActive(), result.get(0).getIsActive());
            assertEquals(category.getSort(), result.get(0).getSort());
            assertEquals(category.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findAllActivated scenarios")
    class FindAllActivatedTest {
        @Test
        void givenIsActive_whenFindAll_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            List<Category> categories = List.of(category);
            when(categoryRepository.findAllByIsActiveOrderBySortAsc(false)).thenReturn(categories);
            // When
            List<Category> result = categoryService.findAll(null);
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(category, result.get(0));
            assertEquals(category.getId(), result.get(0).getId());
            assertEquals(category.getName(), result.get(0).getName());
            assertEquals(category.getSlug(), result.get(0).getSlug());
            assertEquals(category.getDescription(), result.get(0).getDescription());
            assertEquals(category.getContent(), result.get(0).getContent());
            assertEquals(category.getIsActive(), result.get(0).getIsActive());
            assertEquals(category.getSort(), result.get(0).getSort());
            assertEquals(category.getCreatedAt(), result.get(0).getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.get(0).getUpdatedAt());

            verify(categoryRepository, times(1)).findAllByIsActiveOrderBySortAsc(false);
        }
    }

    @Nested
    @DisplayName("Test class for findAll with pagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        void givenCategoryCriteriaAndPaginationCriteria_whenFindAllWithPagination_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            Page<Category> page = new PageImpl<>(List.of(category));
            when(categoryRepository.findAll(any(CategoryFilterSpecification.class),
                    any(Pageable.class))).thenReturn(page);
            // When
            Page<Category> result = categoryService.findAll(null,
                    PaginationCriteria.builder().page(1).size(20).build());
            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getContent().size());
            assertEquals(category, result.getContent().get(0));
            assertEquals(category.getId(), result.getContent().get(0).getId());
            assertEquals(category.getName(), result.getContent().get(0).getName());
            assertEquals(category.getSlug(), result.getContent().get(0).getSlug());
            assertEquals(category.getDescription(), result.getContent().get(0).getDescription());
            assertEquals(category.getContent(), result.getContent().get(0).getContent());
            assertEquals(category.getIsActive(), result.getContent().get(0).getIsActive());
            assertEquals(category.getSort(), result.getContent().get(0).getSort());
            assertEquals(category.getCreatedAt(), result.getContent().get(0).getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.getContent().get(0).getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for findOneById scenarios")
    class FindOneByIdTest {
        @Test
        void givenId_whenFindOneById_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryRepository.findById(any(String.class))).thenReturn(Optional.of(category));
            // When
            Category result = categoryService.findOneById(category.getId());
            // Then
            assertNotNull(result);
            assertEquals(category, result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getSlug(), result.getSlug());
            assertEquals(category.getDescription(), result.getDescription());
            assertEquals(category.getContent(), result.getContent());
            assertEquals(category.getIsActive(), result.getIsActive());
            assertEquals(category.getSort(), result.getSort());
            assertEquals(category.getCreatedAt(), result.getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenId_whenFindOneById_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> categoryService.findOneById("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("category_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for findOneByIdAndIsActive scenarios")
    class FindOneByIdAndIsActiveWithIdTest{
        @Test
        void givenIdAndIsActive_whenFindOneByIdAndIsActiveWithNullIsActive_thenThrowNotFoundException() {
            // Given
            String id = "1";
            when(categoryRepository.findByIdAndIsActive(eq(id), anyBoolean())).thenReturn(Optional.empty());

            // When-Then
            assertThrows(NotFoundException.class, () -> categoryService.findOneByIdAndIsActive(id, null));
        }
        @Test
        void givenId_whenFindOneByIdAndActive_thenAsserBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryRepository.findByIdAndIsActive(category.getId(), true))
                    .thenReturn(Optional.of(category));
            // When
            Category result = categoryService.findOneByIdAndActive(category.getId());
            // Then
            assertNotNull(result);
            assertEquals(category, result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getSlug(), result.getSlug());
            assertEquals(category.getDescription(), result.getDescription());
            assertEquals(category.getContent(), result.getContent());
            assertEquals(category.getIsActive(), result.getIsActive());
            assertEquals(category.getSort(), result.getSort());
            assertEquals(category.getCreatedAt(), result.getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.getUpdatedAt());
        }

    }

    @Nested
    @DisplayName("Test class for findOneByIdOrSlug scenarios")
    class FindOneByIdOrSlugTest {
        @Test
        void givenIdOrSlug_whenFindOneByIdOrSlug_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryRepository.findOneByIdOrSlugIgnoreCase(any(String.class), any(String.class)))
                    .thenReturn(Optional.of(category));
            // When
            Category result = categoryService.findOneByIdOrSlug(category.getId());
            // Then
            assertNotNull(result);
            assertEquals(category, result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getSlug(), result.getSlug());
            assertEquals(category.getDescription(), result.getDescription());
            assertEquals(category.getContent(), result.getContent());
            assertEquals(category.getIsActive(), result.getIsActive());
            assertEquals(category.getSort(), result.getSort());
            assertEquals(category.getCreatedAt(), result.getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.getUpdatedAt());
        }

        @Test
        void givenIOdOrSlug_whenFindOneByIdOrSlug_thenThrowNotFoundException() {
            // When
            Executable closureToTest = () -> categoryService.findOneByIdOrSlug("xxx");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("category_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        void givenCreateCategoryRequest_whenCreateWithCategoryNameAlreadyExists_thenThrowBadRequestException() {
            // Given
            CreateCategoryRequest request = Factory.createCreateCategoryRequest();
            when(categoryRepository.existsByName(request.getName())).thenReturn(true);
            // When
            Executable closureToTest = () -> categoryService.create(request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("category_name_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
        @Test
        void givenCreateCategoryRequest_whenCreate_thenAssertBody() {
            // Given
            CreateCategoryRequest request = Factory.createCreateCategoryRequest();
            request.setName("Test Category");

            Category existingCategory = Factory.createCategory();
            existingCategory.setSlug("test-category");

            when(categoryRepository.existsByName(request.getName())).thenReturn(false);
            when(slugService.generate(request.getName())).thenReturn(existingCategory.getSlug());
            when(categoryRepository.countBySlugStartingWith(existingCategory.getSlug())).thenReturn(1L);

            Category newCategory = Category.builder()
                    .name(request.getName())
                    .slug(String.format("%s-%d", existingCategory.getSlug(), 2))
                    .description(request.getDescription())
                    .content(request.getContent())
                    .sort(1L)
                    .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                    .build();

            when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

            // When
            Category result = categoryService.create(request);

            // Then
            assertNotNull(result);
            assertEquals(newCategory, result);
            assertEquals(newCategory.getId(), result.getId());
            assertEquals(newCategory.getName(), result.getName());
            assertEquals(newCategory.getSlug(), result.getSlug());
            assertEquals(newCategory.getDescription(), result.getDescription());
            assertEquals(newCategory.getContent(), result.getContent());
            assertEquals(newCategory.getIsActive(), result.getIsActive());
            assertEquals(newCategory.getSort(), result.getSort());
            assertEquals(newCategory.getCreatedAt(), result.getCreatedAt());
            assertEquals(newCategory.getUpdatedAt(), result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        @Test
        void givenIdAndUpdateCategoryRequest_whenUpdateWithCategoryNameAlreadyExists_thenThrowBadRequestException() {
            // Given
            UpdateCategoryRequest request = Factory.createUpdateCategoryRequest();
            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndIdNot(request.getName(), category.getId()))
                    .thenReturn(true);
            // When
            Executable closureToTest = () -> categoryService.update(category.getId(), request);
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("category_name_already_exists"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }

        @Test
        void givenIdAndUpdateCategoryRequest_whenUpdateWithValidCategoryId_thenNullPointerException () {
            // Given
            UpdateCategoryRequest request = Factory.createUpdateCategoryRequest();
            request.setName("New Category Name");

            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndIdNot(request.getName(), category.getId())).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            long slugLikeCount = 1L;
            String expectedSlug = String.format("%s-%d", "new-category-name", slugLikeCount + 1);
            when(slugService.generate(request.getName())).thenReturn("new-category-name");
            when(categoryRepository.countBySlugStartingWithAndIdNot("new-category-name", category.getId()))
                    .thenReturn(slugLikeCount);

            // When
            Category result = categoryService.update(category.getId(), request);

            // Then
            assertNotNull(result);
            assertEquals(category, result);
            assertEquals(category.getId(), result.getId());
            assertEquals(request.getName(), result.getName());
            assertEquals(expectedSlug, result.getSlug());
        }

        @Test
        void givenIdAndUpdateCategoryRequest_whenUpdate_thenAssertBody() {
            // Given
            UpdateCategoryRequest request = Factory.createUpdateCategoryRequest();
            request.setContent("");
            request.setDescription("");

            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameAndIdNot(request.getName(), category.getId()))
                    .thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            // When
            Category result = categoryService.update(category.getId(), request);
            // Then
            assertNotNull(result);
            assertNull(result.getContent());
            assertNull(result.getDescription());
            assertEquals(category, result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            assertEquals(category.getSlug(), result.getSlug());
            assertEquals(category.getDescription(), result.getDescription());
            assertEquals(category.getContent(), result.getContent());
            assertEquals(category.getIsActive(), result.getIsActive());
            assertEquals(category.getSort(), result.getSort());
            assertEquals(category.getCreatedAt(), result.getCreatedAt());
            assertEquals(category.getUpdatedAt(), result.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Test class for sort scenarios")
    class SortTest {
        @Test
        void givenIdAndType_whenSortUp_thenAssertBody() {
            // Given
            Category categoryUp = Factory.createCategory();
            Category category = Factory.createCategory();
            category.setSort(2L);
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.findFirstBySortLessThanOrderBySortDesc(category.getSort())).thenReturn(categoryUp);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryRepository.save(categoryUp)).thenReturn(categoryUp);
            // When
            Executable closureToTest = () -> categoryService.sort(category.getId(),
                    AppConstants.EntitySortEnum.UP.name());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenIdAndType_whenSortDown_thenAssertBody() {
            // Given
            Category categoryDown = Factory.createCategory();
            categoryDown.setSort(2L);
            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(categoryRepository.findFirstBySortGreaterThanOrderBySortAsc(category.getSort()))
                    .thenReturn(categoryDown);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryRepository.save(categoryDown)).thenReturn(categoryDown);
            // When
            Executable closureToTest = () -> categoryService.sort(category.getId(),
                    AppConstants.EntitySortEnum.DOWN.name());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenIdAndType_whenSort_thenThrowNotFoundException() {
            // Given
            Category category = Factory.createCategory();
            category.setId(null);
            // When
            Executable closureToTest = () -> categoryService.sort(category.getId(), "XXX");
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("category_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }

        @Test
        void givenIdAndType_whenSort_thenThrowBadRequestException() {
            // Given
            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            // When
            Executable closureToTest = () -> categoryService.sort(category.getId(), "XXX");
            // Then
            assertThrows(BadRequestException.class, closureToTest);
            assertEquals(messageSourceService.get("invalid_sort_type"),
                    assertThrows(BadRequestException.class, closureToTest).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        void givenId_whenDelete_thenAssertBody() {
            // Given
            Category category = Factory.createCategory();
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).delete(category);
            when(categoryRepository.findAllByOrderBySortAsc()).thenReturn(List.of(category));
            // When
            Executable closureToTest = () -> categoryService.delete(category.getId());
            // Then
            Assertions.assertDoesNotThrow(closureToTest);
        }

        @Test
        void givenId_whenDelete_thenThrowNotFoundException() {
            // Given
            Category category = Factory.createCategory();
            category.setId(null);
            // When
            Executable closureToTest = () -> categoryService.delete(category.getId());
            // Then
            assertThrows(NotFoundException.class, closureToTest);
            assertEquals(messageSourceService.get("category_not_found"),
                    assertThrows(NotFoundException.class, closureToTest).getMessage());
        }
    }
}
