package com.cafex.pos.service;

import com.cafex.pos.dto.StatePageResponse;
import com.cafex.pos.dto.StateRequest;
import com.cafex.pos.dto.StateResponse;
import java.util.List;
import java.util.Optional;

public interface StateService {
    StatePageResponse getStatesWithFilters(String name, Boolean isActive, int page, int size);
    List<StateResponse> getAllStates();
    Optional<StateResponse> getStateById(Long id);
    StateResponse createState(StateRequest request);
    StateResponse updateState(Long id, StateRequest request);
    void deleteState(Long id);
}
