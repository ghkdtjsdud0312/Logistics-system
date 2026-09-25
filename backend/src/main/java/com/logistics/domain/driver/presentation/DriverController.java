package com.logistics.domain.driver.presentation;

import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.driver.presentation.dto.DriverCreateRequest;
import com.logistics.domain.driver.presentation.dto.DriverResponse;
import com.logistics.domain.driver.presentation.dto.DriverStatusRequest;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "기사 관리", description = "기사 등록/조회 API")
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DriverResponse> create(@Valid @RequestBody DriverCreateRequest request) {
        return ApiResponse.success(DriverResponse.from(driverService.create(request.toEntity())));
    }

    @GetMapping
    public ApiResponse<List<DriverResponse>> getList(@RequestParam(required = false) DriverStatus status) {
        List<DriverResponse> responses = driverService.getDrivers(status).stream()
                .map(DriverResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}")
    public ApiResponse<DriverResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(DriverResponse.from(driverService.getDriver(id)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<DriverResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody DriverStatusRequest request) {
        return ApiResponse.success(DriverResponse.from(driverService.changeStatus(id, request.status())));
    }
}
