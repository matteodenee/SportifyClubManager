package com.sportify.manager.CommunicationManager;

public class Conversation {
    private final long id;
    private final String name;
    private final ConversationType type;

    public Conversation(long id, String name, ConversationType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public ConversationType getType() { return type; }

    @Override
    public String toString() {
        return (type == ConversationType.GLOBAL ? "🌐 " : "👥 ") + name;
    }
}
