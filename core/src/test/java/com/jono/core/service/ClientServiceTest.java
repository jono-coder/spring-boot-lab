package com.jono.core.service;

import com.jono.core.IdDesc;
import com.jono.core.IdDescription;
import com.jono.core.dto.ClientDto;
import com.jono.core.entity.ClientEntity;
import com.jono.core.entity.ClientStatusEntity;
import com.jono.core.repository.ClientRepository;
import com.jono.core.service.constant.ClientStatusConstant;
import com.jono.core.transformer.ClientTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientStatusConstant clientStatusConstant;
    @Mock
    private ClientTransformer clientTransformer;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @InjectMocks
    private ClientService clientService;

    private ClientEntity client;
    private ClientDto clientDto;
    private ClientStatusEntity status;
    private IdDesc statusDto;

    @BeforeEach
    void setup() {
        status = new ClientStatusEntity("blah");
        statusDto = IdDescription.from(status);
        lenient().when(clientStatusConstant.active()).thenReturn(status);

        client = new ClientEntity("123", "Test Client", clientStatusConstant.active());
        clientDto = new ClientDto(client.accountNo(), client.name(), IdDescription.from(client.status()));
        lenient().when(clientTransformer.toDto(client)).thenReturn(clientDto);

        lenient().when(cacheManager.getCache(anyString())).thenReturn(cache);
        lenient().when(cache.get(anyString(), any(Callable.class)))
                 .thenAnswer(invocation -> {
                     final Callable<?> callable = invocation.getArgument(1);
                     return callable.call(); // call the repository code
                 });
    }

    @Test
    @DisplayName("should return DTO from a/c no lookup as client exists")
    void testFindClientByAccountNo() {
        when(clientRepository.findByAccountNo("123")).thenReturn(Optional.of(client));

        final var result = clientService.findByAccountNo("123").join();

        assertThat(result).isNotNull();
        assertThat(result.accountNo()).isEqualTo(("123"));
        assertThat(result.name()).isEqualTo(("Test Client"));
        assertThat(result.status()).isEqualTo(statusDto);
        verify(clientRepository, times(1)).findByAccountNo("123");
        verifyNoMoreInteractions(clientRepository);
    }

    @Test
    @DisplayName("should throw exception from a/c no lookup as no client exists")
    void testFindClientByAccountNo_notFound() {
        when(clientRepository.findByAccountNo("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findByAccountNo("999"))
                .isExactlyInstanceOf(NoSuchElementException.class);

        verifyNoInteractions(clientTransformer);
    }

    @Test
    @DisplayName("should return DTO from id lookup as client exists")
    void testFindClientById() {
        final var client = new ClientEntity("123", "Test Client", clientStatusConstant.active());
        final var clientDto = new ClientDto(client.accountNo(), client.name(), IdDescription.from(client.status()));

        when(clientTransformer.toDto(client)).thenReturn(clientDto);
        when(clientRepository.findByAccountNo("123")).thenReturn(Optional.of(client));

        final var result = clientService.findByAccountNo("123").join();

        assertThat(result).isNotNull();
        assertThat(result.accountNo()).isEqualTo(("123"));
        assertThat(result.name()).isEqualTo(("Test Client"));
        assertThat(result.status()).isEqualTo(IdDescription.from(status));
        verify(clientRepository, times(1)).findByAccountNo("123");
    }

    @Test
    @DisplayName("should throw exception from id lookup as no client exists")
    void testFindClientById_notFound() {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(999))
                .isExactlyInstanceOf(NoSuchElementException.class);

        verifyNoInteractions(clientTransformer);
    }

    @Test
    @DisplayName("should return a client")
    void testFindAllClients() {
        when(transactionTemplate.execute(any())).thenReturn(List.of(clientDto));
        final var result = clientService.findAll2(null, null);

        assertThat(result)
                .isNotNull()
                .hasSize(1);
        assertThat(result).containsExactly(clientDto);
    }

}
