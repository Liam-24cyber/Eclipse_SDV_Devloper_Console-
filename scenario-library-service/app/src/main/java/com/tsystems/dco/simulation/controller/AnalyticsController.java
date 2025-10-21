/*
 *   ========================================================================
 *  SDV Developer Console
 *
 *   Copyright (C) 2022 - 2023 T-Systems International GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   SPDX-License-Identifier: Apache-2.0
 *
 *   ========================================================================
 */

package com.tsystems.dco.simulation.controller;

import com.tsystems.dco.simulation.dto.AnalyticsDashboardDTO;
import com.tsystems.dco.simulation.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Analytics Dashboard
 */
@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics dashboard endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(
        summary = "Get Analytics Dashboard",
        description = "Retrieve comprehensive analytics dashboard with metrics, trends, and statistics"
    )
    public ResponseEntity<AnalyticsDashboardDTO> getDashboard(
        @Parameter(description = "Number of days to look back (default: 30)")
        @RequestParam(required = false, defaultValue = "30") Integer daysBack
    ) {
        log.info("Fetching analytics dashboard for last {} days", daysBack);
        AnalyticsDashboardDTO dashboard = analyticsService.getDashboardAnalytics(daysBack);
        return ResponseEntity.ok(dashboard);
    }
}
