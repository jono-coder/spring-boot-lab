package com.jono.core.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class TransactionConcurrencyLimiter {

    private final Bulkhead bulkhead;

    public TransactionConcurrencyLimiter(final BulkheadRegistry registry) {
        bulkhead = registry.bulkhead("globalTxLimiter");
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object limitTransactions(final ProceedingJoinPoint pjp) throws Throwable {
        return Bulkhead.decorateCheckedSupplier(bulkhead, pjp::proceed).get();
    }

}
