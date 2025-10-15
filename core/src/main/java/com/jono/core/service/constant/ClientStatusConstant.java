package com.jono.core.service.constant;

import com.jono.core.entity.ClientStatusEntity;

import java.util.List;

public interface ClientStatusConstant {

    @Id(2)
    ClientStatusEntity active();

    @Id(4)
    ClientStatusEntity discon();

    @Id(1)
    ClientStatusEntity pending();

    @Id(3)
    ClientStatusEntity suspended();

    @Id(1)
    @Id(2)
    @Id(3)
    List<ClientStatusEntity> notDiscon();

    default List<ClientStatusEntity> all() {
        return List.of(active(), discon(), pending(), suspended());
    }

}
