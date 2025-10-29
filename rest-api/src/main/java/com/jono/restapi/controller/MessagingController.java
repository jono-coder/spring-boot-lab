package com.jono.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jono.core.service.ClientService;
import com.jono.messaging.jms.MyJmsSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("messaging")
@PreAuthorize("isAuthenticated()")
public class MessagingController {

    private final ClientService service;
    private final MyJmsSender sender;

    protected MessagingController(final ClientService service,
                                  final MyJmsSender sender) {
        this.service = service;
        this.sender = sender;
    }

    @GetMapping("client/{clientId}")
    public void postMessage(@PathVariable("clientId") final Integer clientId) throws JsonProcessingException {
        Objects.requireNonNull(clientId);

        sender.send(service.findById(clientId));
    }

}
