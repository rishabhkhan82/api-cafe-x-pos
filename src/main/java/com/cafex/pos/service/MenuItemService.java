package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemRequest;
import com.cafex.pos.dto.MenuItemResponse;
import com.cafex.pos.dto.MenuItemPageResponse;

import java.util.Optional;

public interface MenuItemService {
    MenuItemResponse saveMenuItem(MenuItemRequest menuItemRequest);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest menuItemRequest);
    MenuItemPageResponse getMenuItemsWithFilters(String name, String category, String restaurantId, String isAvailable, String isActive, String isVegetarian, String isSpicy, String isPopular, String isFeatured, String isRecommended, int page, int size);
    Optional<MenuItemResponse> getMenuItemById(Long id);
    void deleteMenuItem(Long id);
}