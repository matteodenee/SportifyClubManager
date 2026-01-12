package com.sportify.manager.dao;

import com.sportify.manager.services.NetConversation;
import java.util.List;

public interface ConversationDAO {
    long ensureGlobalConversation(int clubId);
    long createGroup(String groupName, String creatorId, int clubId);

    void addParticipant(long conversationId, String userId);
    boolean isParticipant(long conversationId, String userId);

    List<NetConversation> listUserConversations(String userId, int clubId);
    NetConversation getConversationById(long conversationId, int clubId);
}
