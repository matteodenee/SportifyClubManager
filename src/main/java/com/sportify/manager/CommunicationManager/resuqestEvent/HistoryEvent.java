package communication.network;

import java.io.Serializable;
import java.util.List;

public record HistoryEvent(long conversationId, List<NetMessage> history) implements Serializable {}
