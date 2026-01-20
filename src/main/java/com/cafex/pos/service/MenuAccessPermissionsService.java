package com.cafex.pos.service;

import com.cafex.pos.dto.MenuAccessPermissionsRequest;
import com.cafex.pos.dto.MenuAccessPermissionsResponse;
import com.cafex.pos.dto.MenuAccessPermissionsPageResponse;
import com.cafex.pos.entity.MenuAccessPermissions;
import com.cafex.pos.entity.NavigationMenu;
import com.cafex.pos.entity.UserRoles;
import com.cafex.pos.repository.MenuAccessPermissionsRepository;
import com.cafex.pos.repository.NavigationMenuRepository;
import com.cafex.pos.repository.UserRolesRepository;
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
public class MenuAccessPermissionsService {

    private final MenuAccessPermissionsRepository menuAccessPermissionsRepository;
    private final NavigationMenuRepository navigationMenuRepository;
    private final UserRolesRepository userRolesRepository;

    public MenuAccessPermissionsPageResponse getMenuAccessPermissionsWithFilters(String permissionId, String menuId, String roleId, int page, int size) {
        log.info("Fetching menu access permissions with filters - permissionId: {}, menuId: {}, roleId: {}, page: {}, size: {}",
                permissionId, menuId, roleId, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<MenuAccessPermissions> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (permissionId != null && !permissionId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("permissionId"), "%" + permissionId + "%"));
            }

            if (menuId != null && !menuId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("menu").get("id"), Long.parseLong(menuId)));
            }

            if (roleId != null && !roleId.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("role").get("id"), Long.parseLong(roleId)));
            }

            return predicate;
        };

        Page<MenuAccessPermissions> permissionPage = menuAccessPermissionsRepository.findAll(spec, pageable);

        List<MenuAccessPermissionsResponse> content = permissionPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new MenuAccessPermissionsPageResponse(
            content,
            permissionPage.getNumber() + 1,
            permissionPage.getTotalPages(),
            permissionPage.getTotalElements()
        );
    }

    public Optional<MenuAccessPermissionsResponse> getMenuAccessPermissionById(Long id) {
        log.info("Fetching menu access permission by ID: {}", id);
        return menuAccessPermissionsRepository.findById(id)
                .map(this::convertToResponse);
    }

    public List<MenuAccessPermissionsResponse> getMenuAccessPermissionsByRoleId(Long roleId) {
        log.info("Fetching menu access permissions by role ID: {}", roleId);
        List<MenuAccessPermissions> permissions = menuAccessPermissionsRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MenuAccessPermissionsResponse saveMenuAccessPermission(MenuAccessPermissionsRequest request) {
        log.info("Saving new menu access permission: {}", request.getPermissionId());

        if (menuAccessPermissionsRepository.existsByPermissionId(request.getPermissionId())) {
            throw new RuntimeException("Permission ID already exists: " + request.getPermissionId());
        }

        NavigationMenu menu = navigationMenuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found with ID: " + request.getMenuId()));

        UserRoles role = userRolesRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getRoleId()));

        MenuAccessPermissions permission = new MenuAccessPermissions();
        permission.setPermissionId(request.getPermissionId());
        permission.setMenu(menu);
        permission.setRole(role);
        permission.setCanView(request.getCanView());
        permission.setCanEdit(request.getCanEdit());
        permission.setCanDelete(request.getCanDelete());
        permission.setCanCreate(request.getCanCreate());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        permission.setCreatedBy(request.getCreatedBy());

        MenuAccessPermissions savedPermission = menuAccessPermissionsRepository.save(permission);
        log.info("Menu access permission saved successfully with ID: {}", savedPermission.getId());

        return convertToResponse(savedPermission);
    }

    public MenuAccessPermissionsResponse updateMenuAccessPermission(Long id, MenuAccessPermissionsRequest request) {
        log.info("Updating menu access permission with ID: {}", id);

        MenuAccessPermissions existingPermission = menuAccessPermissionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu access permission not found with ID: " + id));

        if (!existingPermission.getPermissionId().equals(request.getPermissionId()) &&
            menuAccessPermissionsRepository.existsByPermissionId(request.getPermissionId())) {
            throw new RuntimeException("Permission ID already exists: " + request.getPermissionId());
        }

        NavigationMenu menu = navigationMenuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found with ID: " + request.getMenuId()));

        UserRoles role = userRolesRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getRoleId()));

        existingPermission.setPermissionId(request.getPermissionId());
        existingPermission.setMenu(menu);
        existingPermission.setRole(role);
        existingPermission.setCanView(request.getCanView());
        existingPermission.setCanEdit(request.getCanEdit());
        existingPermission.setCanDelete(request.getCanDelete());
        existingPermission.setCanCreate(request.getCanCreate());
        existingPermission.setUpdatedAt(LocalDateTime.now());

        MenuAccessPermissions updatedPermission = menuAccessPermissionsRepository.save(existingPermission);
        log.info("Menu access permission updated successfully with ID: {}", updatedPermission.getId());

        return convertToResponse(updatedPermission);
    }

    public void deleteMenuAccessPermission(Long id) {
        log.info("Deleting menu access permission with ID: {}", id);

        if (!menuAccessPermissionsRepository.existsById(id)) {
            throw new RuntimeException("Menu access permission not found with ID: " + id);
        }

        menuAccessPermissionsRepository.deleteById(id);
        log.info("Menu access permission deleted successfully with ID: {}", id);
    }

    private MenuAccessPermissionsResponse convertToResponse(MenuAccessPermissions permission) {
        MenuAccessPermissionsResponse response = new MenuAccessPermissionsResponse();
        response.setId(permission.getId());
        response.setPermissionId(permission.getPermissionId());
        response.setMenuId(permission.getMenu().getId());
        response.setRoleId(permission.getRole().getId());
        response.setCanView(permission.getCanView());
        response.setCanEdit(permission.getCanEdit());
        response.setCanDelete(permission.getCanDelete());
        response.setCanCreate(permission.getCanCreate());
        response.setCreatedAt(permission.getCreatedAt());
        response.setUpdatedAt(permission.getUpdatedAt());
        response.setCreatedBy(permission.getCreatedBy());
        response.setUpdatedBy(permission.getUpdatedBy());
        return response;
    }
}