package com.cafex.pos.service;

import com.cafex.pos.dto.NavigationMenuTypePageResponse;
import com.cafex.pos.dto.NavigationMenuTypeRequest;
import com.cafex.pos.dto.NavigationMenuTypeResponse;
import java.util.List;
import java.util.Optional;

public interface NavigationMenuTypeService {
    NavigationMenuTypePageResponse getNavigationMenuTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<NavigationMenuTypeResponse> getAllNavigationMenuTypes();
    Optional<NavigationMenuTypeResponse> getNavigationMenuTypeById(Long id);
    NavigationMenuTypeResponse createNavigationMenuType(NavigationMenuTypeRequest request);
    NavigationMenuTypeResponse updateNavigationMenuType(Long id, NavigationMenuTypeRequest request);
    void deleteNavigationMenuType(Long id);
}
