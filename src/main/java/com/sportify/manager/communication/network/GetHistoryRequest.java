package com.sportify.manager.communication.network;

import java.io.Serializable;

public record GetHistoryRequest(long conversationId, int limit) implements Serializable {}
