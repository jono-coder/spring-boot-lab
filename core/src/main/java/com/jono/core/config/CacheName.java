package com.jono.core.config;

public enum CacheName {

    CLIENT("client-lookups"),
    ENTITY_CONSTANTS("entity-constants");

    private final String cacheName;

    CacheName(final String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

}
