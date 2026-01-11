package com.lloseng.ocsf.client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ObservableClient extends Observable {
    private final String host;
    private final int port;
    private boolean connected;

    public ObservableClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = false;
    }

    public void addObserver(Observer o) {
        super.addObserver(o);
    }

    public void openConnection() throws IOException {
        connected = true;
    }

    public void closeConnection() throws IOException {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendToServer(Object obj) throws IOException {
        // Stub: no-op for compile-only usage.
    }
}
