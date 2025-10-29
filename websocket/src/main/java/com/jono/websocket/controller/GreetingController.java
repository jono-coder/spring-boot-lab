package com.jono.websocket.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
@PreAuthorize("isAuthenticated()")
public class GreetingController {

    private static final Logger LOGGER = getLogger(GreetingController.class);

    protected GreetingController() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(fluent = false)
    public static class Greeting {
        private String content;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greet(final Greeting greeting) {
        final var content = greeting.getContent();

        LOGGER.info("Received greeting '{}'", content);

        return "Hello : " + content;
    }

    @MessageMapping("/testuser/{data}")
    @SendToUser("/queue/testuser")
    public String testuser(@DestinationVariable("data") final String data, final String text) {
        LOGGER.info("Received testuser '{}' [{}]", text, data);

        return "Hello, " + text + "!";
    }

}
