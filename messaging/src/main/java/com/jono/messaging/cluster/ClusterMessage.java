package com.jono.messaging.cluster;

public sealed interface ClusterMessage permits HelloMessage, ByeMessage, DataMessage {}
