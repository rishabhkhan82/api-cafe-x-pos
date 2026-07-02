package com.cafex.pos.controller;

import com.cafex.pos.dto.PlatformDashboardResponse;
import com.cafex.pos.service.PlatformDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/dashboard")
@RequiredArgsConstructor
public class PlatformDashboardController {

    private final PlatformDashboardService platformDashboardService;

    @GetMapping
    public PlatformDashboardResponse getDashboard() {
        return platformDashboardService.getDashboardSnapshot();
    }
}
