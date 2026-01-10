package com.sportify.manager.CommunicationManager;

import communication.network.NetMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresMessageDAO implements ConversationType.MessageDAO {

    private final Connection connection;

    public PostgresMessageDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(NetMessage msg) {
        if (msg.content() == null || msg.content().isBlank()) {
            throw new IllegalArgumentException("Message vide interdit.");
        }

        try {
            String sql = "INSERT INTO message(conversation_id,sender_id,content,sent_at) VALUES (?,?,?,?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, msg.conversationId());
                ps.setString(2, msg.senderId());
                ps.setString(3, msg.content());
                ps.setTimestamp(4, Timestamp.from(msg.sentAt()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erreur save message: " + e.getMessage());
        }
    }

    @Override
    public List<NetMessage> getHistory(long conversationId, int limit) {
        int lim = Math.max(1, Math.min(limit, 300));
        List<NetMessage> out = new ArrayList<>();

        try {
            String sql =
                    "SELECT conversation_id, sender_id, content, sent_at " +
                            "FROM message WHERE conversation_id=? " +
                            "ORDER BY sent_at DESC LIMIT ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, conversationId);
                ps.setInt(2, lim);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new NetMessage(
                                rs.getLong("conversation_id"),
                                rs.getString("sender_id"),
                                rs.getString("content"),
                                rs.getTimestamp("sent_at").toInstant()
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getHistory: " + e.getMessage());
        }

        // Affichage chronologique
        out.sort((a, b) -> a.sentAt().compareTo(b.sentAt()));
        return out;
    }
}
