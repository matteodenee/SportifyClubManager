package com.sportify.manager.dao;

import com.sportify.manager.services.NetConversation;
import java.util.List;

public interface ConversationDAO {
    long ensureGlobalConversation();
    long createGroup(String groupName, String creatorId);

    void addParticipant(long conversationId, String userId);
    boolean isParticipant(long conversationId, String userId);

    List<NetConversation> listUserConversations(String userId);
    NetConversation getConversationById(long conversationId);
}
