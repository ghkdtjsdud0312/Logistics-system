package com.logistics.global.sse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "실시간 관제", description = "물류 이벤트 SSE 스트림")
@RestController
@RequiredArgsConstructor
public class LogisticsEventController {

    private final LogisticsEventBroadcaster broadcaster;

    @GetMapping(value = "/api/events/logistics", produces = "text/event-stream")
    public SseEmitter subscribe() {
        return broadcaster.subscribe();
    }
}
