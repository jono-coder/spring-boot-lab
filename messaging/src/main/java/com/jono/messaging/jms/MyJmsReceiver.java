package com.jono.messaging.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jono.core.dto.ClientDto;
import org.slf4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MyJmsReceiver {

    private static final Logger LOGGER = getLogger(MyJmsReceiver.class);

    @SuppressWarnings("MethodMayBeStatic")
    @JmsListener(destination = "test-queue")
    public void receive(final String clientDto) throws JsonProcessingException {
        final var mapper = new ObjectMapper();
        final var dto = mapper.readValue(clientDto, ClientDto.class);

        LOGGER.info("Client received: {}", dto);
    }

}
