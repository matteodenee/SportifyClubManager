package com.sportify.manager.CommunicationManager;

import java.util.List;

public enum ConversationType {
    GLOBAL,
    GROUP;

    public static interface MessageDAO {
        void save(NetMessage msg);
        List<NetMessage> getHistory(long conversationId, int limit);
    }
}
