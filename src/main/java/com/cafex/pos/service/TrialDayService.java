package com.cafex.pos.service;

import com.cafex.pos.dto.TrialDayPageResponse;
import com.cafex.pos.dto.TrialDayRequest;
import com.cafex.pos.dto.TrialDayResponse;
import java.util.List;
import java.util.Optional;

public interface TrialDayService {
    TrialDayPageResponse getTrialDaysWithFilters(String name, Boolean isActive, int page, int size);
    List<TrialDayResponse> getAllTrialDays();
    Optional<TrialDayResponse> getTrialDayById(Long id);
    TrialDayResponse createTrialDay(TrialDayRequest request);
    TrialDayResponse updateTrialDay(Long id, TrialDayRequest request);
    void deleteTrialDay(Long id);
}
