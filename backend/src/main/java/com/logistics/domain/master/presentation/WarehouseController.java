package com.logistics.domain.master.presentation;

import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.master.presentation.dto.CodeNameRequest;
import com.logistics.domain.master.presentation.dto.LocationCreateRequest;
import com.logistics.domain.master.presentation.dto.WarehouseTreeResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "창고·위치 관리", description = "창고, 구역, 위치 기준정보 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/warehouses")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> createWarehouse(@Valid @RequestBody CodeNameRequest request) {
        return ApiResponse.success(warehouseService.createWarehouse(request.code(), request.name()).getId());
    }

    @PostMapping("/warehouses/{id}/zones")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> addZone(@PathVariable Long id, @Valid @RequestBody CodeNameRequest request) {
        return ApiResponse.success(warehouseService.addZone(id, request.code(), request.name()).getId());
    }

    @PostMapping("/zones/{id}/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> addLocation(@PathVariable Long id, @Valid @RequestBody LocationCreateRequest request) {
        return ApiResponse.success(warehouseService.addLocation(id, request.code()).getId());
    }

    @GetMapping("/warehouses/tree")
    public ApiResponse<List<WarehouseTreeResponse>> getTree() {
        return ApiResponse.success(warehouseService.getTreeResponses());
    }
}
