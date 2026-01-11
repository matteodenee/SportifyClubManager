package com.sportify.manager.communication.network;

import com.sportify.manager.services.NetConversation;
import java.io.Serializable;
import java.util.List;

public record ConversationListEvent(List<NetConversation> conversations) implements Serializable {}
