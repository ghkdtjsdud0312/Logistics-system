package com.logistics.domain.vehicle.presentation;

import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.domain.vehicle.presentation.dto.VehicleCreateRequest;
import com.logistics.domain.vehicle.presentation.dto.VehicleResponse;
import com.logistics.domain.vehicle.presentation.dto.VehicleStatusRequest;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "차량 관리", description = "차량 등록/조회 API")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VehicleResponse> create(@Valid @RequestBody VehicleCreateRequest request) {
        return ApiResponse.success(VehicleResponse.from(vehicleService.create(request.toEntity())));
    }

    @GetMapping
    public ApiResponse<List<VehicleResponse>> getList(@RequestParam(required = false) VehicleStatus status) {
        List<VehicleResponse> responses = vehicleService.getVehicles(status).stream()
                .map(VehicleResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleResponse> getOne(@PathVariable Long id) {
        return ApiResponse.success(VehicleResponse.from(vehicleService.getVehicle(id)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<VehicleResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody VehicleStatusRequest request) {
        return ApiResponse.success(VehicleResponse.from(vehicleService.changeStatus(id, request.status())));
    }
}
