package com.jono.core.service;

import com.jono.core.config.CacheName;
import com.jono.core.dto.ClientDto;
import com.jono.core.entity.ClientEntity;
import com.jono.core.repository.ClientRepository;
import com.jono.core.transformer.ClientTransformer;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("Since15")
@Service
public class ClientService {

    private static final Logger LOGGER = getLogger(ClientService.class);

    public static final ScopedValue<Integer> MY_ID = ScopedValue.newInstance();

    private final TransactionTemplate transactionTemplate;
    private final CacheManager cacheManager;
    private final ClientRepository repository;
    private final ClientTransformer transformer;
    private final ObjectProvider<? extends Worker> workerFactory;

    private volatile Cache cache;

    protected ClientService(final TransactionTemplate transactionTemplate,
                            final CacheManager cacheManager,
                            final ClientRepository repository,
                            final ClientTransformer transformer,
                            final ObjectProvider<? extends Worker> workerFactory) {
        this.transactionTemplate = transactionTemplate;
        this.cacheManager = cacheManager;
        this.repository = repository;
        this.transformer = transformer;
        this.workerFactory = workerFactory;
    }

    @Transactional(readOnly = true)
    public ClientDto findById(final int id) {
        return transformer.toDto(repository.findById(id).orElseThrow());
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<ClientDto> findByAccountNo(final String accountNo) {
        final var result = getCache().get(accountNo, () -> {
            LOGGER.trace("The accountNo '{}' is not in the cache, so retrieving from the database instead...", accountNo);

            final var entity = repository.findByAccountNo(accountNo).orElseThrow();
            return transformer.toDto(entity);
        });

        return CompletableFuture.completedFuture(result);
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Collection<ClientDto>> findAll(final Integer page, final Integer size) {
        final Pageable pageable;
        if (page == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(page, size);
        }

        return CompletableFuture.completedFuture(transformer.toDtos(repository.findAllSortedByAccountNo(pageable).getContent()));
    }

    @Timed("client.service.findAll2.timing")
    public Collection<ClientDto> findAll2(final Integer page, final Integer size) {
        final Pageable pageable;
        if (page == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(page, size);
        }

        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final var subtask1 = scope.fork(() -> ScopedValue.where(MY_ID, 1).call(() -> findThemAll(pageable)));
            final var _1 = scope.fork(() -> ScopedValue.where(MY_ID, 2).call(() -> findThemAll(pageable)));
            final var _2 = scope.fork(() -> {
                doSomething();
                return null;
            });

            scope.join()
                 .throwIfFailed();

            return subtask1.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    @Transactional
    public ClientEntity save(final @Valid @NotNull ClientDto clientDto) {
        final var entity = transformer.toEntity(clientDto);
        return repository.save(entity);
    }

    public List<String> doSomeWork() {
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final var result = Collections.synchronizedList(new ArrayList<String>(100));

            IntStream.range(0, 100).forEach(i ->
                    scope.fork(() -> {
                        ScopedValue.where(MY_ID, i).run(() -> {
                            try {
                                result.add(workerFactory.getObject().withValue("Task_" + i).call());
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return null;
                    }));

            scope.join();
            scope.throwIfFailed();

            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Collection<ClientDto> findThemAll(final Pageable pageable) {
        LOGGER.info("thread={}", Thread.currentThread());
        LOGGER.info("id={}", MY_ID.get());

        return transactionTemplate.execute(_ -> {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            LOGGER.info("TransactionSynchronization.super.afterCommit()");
                        }
                    });

            final var data = repository.findAllSortedByAccountNo(pageable).getContent();
            return transformer.toDtos(data);
        });
    }

    @SuppressWarnings("MethodMayBeStatic")
    private void doSomething() {
        LOGGER.info("thread={}", Thread.currentThread());
    }

    private Cache getCache() {
        var result = cache;
        if (result == null) {
            synchronized (this) {
                result = cache;
                if (result == null) {
                    final var cacheName = CacheName.CLIENT.getCacheName();
                    cache = cacheManager.getCache(cacheName);
                    if (cache == null) {
                        throw new RuntimeException("Cache '" + cacheName + "' not found");
                    }
                    result = cache;
                }
            }
        }
        return result;
    }

}
