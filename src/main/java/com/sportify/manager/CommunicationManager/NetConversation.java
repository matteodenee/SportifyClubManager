package com.sportify.manager.CommunicationManager;

import java.io.Serializable;

public record NetConversation(long id, String name, NetConversationType type) implements Serializable {
    @Override public String toString() {
        return (type == NetConversationType.GLOBAL ? "🌐 " : "👥 ") + name;
    }
}
