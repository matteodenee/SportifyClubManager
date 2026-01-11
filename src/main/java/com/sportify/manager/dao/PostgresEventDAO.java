package com.sportify.manager.dao;

import com.sportify.manager.services.Event;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresEventDAO implements EventDAO {
    private final Connection connection;

    public PostgresEventDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Event event) throws Exception {
        String sql = "INSERT INTO events (nom, description, date_debut, duree_minutes, lieu, type, club_id, createur_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(event.getDateDebut()));
            pstmt.setInt(4, event.getDureeMinutes());
            pstmt.setString(5, event.getLieu());
            pstmt.setString(6, event.getType());
            pstmt.setInt(7, event.getClubId());
            pstmt.setString(8, event.getCreateurId());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public Event findById(int id) throws Exception {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Event e = mapResultSetToEvent(rs);
                e.setParticipants(getParticipants(id));
                return e;
            }
        }
        return null;
    }

    @Override
    public List<Event> findAllByClubId(int clubId) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE club_id = ? ORDER BY date_debut DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clubId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    @Override
    public List<Event> findAllByCreatorId(String creatorId) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE createur_id = ? ORDER BY date_debut DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, creatorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    @Override
    public List<Event> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE date_debut BETWEEN ? AND ? ORDER BY date_debut";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(start));
            pstmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    @Override
    public void update(Event event) throws Exception {
        String sql = "UPDATE events SET nom = ?, description = ?, date_debut = ?, duree_minutes = ?, " +
                     "lieu = ?, type = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, event.getNom());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(event.getDateDebut()));
            pstmt.setInt(4, event.getDureeMinutes());
            pstmt.setString(5, event.getLieu());
            pstmt.setString(6, event.getType());
            pstmt.setInt(7, event.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void setParticipantStatus(int eventId, String userId, String status) throws Exception {
        String sql = "INSERT INTO event_participation (event_id, user_id, status) VALUES (?, ?, ?) " +
                     "ON CONFLICT (event_id, user_id) DO UPDATE SET status = EXCLUDED.status";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setString(2, userId);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, String> getParticipants(int eventId) throws Exception {
        Map<String, String> participants = new HashMap<>();
        String sql = "SELECT user_id, status FROM event_participation WHERE event_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                participants.put(rs.getString("user_id"), rs.getString("status"));
            }
        }
        return participants;
    }

    @Override
    public List<Event> findByParticipant(String userId) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.* FROM events e " +
                     "JOIN event_participation ep ON e.id = ep.event_id " +
                     "WHERE ep.user_id = ? ORDER BY e.date_debut DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        return new Event(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("description"),
            rs.getTimestamp("date_debut").toLocalDateTime(),
            rs.getInt("duree_minutes"),
            rs.getString("lieu"),
            rs.getString("type"),
            rs.getInt("club_id"),
            rs.getString("createur_id")
        );
    }
}
