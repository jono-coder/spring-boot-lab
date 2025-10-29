package com.jono.core.service;

import com.jono.core.IdDescription;
import com.jono.core.config.CacheName;
import com.jono.core.dto.ClientDto;
import com.jono.core.entity.ClientEntity;
import com.jono.core.entity.ClientStatusEntity;
import com.jono.core.repository.ClientRepository;
import com.jono.core.transformer.ClientTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceWithCacheTest {

    @Mock
    private ClientRepository repository;
    @Mock
    private ClientTransformer transformer;

    private ClientService clientService;
    private ClientDto dto;

    @BeforeEach
    void setup() {
        final var cacheManager = new ConcurrentMapCacheManager(CacheName.CLIENT.getCacheName());
        clientService = new ClientService(null, cacheManager, repository, transformer, null);

        final var client = new ClientEntity("123", "Test Client", new ClientStatusEntity("blah"));
        when(repository.findByAccountNo("123")).thenReturn(Optional.of(client));
        dto = new ClientDto(client.accountNo(), client.name(), IdDescription.from(client.status()));
        when(transformer.toDto(any())).thenReturn(dto);
    }

    @Test
    void cacheHit() {
        // --- First call: cache miss → calls repository
        final var first = clientService.findByAccountNo("123").join();
        assertEquals(dto, first);
        verify(repository, times(1)).findByAccountNo("123");

        // --- Second call: cache hit → repository NOT called again
        final var second = clientService.findByAccountNo("123").join();
        assertEquals(dto, second);
        verify(repository, times(1)).findByAccountNo("123"); // still only 1 call
    }

}
