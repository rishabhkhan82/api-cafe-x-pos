package com.cafex.pos.service;

import com.cafex.pos.dto.NavigationMenuRequest;
import com.cafex.pos.dto.NavigationMenuResponse;
import com.cafex.pos.dto.NavigationMenuPageResponse;
import com.cafex.pos.entity.NavigationMenu;
import com.cafex.pos.repository.NavigationMenuRepository;
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
public class NavigationMenuService {

    private final NavigationMenuRepository navigationMenuRepository;

    public NavigationMenuPageResponse getNavigationMenusWithFilters(String name, String role, Boolean isActive, String type, int page, int size) {
        log.info("Fetching navigation menus with filters - name: {}, role: {}, isActive: {}, type: {}, page: {}, size: {}",
                name, role, isActive, type, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<NavigationMenu> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                String searchTerm = "%" + name.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
            }

            // Role filter
            if (role != null && !role.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("role"), role));
            }

            // IsActive filter
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            // Type filter
            if (type != null && !type.trim().isEmpty()) {
                try {
                    NavigationMenu.MenuType menuType = NavigationMenu.MenuType.valueOf(type.toUpperCase());
                    predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("type"), menuType));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid menu type provided: {}", type);
                }
            }

            return predicate;
        };

        Page<NavigationMenu> menuPage = navigationMenuRepository.findAll(spec, pageable);

        List<NavigationMenuResponse> content = menuPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new NavigationMenuPageResponse(
            content,
            menuPage.getNumber() + 1, // currentPage (1-based)
            menuPage.getTotalPages(),
            menuPage.getTotalElements()
        );
    }

    public Optional<NavigationMenuResponse> getNavigationMenuById(Long id) {
        log.info("Fetching navigation menu by ID: {}", id);
        return navigationMenuRepository.findById(id)
                .map(this::convertToResponse);
    }

    public List<NavigationMenuResponse> getAllNavigationMenus() {
        log.info("Fetching all navigation menus");
        List<NavigationMenu> menus = navigationMenuRepository.findAll();
        return menus.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public NavigationMenuResponse saveNavigationMenu(NavigationMenuRequest navigationMenuRequest) {
        log.info("Saving new navigation menu: {}", navigationMenuRequest.getName());

        // Check if menuId already exists
        if (navigationMenuRepository.existsByMenuId(navigationMenuRequest.getMenuId())) {
            throw new RuntimeException("Menu ID already exists: " + navigationMenuRequest.getMenuId());
        }

        NavigationMenu navigationMenu = new NavigationMenu();
        navigationMenu.setMenuId(navigationMenuRequest.getMenuId());
        navigationMenu.setName(navigationMenuRequest.getName());
        navigationMenu.setParentId(navigationMenuRequest.getParentId());
        navigationMenu.setPath(navigationMenuRequest.getPath());
        navigationMenu.setIcon(navigationMenuRequest.getIcon());
        navigationMenu.setDisplayOrder(navigationMenuRequest.getDisplayOrder() != null ? navigationMenuRequest.getDisplayOrder() : 0);
        navigationMenu.setType(navigationMenuRequest.getType());
        navigationMenu.setIsActive(navigationMenuRequest.getIsActive() != null ? navigationMenuRequest.getIsActive() : true);
        navigationMenu.setCreatedBy(navigationMenuRequest.getCreatedBy());
        navigationMenu.setUpdatedBy(navigationMenuRequest.getUpdatedBy());
        navigationMenu.setCreatedAt(LocalDateTime.now());
        navigationMenu.setUpdatedAt(LocalDateTime.now());

        NavigationMenu savedMenu = navigationMenuRepository.save(navigationMenu);
        log.info("Navigation menu saved successfully with ID: {}", savedMenu.getId());

        return convertToResponse(savedMenu);
    }

    public NavigationMenuResponse updateNavigationMenu(Long id, NavigationMenuRequest navigationMenuRequest) {
        log.info("Updating navigation menu with ID: {}", id);

        NavigationMenu existingMenu = navigationMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Navigation menu not found with ID: " + id));

        // Check menuId uniqueness if changed
        if (!existingMenu.getMenuId().equals(navigationMenuRequest.getMenuId()) &&
            navigationMenuRepository.existsByMenuId(navigationMenuRequest.getMenuId())) {
            throw new RuntimeException("Menu ID already exists: " + navigationMenuRequest.getMenuId());
        }

        // Update fields
        existingMenu.setMenuId(navigationMenuRequest.getMenuId());
        existingMenu.setName(navigationMenuRequest.getName());
        existingMenu.setParentId(navigationMenuRequest.getParentId());
        existingMenu.setPath(navigationMenuRequest.getPath());
        existingMenu.setIcon(navigationMenuRequest.getIcon());
        existingMenu.setDisplayOrder(navigationMenuRequest.getDisplayOrder());
        existingMenu.setType(navigationMenuRequest.getType());
        existingMenu.setIsActive(navigationMenuRequest.getIsActive());
        existingMenu.setUpdatedBy(navigationMenuRequest.getUpdatedBy());
        existingMenu.setUpdatedAt(LocalDateTime.now());

        NavigationMenu updatedMenu = navigationMenuRepository.save(existingMenu);
        log.info("Navigation menu updated successfully with ID: {}", updatedMenu.getId());

        return convertToResponse(updatedMenu);
    }

    public void deleteNavigationMenu(Long id) {
        log.info("Deleting navigation menu with ID: {}", id);

        if (!navigationMenuRepository.existsById(id)) {
            throw new RuntimeException("Navigation menu not found with ID: " + id);
        }

        navigationMenuRepository.deleteById(id);
        log.info("Navigation menu deleted successfully with ID: {}", id);
    }

    private NavigationMenuResponse convertToResponse(NavigationMenu navigationMenu) {
        NavigationMenuResponse response = new NavigationMenuResponse();
        response.setId(navigationMenu.getId());
        response.setMenuId(navigationMenu.getMenuId());
        response.setName(navigationMenu.getName());
        response.setParentId(navigationMenu.getParentId());
        response.setPath(navigationMenu.getPath());
        response.setIcon(navigationMenu.getIcon());
        response.setDisplayOrder(navigationMenu.getDisplayOrder());
        response.setType(navigationMenu.getType());
        response.setIsActive(navigationMenu.getIsActive());
        response.setCreatedBy(navigationMenu.getCreatedBy());
        response.setUpdatedBy(navigationMenu.getUpdatedBy());
        response.setCreatedAt(navigationMenu.getCreatedAt());
        response.setUpdatedAt(navigationMenu.getUpdatedAt());
        return response;
    }
}