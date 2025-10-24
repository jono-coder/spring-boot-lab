package com.jono.batchjobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.jono.core.entity")
@EnableJpaRepositories(basePackages = "com.jono.core.repository")
public class BatchJobsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BatchJobsApplication.class, args);
    }

}
