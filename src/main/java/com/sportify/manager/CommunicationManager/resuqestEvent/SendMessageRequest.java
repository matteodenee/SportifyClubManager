package communication.network;

import java.io.Serializable;

public record SendMessageRequest(long conversationId, String content) implements Serializable {}
