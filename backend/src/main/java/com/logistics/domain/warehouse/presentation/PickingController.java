package com.logistics.domain.warehouse.presentation;

import com.logistics.domain.warehouse.application.PickingService;
import com.logistics.domain.warehouse.application.WorkTaskQueryService;
import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.domain.warehouse.presentation.dto.PickingCompleteRequest;
import com.logistics.domain.warehouse.presentation.dto.PickingTaskResponse;
import com.logistics.domain.warehouse.presentation.dto.TaskSummaryResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "피킹", description = "피킹 작업 목록·시작·완료 API")
@RestController
@RequestMapping("/api/picking-tasks")
@RequiredArgsConstructor
public class PickingController {

    private final PickingService pickingService;
    private final WorkTaskQueryService queryService;

    @GetMapping
    public ApiResponse<List<PickingTaskResponse>> getList(@RequestParam(required = false) WorkStatus status) {
        return ApiResponse.success(queryService.pickingTasks(status));
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<TaskSummaryResponse> start(@PathVariable Long id) {
        return ApiResponse.success(summary(pickingService.start(id)));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<TaskSummaryResponse> complete(@PathVariable Long id,
                                                     @Valid @RequestBody PickingCompleteRequest request) {
        return ApiResponse.success(summary(pickingService.complete(id, request.pickedQty())));
    }

    private TaskSummaryResponse summary(PickingTask task) {
        return new TaskSummaryResponse(task.getId(), task.getTaskNo(), task.getStatus());
    }
}
