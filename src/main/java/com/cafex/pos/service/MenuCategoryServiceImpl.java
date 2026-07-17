package com.cafex.pos.service;

import com.cafex.pos.dto.MenuCategoryPageResponse;
import com.cafex.pos.dto.MenuCategoryRequest;
import com.cafex.pos.dto.MenuCategoryResponse;
import com.cafex.pos.entity.MenuCategoriesMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.MenuCategoriesMasterRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final MenuCategoriesMasterRepository menuCategoriesMasterRepository;

    @Override
    public MenuCategoryPageResponse getMenuCategoriesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching menu categories with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        MenuCategoryPageResponse allResponse = new MenuCategoryPageResponse();
        allResponse.setData(getAllMenuCategories());
        allResponse.setCurrentPage(1);
        allResponse.setPageCount(1);
        allResponse.setTotalRowCount(allResponse.getData().size());

        if (page == 0 && size == 0) {
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<MenuCategoriesMaster> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        Page<MenuCategoriesMaster> categoryPage = menuCategoriesMasterRepository.findAll(spec, pageable);

        List<MenuCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuCategoryPageResponse(
                content,
                categoryPage.getNumber() + 1,
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getAllMenuCategories() {
        log.info("Fetching all menu categories");
        List<MenuCategoriesMaster> categories = menuCategoriesMasterRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuCategoryResponse> getMenuCategoryById(Long id) {
        log.info("Fetching menu category by ID: {}", id);
        return menuCategoriesMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public MenuCategoryResponse createMenuCategory(MenuCategoryRequest request) {
        log.info("Creating new menu category: {}", request.getName());

        if (menuCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Menu category key already exists: " + request.getKey());
        }

        MenuCategoriesMaster category = new MenuCategoriesMaster();
        category.setName(request.getName());
        category.setKey(request.getKey());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setCreatedBy(request.getCreatedBy());
        category.setUpdatedBy(request.getUpdatedBy());

        MenuCategoriesMaster savedCategory = menuCategoriesMasterRepository.save(category);
        log.info("Menu category created successfully with ID: {}", savedCategory.getId());

        return convertToResponse(savedCategory);
    }

    @Override
    public MenuCategoryResponse updateMenuCategory(Long id, MenuCategoryRequest request) {
        log.info("Updating menu category with ID: {}", id);

        MenuCategoriesMaster existingCategory = menuCategoriesMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with ID: " + id));

        if (!existingCategory.getKey().equals(request.getKey()) &&
                menuCategoriesMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Menu category key already exists: " + request.getKey());
        }

        existingCategory.setName(request.getName());
        existingCategory.setKey(request.getKey());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setIsActive(request.getIsActive());
        existingCategory.setDisplayOrder(request.getDisplayOrder());
        existingCategory.setUpdatedBy(request.getUpdatedBy());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        MenuCategoriesMaster updatedCategory = menuCategoriesMasterRepository.save(existingCategory);
        log.info("Menu category updated successfully with ID: {}", updatedCategory.getId());

        return convertToResponse(updatedCategory);
    }

    @Override
    public void deleteMenuCategory(Long id) {
        log.info("Deleting menu category with ID: {}", id);

        if (!menuCategoriesMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu category not found with ID: " + id);
        }

        menuCategoriesMasterRepository.deleteById(id);
        log.info("Menu category deleted successfully with ID: {}", id);
    }

    private MenuCategoryResponse convertToResponse(MenuCategoriesMaster category) {
        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setKey(category.getKey());
        response.setDescription(category.getDescription());
        response.setIsActive(category.getIsActive());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setCreatedBy(category.getCreatedBy());
        response.setUpdatedBy(category.getUpdatedBy());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
