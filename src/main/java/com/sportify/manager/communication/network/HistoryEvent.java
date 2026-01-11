package com.sportify.manager.communication.network;

import com.sportify.manager.services.NetMessage;
import java.io.Serializable;
import java.util.List;

public record HistoryEvent(long conversationId, List<NetMessage> history) implements Serializable {}
