package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemsTypePageResponse;
import com.cafex.pos.dto.MenuItemsTypeRequest;
import com.cafex.pos.dto.MenuItemsTypeResponse;
import com.cafex.pos.entity.MenuItemsTypeMaster;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.MenuItemsTypeMasterRepository;
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
public class MenuItemsTypeServiceImpl implements MenuItemsTypeService {

    private final MenuItemsTypeMasterRepository menuItemsTypeMasterRepository;

    @Override
    public MenuItemsTypePageResponse getMenuItemsTypesWithFilters(String name, Boolean isActive, int page, int size) {
        log.info("Fetching menu items types with filters - name: {}, isActive: {}, page: {}, size: {}",
                name, isActive, page, size);

        MenuItemsTypePageResponse allResponse = new MenuItemsTypePageResponse();

        Specification<MenuItemsTypeMaster> spec = (root, query, criteriaBuilder) -> {
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

        if (page == 0 && size == 0) {
            List<MenuItemsTypeMaster> filteredTypes = menuItemsTypeMasterRepository.findAll(spec);
            List<MenuItemsTypeResponse> content = filteredTypes.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            allResponse.setData(content);
            allResponse.setCurrentPage(1);
            allResponse.setPageCount(1);
            allResponse.setTotalRowCount(content.size());
            return allResponse;
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<MenuItemsTypeMaster> typePage = menuItemsTypeMasterRepository.findAll(spec, pageable);

        List<MenuItemsTypeResponse> content = typePage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuItemsTypePageResponse(
                content,
                typePage.getNumber() + 1,
                typePage.getTotalPages(),
                typePage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsTypeResponse> getAllMenuItemsTypes() {
        log.info("Fetching all menu items types");
        List<MenuItemsTypeMaster> types = menuItemsTypeMasterRepository.findAll();
        return types.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItemsTypeResponse> getMenuItemsTypeById(Long id) {
        log.info("Fetching menu items type by ID: {}", id);
        return menuItemsTypeMasterRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public MenuItemsTypeResponse createMenuItemsType(MenuItemsTypeRequest request) {
        log.info("Creating new menu items type: {}", request.getName());

        if (menuItemsTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Menu items type key already exists: " + request.getKey());
        }

        MenuItemsTypeMaster type = new MenuItemsTypeMaster();
        type.setName(request.getName());
        type.setKey(request.getKey());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        type.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        type.setColorClasses(request.getColorClasses());
        type.setIcon(request.getIcon());
        type.setCreatedBy(request.getCreatedBy());
        type.setUpdatedBy(request.getUpdatedBy());

        MenuItemsTypeMaster savedType = menuItemsTypeMasterRepository.save(type);
        log.info("Menu items type created successfully with ID: {}", savedType.getId());

        return convertToResponse(savedType);
    }

    @Override
    public MenuItemsTypeResponse updateMenuItemsType(Long id, MenuItemsTypeRequest request) {
        log.info("Updating menu items type with ID: {}", id);

        MenuItemsTypeMaster existingType = menuItemsTypeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu items type not found with ID: " + id));

        if (!existingType.getKey().equals(request.getKey()) &&
                menuItemsTypeMasterRepository.existsByKey(request.getKey())) {
            throw new ConflictException("Menu items type key already exists: " + request.getKey());
        }

        existingType.setName(request.getName());
        existingType.setKey(request.getKey());
        existingType.setDescription(request.getDescription());
        existingType.setIsActive(request.getIsActive());
        existingType.setDisplayOrder(request.getDisplayOrder());
        existingType.setColorClasses(request.getColorClasses());
        existingType.setIcon(request.getIcon());
        existingType.setUpdatedBy(request.getUpdatedBy());
        existingType.setUpdatedAt(LocalDateTime.now());

        MenuItemsTypeMaster updatedType = menuItemsTypeMasterRepository.save(existingType);
        log.info("Menu items type updated successfully with ID: {}", updatedType.getId());

        return convertToResponse(updatedType);
    }

    @Override
    public void deleteMenuItemsType(Long id) {
        log.info("Deleting menu items type with ID: {}", id);

        if (!menuItemsTypeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu items type not found with ID: " + id);
        }

        menuItemsTypeMasterRepository.deleteById(id);
        log.info("Menu items type deleted successfully with ID: {}", id);
    }

    private MenuItemsTypeResponse convertToResponse(MenuItemsTypeMaster type) {
        MenuItemsTypeResponse response = new MenuItemsTypeResponse();
        response.setId(type.getId());
        response.setName(type.getName());
        response.setKey(type.getKey());
        response.setDescription(type.getDescription());
        response.setIsActive(type.getIsActive());
        response.setDisplayOrder(type.getDisplayOrder());
        response.setColorClasses(type.getColorClasses());
        response.setIcon(type.getIcon());
        response.setCreatedBy(type.getCreatedBy());
        response.setUpdatedBy(type.getUpdatedBy());
        response.setCreatedAt(type.getCreatedAt());
        response.setUpdatedAt(type.getUpdatedAt());
        return response;
    }
}
