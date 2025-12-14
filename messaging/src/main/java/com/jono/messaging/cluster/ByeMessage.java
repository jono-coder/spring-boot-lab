package com.jono.messaging.cluster;

import java.io.Serial;
import java.io.Serializable;

public record ByeMessage(String reason) implements ClusterMessage, Serializable {
    @Serial
    private static final long serialVersionUID = -1957344766427864344L;
}
