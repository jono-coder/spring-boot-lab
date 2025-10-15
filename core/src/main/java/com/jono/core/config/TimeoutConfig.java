package com.jono.core.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.QueryTimeoutException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@EnableTransactionManagement
public class TimeoutConfig {

    private static final Logger LOGGER = getLogger(TimeoutConfig.class);

    /**
     * Global JPA transaction manager with default timeout.
     * Individual @Transactional(timeout = ...) can override this.
     */
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final var tm = new JpaTransactionManager(emf);
        tm.setDefaultTimeout(30); // 30 seconds
        tm.setRollbackOnCommitFailure(true);
        return tm;
    }

    /**
     * Aspect to log all query and transaction timeouts centrally.
     */
    @SuppressWarnings("MethodMayBeStatic")
    @Aspect
    @Configuration
    public static class TimeoutLoggingAspect {
        // Catch transaction timeouts
        @AfterThrowing(pointcut = "execution(* com.jono.service..*(..))", throwing = "ex")
        public void logTransactionTimeout(final TransactionTimedOutException ex) {
            LOGGER.warn("Transaction timed out: {}", ex.getMessage());
        }

        // Catch query timeouts
        @AfterThrowing(pointcut = "execution(* com.jono.service..*(..))", throwing = "ex")
        public void logQueryTimeout(final QueryTimeoutException ex) {
            LOGGER.warn("Query timed out: {}", ex.getMessage());
        }
    }

}
