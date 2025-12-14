package com.jono.messaging.cluster;

import java.io.Serial;
import java.io.Serializable;

public final class HelloMessage implements ClusterMessage, Serializable {

    @Serial
    private static final long serialVersionUID = -2131516567400984488L;

    private final String name;

    public HelloMessage(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

}
