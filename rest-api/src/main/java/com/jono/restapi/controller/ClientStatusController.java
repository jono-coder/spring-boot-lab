package com.jono.restapi.controller;

import com.jono.core.IdDesc;
import com.jono.core.IdDescription;
import com.jono.core.service.constant.ClientStatusConstant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("clientStatus")
public class ClientStatusController {

    private final ClientStatusConstant clientStatusConstant;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected ClientStatusController(final ClientStatusConstant clientStatusConstant) {
        this.clientStatusConstant = clientStatusConstant;
    }

    @GetMapping("active")
    @Transactional(readOnly = true)
    public IdDesc findActive() {
        return IdDescription.from(clientStatusConstant.active());
    }

    @GetMapping("discon")
    @Transactional(readOnly = true)
    public IdDesc findDiscon() {
        return IdDescription.from(clientStatusConstant.discon());
    }

    @GetMapping("notDiscon")
    @Transactional(readOnly = true)
    public List<? extends IdDesc> findNotDiscon() {
        return clientStatusConstant.notDiscon().stream()
                                   .map(IdDescription::from)
                                   .toList();
    }

    @GetMapping("all")
    @Transactional(readOnly = true)
    public List<? extends IdDesc> findAll() {
        return IdDescription.from(clientStatusConstant.all());
    }

}
