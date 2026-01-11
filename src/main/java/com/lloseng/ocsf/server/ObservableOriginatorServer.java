package com.lloseng.ocsf.server;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ObservableOriginatorServer extends Observable {
    private final int port;

    public ObservableOriginatorServer(int port) {
        this.port = port;
    }

    public void addObserver(Observer o) {
        super.addObserver(o);
    }

    public void listen() throws IOException {
        // Stub: no-op for compile-only usage.
    }

    public void close() throws IOException {
        // Stub: no-op for compile-only usage.
    }

    public void sendToAllClients(Object event) {
        // Stub: no-op for compile-only usage.
    }
}
