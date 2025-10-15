package com.jono.core.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final ApplicationEventPublisher publisher;

    protected HelloService(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public String hello() throws NoSuchMethodException {
        publisher.publishEvent(new MethodCalledEvent(getClass().getMethod("hello")));

        return "Hello from Spring Boot!";
    }

}
