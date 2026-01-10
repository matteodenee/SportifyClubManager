package communication.network;

import java.io.Serializable;
import java.util.List;

public record ConversationListEvent(List<NetConversation> conversations) implements Serializable {}
