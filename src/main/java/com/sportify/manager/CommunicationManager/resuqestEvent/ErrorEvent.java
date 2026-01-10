package communication.network;

import java.io.Serializable;

public record ErrorEvent(String message) implements Serializable {}
