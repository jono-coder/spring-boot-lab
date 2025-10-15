package com.jono.messaging.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jono.core.dto.ClientDto;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MyJmsSender {

    private static final Logger LOGGER = getLogger(MyJmsSender.class);

    private final JmsTemplate jmsTemplate;

    protected MyJmsSender(final JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(final ClientDto client) throws JsonProcessingException {
        LOGGER.info("Jms Message Sender: {}", client);

        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(client);
        jmsTemplate.setTimeToLive(Instant.now().plus(Duration.ofHours(1)).toEpochMilli());
        jmsTemplate.convertAndSend("test-queue", json);
    }

}
