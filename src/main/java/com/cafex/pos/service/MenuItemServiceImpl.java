package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemRequest;
import com.cafex.pos.dto.MenuItemResponse;
import com.cafex.pos.dto.MenuItemPageResponse;
import com.cafex.pos.entity.MenuItem;
import com.cafex.pos.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public MenuItemResponse saveMenuItem(MenuItemRequest menuItemRequest) {
        log.info("Saving new menu item: {}", menuItemRequest.getItemId());

        // Check if itemId already exists
        if (menuItemRepository.existsByItemId(menuItemRequest.getItemId())) {
            throw new RuntimeException("Item ID already exists: " + menuItemRequest.getItemId());
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(menuItemRequest.getItemId());
        menuItem.setName(menuItemRequest.getName());
        menuItem.setDescription(menuItemRequest.getDescription());
        menuItem.setPrice(menuItemRequest.getPrice());
        menuItem.setOriginalPrice(menuItemRequest.getOriginalPrice());
        menuItem.setCategory(menuItemRequest.getCategory());
        menuItem.setImage(menuItemRequest.getImage());
        menuItem.setIsAvailable(menuItemRequest.getIsAvailable() != null ? menuItemRequest.getIsAvailable() : true);
        menuItem.setIsActive(menuItemRequest.getIsActive() != null ? menuItemRequest.getIsActive() : true);
        menuItem.setIsVegetarian(menuItemRequest.getIsVegetarian() != null ? menuItemRequest.getIsVegetarian() : false);
        menuItem.setIsVeg(menuItemRequest.getIsVeg());
        menuItem.setIsSpicy(menuItemRequest.getIsSpicy() != null ? menuItemRequest.getIsSpicy() : false);
        menuItem.setIsPopular(menuItemRequest.getIsPopular() != null ? menuItemRequest.getIsPopular() : false);
        menuItem.setPreparationTime(menuItemRequest.getPreparationTime());
        menuItem.setDiscount(menuItemRequest.getDiscount());
        menuItem.setRestaurantId(menuItemRequest.getRestaurantId());
        menuItem.setCreatedAt(menuItemRequest.getCreatedAt() != null ? menuItemRequest.getCreatedAt() : LocalDateTime.now());
        menuItem.setUpdatedAt(menuItemRequest.getUpdatedAt() != null ? menuItemRequest.getUpdatedAt() : LocalDateTime.now());
        menuItem.setCreatedBy(menuItemRequest.getCreatedBy());
        menuItem.setUpdatedBy(menuItemRequest.getUpdatedBy());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item saved successfully with ID: {}", savedMenuItem.getId());

        return convertToResponse(savedMenuItem);
    }

    @Override
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest menuItemRequest) {
        log.info("Updating menu item with ID: {}", id);

        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + id));

        // Check itemId uniqueness if changed
        if (!existingMenuItem.getItemId().equals(menuItemRequest.getItemId()) &&
            menuItemRepository.existsByItemId(menuItemRequest.getItemId())) {
            throw new RuntimeException("Item ID already exists: " + menuItemRequest.getItemId());
        }

        // Update fields
        existingMenuItem.setItemId(menuItemRequest.getItemId());
        existingMenuItem.setName(menuItemRequest.getName());
        existingMenuItem.setDescription(menuItemRequest.getDescription());
        existingMenuItem.setPrice(menuItemRequest.getPrice());
        existingMenuItem.setOriginalPrice(menuItemRequest.getOriginalPrice());
        existingMenuItem.setCategory(menuItemRequest.getCategory());
        existingMenuItem.setImage(menuItemRequest.getImage());
        existingMenuItem.setIsAvailable(menuItemRequest.getIsAvailable());
        existingMenuItem.setIsActive(menuItemRequest.getIsActive());
        existingMenuItem.setIsVegetarian(menuItemRequest.getIsVegetarian());
        existingMenuItem.setIsVeg(menuItemRequest.getIsVeg());
        existingMenuItem.setIsSpicy(menuItemRequest.getIsSpicy());
        existingMenuItem.setIsPopular(menuItemRequest.getIsPopular());
        existingMenuItem.setPreparationTime(menuItemRequest.getPreparationTime());
        existingMenuItem.setDiscount(menuItemRequest.getDiscount());
        existingMenuItem.setRestaurantId(menuItemRequest.getRestaurantId());
        existingMenuItem.setUpdatedAt(LocalDateTime.now());
        existingMenuItem.setUpdatedBy(menuItemRequest.getUpdatedBy());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        log.info("Menu item updated successfully with ID: {}", updatedMenuItem.getId());

        return convertToResponse(updatedMenuItem);
    }

    @Override
    public MenuItemPageResponse getMenuItemsWithFilters(String name, String category, String restaurantId, String isAvailable, String isActive, String isVegetarian, String isSpicy, int page, int size) {
        log.info("Fetching menu items with filters - name: {}, category: {}, restaurantId: {}, isAvailable: {}, isActive: {}, isVegetarian: {}, isSpicy: {}, page: {}, size: {}",
                name, category, restaurantId, isAvailable, isActive, isVegetarian, isSpicy, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<MenuItem> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm)
                );
                predicate = criteriaBuilder.and(predicate, namePredicate);
            }

            // Category filter
            if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category"), category));
            }

            // Restaurant filter
            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurantId"), id));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            // Is Available filter
            if (isAvailable != null && !isAvailable.trim().isEmpty() && !"all".equals(isAvailable)) {
                Boolean available = "true".equals(isAvailable);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isAvailable"), available));
            }

            // Is Active filter
            if (isActive != null && !isActive.trim().isEmpty() && !"all".equals(isActive)) {
                Boolean active = "true".equals(isActive);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), active));
            }

            // Is Vegetarian filter
            if (isVegetarian != null && !isVegetarian.trim().isEmpty() && !"all".equals(isVegetarian)) {
                Boolean vegetarian = "true".equals(isVegetarian);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isVegetarian"), vegetarian));
            }

            // Is Spicy filter
            if (isSpicy != null && !isSpicy.trim().isEmpty() && !"all".equals(isSpicy)) {
                Boolean spicy = "true".equals(isSpicy);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isSpicy"), spicy));
            }

            return predicate;
        };

        Page<MenuItem> menuItemPage = menuItemRepository.findAll(spec, pageable);

        List<MenuItemResponse> content = menuItemPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuItemPageResponse(
            content,
            menuItemPage.getNumber() + 1, // currentPage (1-based)
            menuItemPage.getTotalPages(),
            menuItemPage.getTotalElements()
        );
    }

    @Override
    public Optional<MenuItemResponse> getMenuItemById(Long id) {
        log.info("Fetching menu item by ID: {}", id);
        return menuItemRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteMenuItem(Long id) {
        log.info("Deleting menu item with ID: {}", id);

        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found with ID: " + id);
        }

        menuItemRepository.deleteById(id);
        log.info("Menu item deleted successfully with ID: {}", id);
    }

    private MenuItemResponse convertToResponse(MenuItem menuItem) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setItemId(menuItem.getItemId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setPrice(menuItem.getPrice());
        response.setOriginalPrice(menuItem.getOriginalPrice());
        response.setCategory(menuItem.getCategory());
        response.setImage(menuItem.getImage());
        response.setIsAvailable(menuItem.getIsAvailable());
        response.setIsActive(menuItem.getIsActive());
        response.setIsVegetarian(menuItem.getIsVegetarian());
        response.setIsVeg(menuItem.getIsVeg());
        response.setIsSpicy(menuItem.getIsSpicy());
        response.setIsPopular(menuItem.getIsPopular());
        response.setPreparationTime(menuItem.getPreparationTime());
        response.setDiscount(menuItem.getDiscount());
        response.setRestaurantId(menuItem.getRestaurantId());
        response.setCreatedAt(menuItem.getCreatedAt());
        response.setUpdatedAt(menuItem.getUpdatedAt());
        response.setCreatedBy(menuItem.getCreatedBy());
        response.setUpdatedBy(menuItem.getUpdatedBy());
        return response;
    }
}