package com.sportify.manager.controllers;

import com.sportify.manager.services.NetConversation;
import com.sportify.manager.services.NetMessage;
import java.util.List;

public interface CommunicationListener {
    void onConversations(List<NetConversation> conversations);
    void onHistory(long conversationId, List<NetMessage> history);
    void onNewMessage(NetMessage message);
    void onError(String message);
    void onSystem(String message);
}
