package com.sportify.manager.communication.network;

import java.io.Serializable;

public record JoinConversationRequest(long conversationId) implements Serializable {}
