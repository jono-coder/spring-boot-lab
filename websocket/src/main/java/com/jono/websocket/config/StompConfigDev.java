package com.jono.websocket.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Profile("dev")
public class StompConfigDev implements WebSocketMessageBrokerConfigurer {

    @Value("${activemq.port:61614}")  // 0 means auto-assign
    private int activemqPort;

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue", "/user") // Relay to broker for pub/sub topics
              .setRelayHost("localhost")
              .setRelayPort(activemqPort); // Matches the STOMP connector port
        config.setApplicationDestinationPrefixes("/app") // Prefix for app-handled messages
              .setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // STOMP endpoint for clients
                .setAllowedOriginPatterns("*")
                .addInterceptors(new CustomUserHandshakeInterceptor());
    }

    // Customizes the autoconfigured embedded broker to expose STOMP over TCP
    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService broker() throws Exception {
        final var broker = new BrokerService();
        final var connectorUri = "stomp://localhost:" + activemqPort + "?transport.transformer=jms";
        System.out.println("Connecting to broker at " + connectorUri);
        broker.addConnector(connectorUri); // Expose STOMP
        broker.setPersistent(false); // Non-persistent for DEV
        broker.setUseShutdownHook(true);

        final var managementContext = new ManagementContext();
        managementContext.setCreateConnector(true);
        broker.setManagementContext(managementContext);

        return broker;
    }

}
