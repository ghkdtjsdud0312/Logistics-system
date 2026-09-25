package com.logistics.domain.audit.presentation;

import com.logistics.domain.audit.application.AuditQueryService;
import com.logistics.domain.audit.domain.AuditSearchCriteria;
import com.logistics.domain.audit.presentation.dto.AuditLogResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "감사로그", description = "상태 변경 이력 검색 API")
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private static final int DEFAULT_LIMIT = 100;

    private final AuditQueryService queryService;

    @GetMapping
    public ApiResponse<List<AuditLogResponse>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String action) {
        return ApiResponse.success(queryService.search(
                new AuditSearchCriteria(from, to, actor, target, action, DEFAULT_LIMIT)));
    }
}
