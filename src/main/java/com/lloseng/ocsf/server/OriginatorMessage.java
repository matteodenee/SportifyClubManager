package com.lloseng.ocsf.server;

public class OriginatorMessage {
    private final ConnectionToClient originator;
    private final Object message;

    public OriginatorMessage(ConnectionToClient originator, Object message) {
        this.originator = originator;
        this.message = message;
    }

    public ConnectionToClient getOriginator() {
        return originator;
    }

    public Object getMessage() {
        return message;
    }
}
