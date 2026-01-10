package communication.network;

import java.io.Serializable;

public record NewMessageEvent(NetMessage message) implements Serializable {}
