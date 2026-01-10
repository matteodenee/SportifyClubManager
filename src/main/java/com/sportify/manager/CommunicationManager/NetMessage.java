package com.sportify.manager.CommunicationManager;


import java.io.Serializable;
import java.time.Instant;

public record NetMessage(long conversationId, String senderId, String content, Instant sentAt)
        implements Serializable {}
