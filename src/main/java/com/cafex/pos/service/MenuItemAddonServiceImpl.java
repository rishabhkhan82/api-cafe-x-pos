package com.cafex.pos.service;

import com.cafex.pos.dto.MenuItemAddonRequest;
import com.cafex.pos.dto.MenuItemAddonResponse;
import com.cafex.pos.entity.MenuItemAddon;
import com.cafex.pos.entity.MenuAddon;
import com.cafex.pos.repository.MenuItemAddonRepository;
import com.cafex.pos.repository.MenuAddonRepository;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuItemAddonServiceImpl implements MenuItemAddonService {

    private final MenuItemAddonRepository menuItemAddonRepository;
    private final MenuAddonRepository menuAddonRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/restaurant/";

    @Override
    public MenuItemAddonResponse linkAddonToMenuItem(Long menuItemId, MenuItemAddonRequest request) {
        log.info("Linking add-on {} to menu item {}", request.getAddonId(), menuItemId);

        if (menuItemAddonRepository.existsByMenuItemIdAndAddonId(menuItemId, request.getAddonId())) {
            throw new ConflictException("Add-on already linked to this menu item");
        }

        MenuItemAddon link = new MenuItemAddon();
        link.setMenuItemId(menuItemId);
        link.setAddonId(request.getAddonId());
        link.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
        link.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 0);
        link.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 10);
        link.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        link.setCreatedAt(LocalDateTime.now());
        link.setUpdatedAt(LocalDateTime.now());

        MenuItemAddon savedLink = menuItemAddonRepository.save(link);
        log.info("Add-on linked successfully with ID: {}", savedLink.getId());

        return convertToResponse(savedLink);
    }

    @Override
    public MenuItemAddonResponse updateMenuItemAddon(Long id, MenuItemAddonRequest request) {
        log.info("Updating menu item add-on link with ID: {}", id);

        MenuItemAddon existingLink = menuItemAddonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item add-on link not found with ID: " + id));

        existingLink.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
        existingLink.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 0);
        existingLink.setMaxQuantity(request.getMaxQuantity() != null ? request.getMaxQuantity() : 10);
        existingLink.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        existingLink.setUpdatedAt(LocalDateTime.now());

        MenuItemAddon updatedLink = menuItemAddonRepository.save(existingLink);
        log.info("Menu item add-on link updated successfully with ID: {}", updatedLink.getId());

        return convertToResponse(updatedLink);
    }

    @Override
    public List<MenuItemAddonResponse> getAddonsByMenuItemId(Long menuItemId) {
        log.info("Fetching add-ons for menu item ID: {}", menuItemId);

        List<MenuItemAddon> links = menuItemAddonRepository.findByMenuItemId(menuItemId);

        return links.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public void unlinkAddonFromMenuItem(Long id) {
        log.info("Unlinking add-on with ID: {}", id);

        MenuItemAddon link = menuItemAddonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item add-on link not found with ID: " + id));

        Long menuItemId = link.getMenuItemId();
        menuItemAddonRepository.deleteById(id);
        log.info("Add-on unlinked successfully with ID: {}", id);

        messagingTemplate.convertAndSend(TOPIC_PREFIX + menuItemId + "/menu-item-addons", link);
    }

    @Override
    public void deleteByMenuItemId(Long menuItemId) {
        log.info("Deleting all add-on links for menu item ID: {}", menuItemId);
        menuItemAddonRepository.deleteByMenuItemId(menuItemId);
        log.info("All add-on links deleted for menu item ID: {}", menuItemId);
    }

    private MenuItemAddonResponse convertToResponse(MenuItemAddon link) {
        MenuItemAddonResponse response = new MenuItemAddonResponse();
        response.setId(link.getId());
        response.setMenuItemId(link.getMenuItemId());
        response.setAddonId(link.getAddonId());
        response.setIsRequired(link.getIsRequired());
        response.setMinQuantity(link.getMinQuantity());
        response.setMaxQuantity(link.getMaxQuantity());
        response.setDisplayOrder(link.getDisplayOrder());
        response.setCreatedAt(link.getCreatedAt());
        response.setUpdatedAt(link.getUpdatedAt());

        try {
            MenuAddon addon = menuAddonRepository.findById(link.getAddonId()).orElse(null);
            if (addon != null) {
                response.setAddonName(addon.getName());
                response.setAddonPrice(addon.getPrice());
                response.setAddonImage(addon.getImage());
            }
        } catch (Exception e) {
            log.warn("Could not fetch addon details for id: {}", link.getAddonId());
        }

        return response;
    }
}
