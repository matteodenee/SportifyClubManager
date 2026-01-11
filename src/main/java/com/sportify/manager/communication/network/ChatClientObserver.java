package com.sportify.manager.communication.network;

import javafx.application.Platform;

import java.util.Observable;
import java.util.Observer;

public final class ChatClientObserver implements Observer {

    public interface NetworkEventSink {
        void onNetworkObject(Object msg);
    }

    private final NetworkEventSink sink;

    public ChatClientObserver(NetworkEventSink sink) {
        this.sink = sink;
    }

    @Override
    public void update(Observable o, Object msg) {
        Platform.runLater(() -> sink.onNetworkObject(msg));
    }
}
