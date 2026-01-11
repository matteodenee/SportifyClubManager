package com.sportify.manager.dao;

import com.sportify.manager.services.NetConversation;
import com.sportify.manager.services.NetConversationType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresConversationDAO implements ConversationDAO {

    private final Connection connection;

    public PostgresConversationDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long ensureGlobalConversation() {
        try {
            String select = "SELECT id FROM conversation WHERE type='GLOBAL' AND name='GLOBAL'";
            try (PreparedStatement ps = connection.prepareStatement(select);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            String insert = "INSERT INTO conversation(name,type,created_by) VALUES ('GLOBAL','GLOBAL',NULL) RETURNING id";
            try (PreparedStatement ps = connection.prepareStatement(insert);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur ensureGlobalConversation: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public long createGroup(String groupName, String creatorId) {
        if (groupName == null || groupName.isBlank()) {
            throw new IllegalArgumentException("Nom du groupe invalide.");
        }
        try {
            String sql = "INSERT INTO conversation(name,type,created_by) VALUES (?, 'GROUP', ?) RETURNING id";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, groupName.trim());
                ps.setString(2, creatorId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur createGroup: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public void addParticipant(long conversationId, String userId) {
        try {
            String sql = "INSERT INTO conversation_participant(conversation_id,user_id) VALUES (?,?) " +
                    "ON CONFLICT (conversation_id,user_id) DO NOTHING";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, conversationId);
                ps.setString(2, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erreur addParticipant: " + e.getMessage());
        }
    }

    @Override
    public boolean isParticipant(long conversationId, String userId) {
        try {
            String sql = "SELECT 1 FROM conversation_participant WHERE conversation_id=? AND user_id=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, conversationId);
                ps.setString(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur isParticipant: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<NetConversation> listUserConversations(String userId) {
        List<NetConversation> out = new ArrayList<>();
        try {
            String sql =
                    "SELECT conv.id, conv.name, conv.type " +
                            "FROM conversation conv " +
                            "JOIN conversation_participant p ON p.conversation_id = conv.id " +
                            "WHERE p.user_id = ? " +
                            "ORDER BY CASE conv.type WHEN 'GLOBAL' THEN 0 ELSE 1 END, conv.name ASC";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new NetConversation(
                                rs.getLong("id"),
                                rs.getString("name"),
                                NetConversationType.valueOf(rs.getString("type"))
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur listUserConversations: " + e.getMessage());
        }
        return out;
    }

    @Override
    public NetConversation getConversationById(long conversationId) {
        try {
            String sql = "SELECT id,name,type FROM conversation WHERE id=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, conversationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return new NetConversation(
                            rs.getLong("id"),
                            rs.getString("name"),
                            NetConversationType.valueOf(rs.getString("type"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getConversationById: " + e.getMessage());
            return null;
        }
    }
}
