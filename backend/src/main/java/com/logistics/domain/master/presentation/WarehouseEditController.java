package com.logistics.domain.master.presentation;

import com.logistics.domain.master.application.WarehouseEditService;
import com.logistics.domain.master.presentation.dto.NameRequest;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "창고·위치 관리", description = "창고, 구역, 위치 수정/삭제 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WarehouseEditController {

    private final WarehouseEditService editService;

    @PutMapping("/warehouses/{id}")
    public ApiResponse<Long> renameWarehouse(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
        return ApiResponse.success(editService.renameWarehouse(id, request.name()));
    }

    @DeleteMapping("/warehouses/{id}")
    public ApiResponse<Void> deleteWarehouse(@PathVariable Long id) {
        editService.deleteWarehouse(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/zones/{id}")
    public ApiResponse<Long> renameZone(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
        return ApiResponse.success(editService.renameZone(id, request.name()));
    }

    @DeleteMapping("/zones/{id}")
    public ApiResponse<Void> deleteZone(@PathVariable Long id) {
        editService.deleteZone(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/locations/{id}")
    public ApiResponse<Void> deleteLocation(@PathVariable Long id) {
        editService.deleteLocation(id);
        return ApiResponse.success(null);
    }
}
