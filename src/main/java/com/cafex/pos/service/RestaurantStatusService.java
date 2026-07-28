package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantStatusPageResponse;
import com.cafex.pos.dto.RestaurantStatusRequest;
import com.cafex.pos.dto.RestaurantStatusResponse;
import java.util.List;
import java.util.Optional;

public interface RestaurantStatusService {
    RestaurantStatusPageResponse getRestaurantStatusesWithFilters(String name, Boolean isActive, int page, int size);
    List<RestaurantStatusResponse> getAllRestaurantStatuses();
    Optional<RestaurantStatusResponse> getRestaurantStatusById(Long id);
    RestaurantStatusResponse createRestaurantStatus(RestaurantStatusRequest request);
    RestaurantStatusResponse updateRestaurantStatus(Long id, RestaurantStatusRequest request);
    void deleteRestaurantStatus(Long id);
}
