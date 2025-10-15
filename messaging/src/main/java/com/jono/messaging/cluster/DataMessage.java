package com.jono.messaging.cluster;

import java.io.Serial;
import java.io.Serializable;

public final class DataMessage implements ClusterMessage, Serializable {
    @Serial
    private static final long serialVersionUID = 6570075437968445597L;

    private final int[] payload;

    public DataMessage(final int[] payload) {
        this.payload = payload.clone();
    }

    public int[] payload() {
        return payload.clone();
    }
}
