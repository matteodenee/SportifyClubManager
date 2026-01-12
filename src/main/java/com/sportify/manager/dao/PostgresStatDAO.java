package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresStatDAO implements StatDAO {
    private Connection connection;

    public PostgresStatDAO(Connection connection) {
        this.connection = connection;
    }



    @Override
    public List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException {
        List<SmallEvent> events = new ArrayList<>();
        String query = "SELECT * FROM small_events WHERE team_id = ? AND period = ? ORDER BY event_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, period);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) events.add(mapRowToEvent(rs));
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
                while (rs.next()) events.add(mapRowToEvent(rs));
            }
        }
        return events;
    }

    @Override
    public List<SmallEvent> getEventsByMatch(int matchId) throws SQLException {
        List<SmallEvent> events = new ArrayList<>();
        String query = "SELECT * FROM small_events WHERE match_id = ? ORDER BY event_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) events.add(mapRowToEvent(rs));
            }
        }
        return events;
    }

    @Override
    public void addSmallEvent(SmallEvent event) throws SQLException {
        String query = "INSERT INTO small_events (type, description, team_id, player_id, period, event_date, match_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getType());
            stmt.setString(2, event.getDescription());
            stmt.setInt(3, event.getTeamId());
            stmt.setString(4, event.getPlayerId());
            stmt.setString(5, event.getPeriod());
            stmt.setTimestamp(6, event.getTimestamp());
            if (event.getMatchId() == null) {
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, event.getMatchId());
            }
            stmt.executeUpdate();
        }
    }



    @Override
    public Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT type, COUNT(*) as total FROM small_events WHERE team_id = ? AND period = ? GROUP BY type";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, period);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) stats.put(rs.getString("type"), rs.getInt("total"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Double> getPlayerPerformanceMetrics(String playerId, String period) throws SQLException {
        Map<String, Double> metrics = new HashMap<>();

        String query = "SELECT type, COUNT(*) as count FROM small_events WHERE player_id = ? AND period = ? GROUP BY type";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerId);
            stmt.setString(2, period);
            try (ResultSet rs = stmt.executeQuery()) {
                int total = 0;
                int goals = 0;
                while (rs.next()) {
                    int c = rs.getInt("count");
                    total += c;
                    if (rs.getString("type").equals("GOAL")) goals = c;
                }
                if (total > 0) metrics.put("GoalRatio", (double) goals / total);
            }
        }
        return metrics;
    }

    @Override
    public Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException {
        Map<String, Integer> ranking = new HashMap<>();
        String query = "SELECT player_id, COUNT(*) as score FROM small_events " +
                "WHERE team_id = ? AND type = ? GROUP BY player_id ORDER BY score DESC LIMIT ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, eventType);
            stmt.setInt(3, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ranking.put(rs.getString("player_id"), rs.getInt("score"));
            }
        }
        return ranking;
    }

    @Override
    public Map<String, Integer> getTrendData(int teamId, String eventType, String startDate, String endDate) throws SQLException {
        Map<String, Integer> trends = new HashMap<>();

        String query = "SELECT DATE(event_date) as day, COUNT(*) as total FROM small_events " +
                "WHERE team_id = ? AND type = ? AND event_date BETWEEN ? AND ? " +
                "GROUP BY day ORDER BY day ASC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, eventType);
            stmt.setString(3, startDate);
            stmt.setString(4, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) trends.put(rs.getString("day"), rs.getInt("total"));
            }
        }
        return trends;
    }

    private SmallEvent mapRowToEvent(ResultSet rs) throws SQLException {
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

    public void deleteEventsByMatch(int matchId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM small_events WHERE match_id = ?")) {
            stmt.setInt(1, matchId);
            stmt.executeUpdate();
        }
    }
}
