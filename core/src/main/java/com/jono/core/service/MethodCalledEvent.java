package com.jono.core.service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class MethodCalledEvent {

    private final Method method;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public MethodCalledEvent(final Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
