package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantMenuCategoryPageResponse;
import com.cafex.pos.dto.RestaurantMenuCategoryRequest;
import com.cafex.pos.dto.RestaurantMenuCategoryResponse;
import com.cafex.pos.entity.MenuCategories;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.RestaurantMenuCategoriesRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantMenuCategoryServiceImpl implements RestaurantMenuCategoryService {

    private final RestaurantMenuCategoriesRepository restaurantMenuCategoriesRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/restaurant/";

    @Override
    public RestaurantMenuCategoryPageResponse getRestaurantMenuCategoriesWithFilters(Long restaurantId, String name, Boolean isActive, int page, int size) {
        log.info("Fetching restaurant menu categories for restaurantId: {} with filters - name: {}, isActive: {}, page: {}, size: {}", restaurantId, name, isActive, page, size);

        RestaurantMenuCategoryPageResponse response = new RestaurantMenuCategoryPageResponse();

        Specification<MenuCategories> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId));

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

        if (page == 0 && size == 0) {
            List<MenuCategories> filteredCategories = restaurantMenuCategoriesRepository.findAll(spec);
            filteredCategories.sort(java.util.Comparator.comparing(MenuCategories::getDisplayOrder));
            List<RestaurantMenuCategoryResponse> content = filteredCategories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            response.setData(content);
            response.setCurrentPage(1);
            response.setPageCount(1);
            response.setTotalRowCount(content.size());
            return response;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.ASC, "displayOrder"));
        Page<MenuCategories> categoryPage = restaurantMenuCategoriesRepository.findAll(spec, pageable);

        List<RestaurantMenuCategoryResponse> content = categoryPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RestaurantMenuCategoryPageResponse(
                content,
                categoryPage.getNumber() + 1,
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantMenuCategoryResponse> getAllRestaurantMenuCategories(Long restaurantId) {
        log.info("Fetching all restaurant menu categories for restaurantId: {}", restaurantId);
        List<MenuCategories> categories = restaurantMenuCategoriesRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantMenuCategoryResponse> getActiveRestaurantMenuCategories(Long restaurantId) {
        log.info("Fetching active restaurant menu categories for restaurantId: {}", restaurantId);
        List<MenuCategories> categories = restaurantMenuCategoriesRepository.findByRestaurantIdAndIsActiveOrderByDisplayOrderAsc(restaurantId, true);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantMenuCategoryResponse> getRestaurantMenuCategoryById(Long restaurantId, Long id) {
        log.info("Fetching restaurant menu category by ID: {} for restaurantId: {}", id, restaurantId);
        return restaurantMenuCategoriesRepository.findByRestaurantIdAndId(restaurantId, id)
                .map(this::convertToResponse);
    }

    @Override
    public RestaurantMenuCategoryResponse createRestaurantMenuCategory(RestaurantMenuCategoryRequest request) {
        log.info("Creating new restaurant menu category: {} for restaurantId: {}", request.getName(), request.getRestaurantId());

        if (restaurantMenuCategoriesRepository.existsByKeyAndRestaurantId(request.getKey(), request.getRestaurantId())) {
            throw new ConflictException("Menu category key already exists for this restaurant: " + request.getKey());
        }

        if (restaurantMenuCategoriesRepository.existsByNameAndRestaurantId(request.getName(), request.getRestaurantId())) {
            throw new ConflictException("Menu category name already exists for this restaurant: " + request.getName());
        }

        MenuCategories category = new MenuCategories();
        category.setCategoryId(generateCategoryId());
        category.setName(request.getName());
        category.setKey(request.getKey());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        category.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        category.setParentCategoryId(request.getParentCategoryId());
        category.setItemCount(0);
        category.setTotalValue(BigDecimal.ZERO);
        category.setPopularityScore(BigDecimal.ZERO);
        category.setCreatedBy(request.getCreatedBy());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        com.cafex.pos.entity.Restaurant restaurant = new com.cafex.pos.entity.Restaurant();
        restaurant.setId(request.getRestaurantId());
        category.setRestaurant(restaurant);

        MenuCategories savedCategory = restaurantMenuCategoriesRepository.save(category);
        log.info("Restaurant menu category created successfully with ID: {}", savedCategory.getId());

        RestaurantMenuCategoryResponse createdResponse = convertToResponse(savedCategory);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + savedCategory.getRestaurant().getId() + "/menu-categories", createdResponse);

        return createdResponse;
    }

    @Override
    public RestaurantMenuCategoryResponse updateRestaurantMenuCategory(Long restaurantId, Long id, RestaurantMenuCategoryRequest request) {
        log.info("Updating restaurant menu category with ID: {} for restaurantId: {}", id, restaurantId);

        MenuCategories existingCategory = restaurantMenuCategoriesRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with ID: " + id + " for this restaurant"));

        if (!existingCategory.getKey().equals(request.getKey()) &&
                restaurantMenuCategoriesRepository.existsByKeyAndRestaurantIdAndIdNot(request.getKey(), restaurantId, id)) {
            throw new ConflictException("Menu category key already exists for this restaurant: " + request.getKey());
        }

        if (!existingCategory.getName().equals(request.getName()) &&
                restaurantMenuCategoriesRepository.existsByNameAndRestaurantIdAndIdNot(request.getName(), restaurantId, id)) {
            throw new ConflictException("Menu category name already exists for this restaurant: " + request.getName());
        }

        existingCategory.setName(request.getName());
        existingCategory.setKey(request.getKey());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setIcon(request.getIcon());
        existingCategory.setColor(request.getColor());
        existingCategory.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : existingCategory.getDisplayOrder());
        existingCategory.setIsActive(request.getIsActive() != null ? request.getIsActive() : existingCategory.getIsActive());
        existingCategory.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : existingCategory.getIsDefault());
        existingCategory.setParentCategoryId(request.getParentCategoryId());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        MenuCategories updatedCategory = restaurantMenuCategoriesRepository.save(existingCategory);
        log.info("Restaurant menu category updated successfully with ID: {}", updatedCategory.getId());

        RestaurantMenuCategoryResponse updatedResponse = convertToResponse(updatedCategory);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/menu-categories", updatedResponse);

        return updatedResponse;
    }

    @Override
    public void deleteRestaurantMenuCategory(Long restaurantId, Long id) {
        log.info("Deleting restaurant menu category with ID: {} for restaurantId: {}", id, restaurantId);

        MenuCategories category = restaurantMenuCategoriesRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with ID: " + id + " for this restaurant"));

        restaurantMenuCategoriesRepository.delete(category);
        log.info("Restaurant menu category deleted successfully with ID: {}", id);

        RestaurantMenuCategoryResponse deletedResponse = convertToResponse(category);
        messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/menu-categories", deletedResponse);
    }

    private String generateCategoryId() {
        return "CAT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    private RestaurantMenuCategoryResponse convertToResponse(MenuCategories category) {
        RestaurantMenuCategoryResponse response = new RestaurantMenuCategoryResponse();
        response.setId(category.getId());
        response.setCategoryId(category.getCategoryId());
        response.setName(category.getName());
        response.setKey(category.getKey());
        response.setDescription(category.getDescription());
        response.setIcon(category.getIcon());
        response.setColor(category.getColor());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setIsActive(category.getIsActive());
        response.setIsDefault(category.getIsDefault());
        response.setParentCategoryId(category.getParentCategoryId());
        response.setRestaurantId(category.getRestaurant() != null ? category.getRestaurant().getId() : null);
        response.setItemCount(category.getItemCount());
        response.setTotalValue(category.getTotalValue());
        response.setPopularityScore(category.getPopularityScore());
        response.setLastOrdered(category.getLastOrdered());
        response.setCreatedBy(category.getCreatedBy());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
