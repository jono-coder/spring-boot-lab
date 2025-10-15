package com.jono.restapi.controller;

import com.jono.core.dto.ClientDto;
import com.jono.core.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.net.URI;
import java.time.Duration;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("client")
public class ClientController {

    private static final Logger LOGGER = getLogger(ClientController.class);

    private final ClientService clientService;

    protected ClientController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<Collection<? extends ClientDto>> findAll(@RequestParam(value = "page", required = false) final Integer page,
                                                                   @RequestParam(value = "size", required = false) final Integer size) {
        final var data = clientService.findAll2(page, size);
        return ResponseEntity.ok()
                             .cacheControl(CacheControl.maxAge(Duration.ofSeconds(10)).mustRevalidate())
                             .body(data);
    }

    @GetMapping("{accountNo}")
    public DeferredResult<ResponseEntity<ClientDto>> findByAccountNo(@PathVariable("accountNo") final String accountNo) {
        final var deferredResult = new DeferredResult<ResponseEntity<ClientDto>>();

        clientService.findByAccountNo(accountNo)
                     .whenComplete((result, error) -> {
                         if (error != null) {
                             deferredResult.setErrorResult(error);
                         } else {
                             deferredResult.setResult(ResponseEntity.ok()
                                                                    .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).mustRevalidate())
                                                                    .body(result));
                         }
                     });

        return deferredResult;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @NotNull @RequestBody final ClientDto clientDto) {
        LOGGER.info("Saving...");

        final var entity = clientService.save(clientDto);

        return ResponseEntity.created(URI.create("client/" + entity.id()))
                             .build();
    }

}
