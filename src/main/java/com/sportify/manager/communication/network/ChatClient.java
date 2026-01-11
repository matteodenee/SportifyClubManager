package com.sportify.manager.communication.network;

import com.lloseng.ocsf.client.ObservableClient;
import java.io.IOException;
import java.util.Observer;

public final class ChatClient {
    private final ObservableClient client;

    public ChatClient(String host, int port) {
        this.client = new ObservableClient(host, port);
    }

    public void addObserver(Observer o) { client.addObserver(o); }
    public void open() throws IOException { client.openConnection(); }
    public void close() throws IOException { client.closeConnection(); }
    public boolean isConnected() { return client.isConnected(); }
    public void send(Object obj) throws IOException { client.sendToServer(obj); }
}
