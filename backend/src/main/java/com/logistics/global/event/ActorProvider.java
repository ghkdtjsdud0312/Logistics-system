package com.logistics.global.event;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** 요청 헤더 X-Actor(URL 인코딩)에서 사용자를 읽는다. 없으면 SYSTEM. */
@Component
public class ActorProvider {

    public static final String HEADER = "X-Actor";
    public static final String DEFAULT_ACTOR = "SYSTEM";

    public String current() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String value = attributes.getRequest().getHeader(HEADER);
            if (value != null && !value.isBlank()) {
                return URLDecoder.decode(value, StandardCharsets.UTF_8);
            }
        }
        return DEFAULT_ACTOR;
    }
}
