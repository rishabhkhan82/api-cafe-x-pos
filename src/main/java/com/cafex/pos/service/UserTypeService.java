package com.cafex.pos.service;

import com.cafex.pos.dto.UserTypePageResponse;
import com.cafex.pos.dto.UserTypeRequest;
import com.cafex.pos.dto.UserTypeResponse;
import java.util.List;
import java.util.Optional;

public interface UserTypeService {
    UserTypePageResponse getUserTypesWithFilters(String name, Boolean isActive, int page, int size);
    List<UserTypeResponse> getAllUserTypes();
    Optional<UserTypeResponse> getUserTypeById(Long id);
    UserTypeResponse createUserType(UserTypeRequest request);
    UserTypeResponse updateUserType(Long id, UserTypeRequest request);
    void deleteUserType(Long id);
}
