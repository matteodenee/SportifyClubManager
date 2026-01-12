package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostgresSmallEventDAO implements SmallEventDAO {
    private final Connection connection;

    public PostgresSmallEventDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(SmallEvent event) {
        String sql = "INSERT INTO small_events (type, description, team_id, player_id, period, event_date, match_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getType());
            stmt.setString(2, event.getDescription());
            stmt.setInt(3, event.getTeamId());
            stmt.setString(4, event.getPlayerId());
            stmt.setString(5, event.getPeriod());
            Timestamp ts = event.getTimestamp() != null ? event.getTimestamp() : new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(6, ts);
            if (event.getMatchId() == null) {
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(7, event.getMatchId());
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création d'un small event: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<SmallEvent> findByMatch(int matchId) {
        List<SmallEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM small_events WHERE match_id = ? ORDER BY event_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing des small events par match: " + e.getMessage());
        }
        return events;
    }

    @Override
    public List<SmallEvent> findByTeamAndPeriod(int teamId, String period) {
        List<SmallEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM small_events WHERE team_id = ? AND period = ? ORDER BY event_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, period);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing des small events: " + e.getMessage());
        }
        return events;
    }

    @Override
    public boolean deleteByMatch(int matchId) {
        String sql = "DELETE FROM small_events WHERE match_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression des small events: " + e.getMessage());
            return false;
        }
    }

    private SmallEvent mapRow(ResultSet rs) throws SQLException {
        return new SmallEvent(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getInt("team_id"),
                rs.getString("player_id"),
                rs.getTimestamp("event_date"),
                rs.getString("period"),
                rs.getObject("match_id", Integer.class)
        );
    }
}
