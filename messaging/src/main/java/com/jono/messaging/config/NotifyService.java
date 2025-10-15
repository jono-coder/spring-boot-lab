package com.jono.messaging.config;

import com.jono.messaging.cluster.ClusterMessenger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class NotifyService {

    private final ClusterMessenger messenger;

    protected NotifyService(final ClusterMessenger messenger) {
        this.messenger = messenger;
    }

    public void notify(final String msg) {
        messenger.broadcast(msg);
    }

}
