package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresStatDAO implements StatDAO {
    private Connection connection;

    public PostgresStatDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException {
        List<SmallEvent> events = new ArrayList<>();
        String query = "SELECT * FROM small_events WHERE team_id = ? AND period = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, period);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
        }
        return events;
    }

    @Override
    public List<SmallEvent> getEventsByPlayer(String playerId, String period) throws SQLException {
        List<SmallEvent> events = new ArrayList<>();
        String query = "SELECT * FROM small_events WHERE player_id = ? AND period = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerId);
            stmt.setString(2, period);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
        }
        return events;
    }

    @Override
    public void addSmallEvent(SmallEvent event) throws SQLException {
        String query = "INSERT INTO small_events (type, description, team_id, player_id, period, event_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getType());
            stmt.setString(2, event.getDescription());
            stmt.setInt(3, event.getTeamId());
            stmt.setString(4, event.getPlayerId());
            stmt.setString(5, event.getPeriod());
            stmt.setTimestamp(6, event.getTimestamp());
            stmt.executeUpdate();
        }
    }

    // Méthode utilitaire pour transformer une ligne SQL en objet Java
    private SmallEvent mapRowToEvent(ResultSet rs) throws SQLException {
        return new SmallEvent(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getInt("team_id"),
                rs.getString("player_id"),
                rs.getTimestamp("event_date"),
                rs.getString("period")
        );
    }
}