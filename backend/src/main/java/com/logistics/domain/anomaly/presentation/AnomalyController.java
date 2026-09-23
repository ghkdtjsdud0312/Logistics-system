package com.logistics.domain.anomaly.presentation;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import com.logistics.domain.anomaly.presentation.dto.AnomalyResponse;
import com.logistics.domain.anomaly.presentation.dto.AnomalyStatusChangeRequest;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이상 탐지", description = "배차 이상 목록 조회/상태 변경 API")
@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyService anomalyService;

    @GetMapping
    public ApiResponse<List<AnomalyResponse>> getList(@RequestParam(required = false) AnomalyStatus status) {
        List<AnomalyResponse> responses = anomalyService.getAnomalies(status).stream()
                .map(AnomalyResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AnomalyResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody AnomalyStatusChangeRequest request) {
        return ApiResponse.success(AnomalyResponse.from(anomalyService.changeStatus(id, request.status())));
    }
}
