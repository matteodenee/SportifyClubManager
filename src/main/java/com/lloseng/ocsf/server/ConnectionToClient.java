package com.lloseng.ocsf.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionToClient {
    private final Map<String, Object> info = new HashMap<>();

    public void setInfo(String key, Object value) {
        info.put(key, value);
    }

    public Object getInfo(String key) {
        return info.get(key);
    }

    public void sendToClient(Object message) throws IOException {
        // Stub: no-op for compile-only usage.
    }

    public void close() throws IOException {
        // Stub: no-op for compile-only usage.
    }
}
