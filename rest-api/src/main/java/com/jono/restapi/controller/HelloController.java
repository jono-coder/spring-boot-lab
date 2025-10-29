package com.jono.restapi.controller;

import com.jono.core.service.HelloService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final HelloService helloService;

    protected HelloController(final HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("hello")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String hello() throws NoSuchMethodException {
        return helloService.hello();
    }

}
