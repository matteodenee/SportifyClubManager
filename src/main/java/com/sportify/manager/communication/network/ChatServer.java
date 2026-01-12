package com.sportify.manager.communication.network;

import com.lloseng.ocsf.server.ObservableOriginatorServer;

import java.io.IOException;
import java.util.Observer;

public final class ChatServer {
    private final ObservableOriginatorServer server;

    public ChatServer(int port) {
        this.server = new ObservableOriginatorServer(port);
    }

    public void addObserver(Observer o) { server.addObserver(o); }
    public void listen() throws IOException { server.listen(); }
    public void close() throws IOException { server.close(); }
    public void sendToAll(Object event) { server.sendToAllClients(event); }
    public com.lloseng.ocsf.server.ConnectionToClient[] getClientConnections() {
        Thread[] threads = server.getClientConnections();
        if (threads == null || threads.length == 0) {
            return new com.lloseng.ocsf.server.ConnectionToClient[0];
        }
        java.util.List<com.lloseng.ocsf.server.ConnectionToClient> out = new java.util.ArrayList<>();
        for (Thread t : threads) {
            if (t instanceof com.lloseng.ocsf.server.ConnectionToClient c) {
                out.add(c);
            }
        }
        return out.toArray(new com.lloseng.ocsf.server.ConnectionToClient[0]);
    }
}
