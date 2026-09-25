package com.logistics.domain.warehouse.presentation;

import com.logistics.domain.warehouse.application.PackingService;
import com.logistics.domain.warehouse.application.WorkTaskQueryService;
import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.domain.warehouse.presentation.dto.PackingCompleteRequest;
import com.logistics.domain.warehouse.presentation.dto.PackingTaskResponse;
import com.logistics.domain.warehouse.presentation.dto.TaskSummaryResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "포장", description = "포장 작업 목록·완료 API")
@RestController
@RequestMapping("/api/packing-tasks")
@RequiredArgsConstructor
public class PackingController {

    private final PackingService packingService;
    private final WorkTaskQueryService queryService;

    @GetMapping
    public ApiResponse<List<PackingTaskResponse>> getList(@RequestParam(required = false) WorkStatus status) {
        return ApiResponse.success(queryService.packingTasks(status));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<TaskSummaryResponse> complete(@PathVariable Long id,
                                                     @Valid @RequestBody PackingCompleteRequest request) {
        PackingTask task = packingService.complete(id, request.boxCode());
        return ApiResponse.success(new TaskSummaryResponse(task.getId(), task.getTaskNo(), task.getStatus()));
    }
}
