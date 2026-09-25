package com.logistics.global.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StatusChangedEventTest {

    private final ActorProvider actorProvider = new ActorProvider();

    @AfterEach
    void clear() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("X-Actor 헤더가 없으면 SYSTEM, 있으면 URL 디코딩한 값을 쓴다")
    void actor_fromHeader() {
        assertThat(actorProvider.current()).isEqualTo("SYSTEM");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Actor", URLEncoder.encode("홍길동", StandardCharsets.UTF_8));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThat(actorProvider.current()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("publish는 상태 변경 이벤트를 채워서 스프링 이벤트로 발행한다")
    void publish_buildsEvent() {
        ApplicationEventPublisher springPublisher = mock(ApplicationEventPublisher.class);
        Clock clock = Clock.fixed(Instant.parse("2026-09-25T00:00:00Z"), ZoneOffset.UTC);
        StatusChangedEventPublisher publisher = new StatusChangedEventPublisher(springPublisher, actorProvider, clock);

        publisher.publish("ORDER", 1L, "ORD-001", 1L, "PICK_COMPLETE", "PICKING", "PICKED");

        ArgumentCaptor<StatusChangedEvent> captor = ArgumentCaptor.forClass(StatusChangedEvent.class);
        verify(springPublisher).publishEvent(captor.capture());
        StatusChangedEvent event = captor.getValue();
        assertThat(event.targetNo()).isEqualTo("ORD-001");
        assertThat(event.toStatus()).isEqualTo("PICKED");
        assertThat(event.actor()).isEqualTo("SYSTEM");
        assertThat(event.occurredAt()).isEqualTo(Instant.parse("2026-09-25T00:00:00Z"));
    }

    @Test
    @DisplayName("Kafka 발행이 실패해도 예외를 던지지 않는다")
    @SuppressWarnings("unchecked")
    void relay_swallowsKafkaFailure() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenThrow(new IllegalStateException("broker down"));
        StatusChangedEvent event = new StatusChangedEvent("e1", "ORDER", 1L, "ORD-001", 1L,
                "CREATE", null, "RECEIVED", "SYSTEM", Instant.now());

        assertThatCode(() -> new StatusChangedKafkaRelay(kafkaTemplate).relay(event)).doesNotThrowAnyException();
        verify(kafkaTemplate).send(eq(EventTopics.STATUS_CHANGED), eq("ORDER:1"), eq(event));
    }
}
