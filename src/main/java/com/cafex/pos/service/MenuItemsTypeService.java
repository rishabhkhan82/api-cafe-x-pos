package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemsTypePageResponse;
import com.cafex.pos.dto.MenuItemsTypeRequest;
import com.cafex.pos.dto.MenuItemsTypeResponse;
import java.util.List;
import java.util.Optional;

public interface MenuItemsTypeService {
    MenuItemsTypePageResponse getMenuItemsTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<MenuItemsTypeResponse> getAllMenuItemsTypes();
    Optional<MenuItemsTypeResponse> getMenuItemsTypeById(Long id);
    MenuItemsTypeResponse createMenuItemsType(MenuItemsTypeRequest request);
    MenuItemsTypeResponse updateMenuItemsType(Long id, MenuItemsTypeRequest request);
    void deleteMenuItemsType(Long id);
}
