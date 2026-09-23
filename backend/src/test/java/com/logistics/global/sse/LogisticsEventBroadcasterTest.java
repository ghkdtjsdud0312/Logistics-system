package com.logistics.global.sse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogisticsEventBroadcasterTest {

    private final LogisticsEventBroadcaster broadcaster = new LogisticsEventBroadcaster();

    @SuppressWarnings("unchecked")
    private List<SseEmitter> emitters() throws Exception {
        Field field = LogisticsEventBroadcaster.class.getDeclaredField("emitters");
        field.setAccessible(true);
        return (List<SseEmitter>) field.get(broadcaster);
    }

    @Test
    @DisplayName("구독하면 emitter 목록에 추가된다")
    void subscribe_addsEmitter() throws Exception {
        broadcaster.subscribe();
        assertThat(emitters()).hasSize(1);
    }

    @Test
    @DisplayName("여러 클라이언트가 구독하면 모두 목록에 유지된다")
    void subscribe_multipleClients() throws Exception {
        broadcaster.subscribe();
        broadcaster.subscribe();

        assertThat(emitters()).hasSize(2);
    }
}
