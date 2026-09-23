package com.logistics.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** 시간 기반 로직(정체 배차 탐지 등)을 테스트 가능하게 만들기 위한 Clock 빈 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
