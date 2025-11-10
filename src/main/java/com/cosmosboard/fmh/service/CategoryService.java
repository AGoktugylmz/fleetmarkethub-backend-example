package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.category.CreateCategoryRequest;
import com.cosmosboard.fmh.dto.request.category.UpdateCategoryRequest;
import com.cosmosboard.fmh.entity.Category;
import com.cosmosboard.fmh.entity.specification.CategoryFilterSpecification;
import com.cosmosboard.fmh.entity.specification.criteria.CategoryCriteria;
import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.exception.NotFoundException;
import com.cosmosboard.fmh.repository.jpa.CategoryRepository;
import com.cosmosboard.fmh.util.AppConstants;
import com.cosmosboard.fmh.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private static final String CATEGORY_NOT_FOUND = "category_not_found";

    private final CategoryRepository categoryRepository;

    private final SlugService slugService;

    private final MessageSourceService messageSourceService;

    /**
     * Count all categories.
     *
     * @return long
     */
    public long count() {
        return categoryRepository.count();
    }

    /**
     * Find all categories.
     *
     * @return List of Category
     */
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderBySortAsc();
    }

    /**
     * Find all categories by activity status.
     *
     * @param isActive Boolean
     * @return List of Category
     */
    public List<Category> findAll(Boolean isActive) {
        if (isActive == null) {
            isActive = false;
        }

        return categoryRepository.findAllByIsActiveOrderBySortAsc(isActive);
    }

    /**
     * Find all categories with pagination.
     *
     * @param cityCriteria       CityCriteria
     * @param paginationCriteria PaginationCriteria
     * @return Page of Category
     */
    public Page<Category> findAll(CategoryCriteria cityCriteria, PaginationCriteria paginationCriteria) {
        return categoryRepository.findAll(new CategoryFilterSpecification(cityCriteria),
            PageRequestBuilder.build(paginationCriteria));
    }

    /**
     * Find one category by ID.
     *
     * @param id String
     * @return Category
     */
    public Category findOneById(String id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(CATEGORY_NOT_FOUND)));
    }

    /**
     * Find one category by ID and is active.
     *
     * @param id String
     * @return Category
     */
    public Category findOneByIdAndIsActive(String id, Boolean isActive) {
        if (isActive == null) {
            isActive = false;
        }

        return categoryRepository.findByIdAndIsActive(id, isActive)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(CATEGORY_NOT_FOUND)));
    }

    /**
     * Find one category by ID and active.
     *
     * @param id String
     * @return Category
     */
    public Category findOneByIdAndActive(String id) {
        return findOneByIdAndIsActive(id, true);
    }

    /**
     * Find one category by ID or slug.
     *
     * @param idOrSlug String
     * @return Category
     */
    public Category findOneByIdOrSlug(String idOrSlug) {
        return categoryRepository.findOneByIdOrSlugIgnoreCase(idOrSlug, idOrSlug)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get(CATEGORY_NOT_FOUND)));
    }

    /**
     * Create new category from request.
     *
     * @param request CreateCategoryRequest
     * @return Category
     */
    public Category create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            log.error("Category with name {} already exists", request.getName());
            String message = messageSourceService.get("category_already_exists");
            log.error(message);
            throw new BadRequestException(message);
        }

        String slug = slugService.generate(request.getName());
        long slugLikeCount = categoryRepository.countBySlugStartingWith(slug);
        if (slugLikeCount > 0) {
            slug = String.format("%s-%d", slug, slugLikeCount + 1);
        }

        return categoryRepository.save(Category.builder()
            .name(request.getName())
            .slug(slug)
            .description(request.getDescription())
            .content(request.getContent())
            .sort(count() + 1)
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .build());
    }

    /**
     * Update category from request.
     *
     * @param id      String
     * @param request UpdateCategoryRequest
     * @return Category
     */
    public Category update(String id, UpdateCategoryRequest request) {
        Category category = findOneById(id);

        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            log.error("Category with name {} already exists", request.getName());
            throw new BadRequestException(messageSourceService.get("category_already_exists"));
        }

        if (request.getName() != null && !category.getName().equals(request.getName())) {
            String slug = slugService.generate(request.getName());
            long slugLikeCount = categoryRepository.countBySlugStartingWithAndIdNot(slug, id);
            if (slugLikeCount > 0) {
                slug = String.format("%s-%d", slug, slugLikeCount + 1);
            }

            category.setName(request.getName());
            category.setSlug(slug);
        }

        if (request.getDescription() != null) {
            if (request.getDescription().equals("")) {
                request.setDescription(null);
            }

            category.setDescription(request.getDescription());
        }

        if (request.getContent() != null) {
            if (request.getContent().equals("")) {
                request.setContent(null);
            }

            category.setContent(request.getContent());
        }

        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : category.getIsActive());

        return categoryRepository.save(category);
    }

    /**
     * Sort category by ID.
     *
     * @param id   String
     * @param type String (up|down)
     */
    public void sort(String id, String type) {
        Category category = findOneById(id);

        try {
            AppConstants.EntitySortEnum entitySortEnum = AppConstants.EntitySortEnum.valueOf(type.toUpperCase());
            if (entitySortEnum.equals(AppConstants.EntitySortEnum.UP)) {
                Category categoryUp = categoryRepository.findFirstBySortLessThanOrderBySortDesc(category.getSort());
                if (categoryUp != null) {
                    Long sort = category.getSort();
                    category.setSort(categoryUp.getSort());
                    categoryUp.setSort(sort);

                    categoryRepository.save(category);
                    categoryRepository.save(categoryUp);
                }
                return;
            }

            Category categoryDown = categoryRepository.findFirstBySortGreaterThanOrderBySortAsc(category.getSort());
            if (categoryDown != null) {
                Long sort = category.getSort();
                category.setSort(categoryDown.getSort());
                categoryDown.setSort(sort);

                categoryRepository.save(category);
                categoryRepository.save(categoryDown);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort type {}", type);
            String message = messageSourceService.get("invalid_sort_type");
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    /**
     * Deletes a category by its ID from the repository and resorts the remaining categories.
     *
     * @param id String
     */
    public void delete(String id) {
        categoryRepository.delete(findOneById(id));

        // Resort categories
        List<Category> categories = categoryRepository.findAllByOrderBySortAsc();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            category.setSort((long) i);
            categoryRepository.save(category);
        }
    }
}
