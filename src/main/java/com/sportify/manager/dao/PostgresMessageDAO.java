package com.sportify.manager.dao;

import com.sportify.manager.services.NetMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostgresMessageDAO implements MessageDAO {

    private final Connection connection;

    public PostgresMessageDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long save(NetMessage msg) throws SQLException {
        if (msg.content() == null || msg.content().isBlank()) {
            throw new IllegalArgumentException("Message vide interdit.");
        }

        String sql = "INSERT INTO message(conversation_id,sender_id,content,sent_at) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, msg.conversationId());
            ps.setString(2, msg.senderId());
            ps.setString(3, msg.content());
            ps.setTimestamp(4, Timestamp.from(msg.sentAt()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0L;
    }

    @Override
    public List<NetMessage> getHistory(long conversationId, int limit) throws SQLException {
        int lim = Math.max(1, Math.min(limit, 300));
        List<NetMessage> out = new ArrayList<>();

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

        out.sort((a, b) -> a.sentAt().compareTo(b.sentAt()));
        return out;
    }
}
