package com.jono.core.service;

import com.jono.core.service.constant.ClientStatusConstant;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Worker implements Callable<String> {

    private static final Logger LOGGER = getLogger(Worker.class);

    private final ClientStatusConstant clientStatusConstant;
    private String value;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected Worker(final ClientStatusConstant clientStatusConstant) {
        this.clientStatusConstant = clientStatusConstant;
    }

    public Worker withValue(final String value) {
        this.value = value == null ? "<null>" : value;
        return this;
    }

    @Transactional(readOnly = true)
    @Override
    public String call() throws Exception {
        Thread.sleep(1_000);
        LOGGER.info("id={}", ClientService.MY_ID.get());
        //noinspection ResultOfMethodCallIgnored
        clientStatusConstant.active().description();
        return value + " :: done";
    }

}
