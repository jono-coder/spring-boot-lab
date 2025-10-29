package com.jono.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.jono.core",
                "com.jono.coresecurity",
                "com.jono.graphql",
                "com.jono.messaging",
                "com.jono.restapi",
                "com.jono.websocket"
        }
)
@EntityScan(basePackages = "com.jono.core.entity")
@EnableJpaRepositories(basePackages = "com.jono.core.repository")
public class SpringBootLabApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootLabApplication.class, args);
    }

}
