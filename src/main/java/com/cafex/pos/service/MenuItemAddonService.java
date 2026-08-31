package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemAddonRequest;
import com.cafex.pos.dto.MenuItemAddonResponse;
import com.cafex.pos.dto.MenuItemAddonPageResponse;

import java.util.List;
import java.util.Optional;

public interface MenuItemAddonService {
    MenuItemAddonResponse linkAddonToMenuItem(Long menuItemId, MenuItemAddonRequest request);
    MenuItemAddonResponse updateMenuItemAddon(Long id, MenuItemAddonRequest request);
    List<MenuItemAddonResponse> getAddonsByMenuItemId(Long menuItemId);
    void unlinkAddonFromMenuItem(Long id);
    void deleteByMenuItemId(Long menuItemId);
}
