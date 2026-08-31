package com.cafex.pos.service;

import com.cafex.pos.dto.MenuAddonRequest;
import com.cafex.pos.dto.MenuAddonResponse;
import com.cafex.pos.dto.MenuAddonPageResponse;

import java.util.Optional;

public interface MenuAddonService {
    MenuAddonResponse saveMenuAddon(MenuAddonRequest menuAddonRequest);
    MenuAddonResponse updateMenuAddon(Long id, MenuAddonRequest menuAddonRequest);
    MenuAddonPageResponse getMenuAddonsWithFilters(String name, String restaurantId, int page, int size);
    Optional<MenuAddonResponse> getMenuAddonById(Long id);
    void deleteMenuAddon(Long id);
}
