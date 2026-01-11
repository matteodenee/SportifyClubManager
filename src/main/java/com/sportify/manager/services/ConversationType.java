package com.sportify.manager.services;

import java.util.List;

public enum ConversationType {
    GLOBAL,
    GROUP;

    public interface MessageDAO {
        void save(NetMessage msg);
        List<NetMessage> getHistory(long conversationId, int limit);
    }
}
