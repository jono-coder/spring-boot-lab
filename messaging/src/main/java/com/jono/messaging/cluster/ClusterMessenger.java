package com.jono.messaging.cluster;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ClusterMessenger implements Receiver {

    private static final Logger LOGGER = getLogger(ClusterMessenger.class);

    private JChannel channel;

    @PostConstruct
    public void init() throws Exception {
        channel = new JChannel();
        channel.receiver(this);
        channel.connect("MyAppCluster");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
        LOGGER.info("👋 LAN Notifier stopped");
    }

    @Override
    public void receive(final Message msg) {
        final var obj = msg.getObject();
        if (obj instanceof final ClusterMessage clusterMsg) {
            // Exhaustive switch over sealed types
            switch (clusterMsg) {
                case final HelloMessage hello -> LOGGER.info("👋 Hello from '{}'", hello.name());
                case final ByeMessage bye -> LOGGER.info("👋 Bye: {}", bye.reason());
                case final DataMessage data -> LOGGER.info("📦 Data: {}", data.payload());
            }
        } else {
            LOGGER.info("⚠️ Unknown message type: {}", obj);
        }
    }

    // Optional overrides:
    @Override
    public void viewAccepted(final View newView) {
        LOGGER.info("👥 New cluster view: {}", newView);
    }

    public void broadcast(final String message) {
        try {
            channel.send(null, message);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
