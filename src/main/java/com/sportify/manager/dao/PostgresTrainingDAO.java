package com.sportify.manager.dao;

import com.sportify.manager.services.Entrainement;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PostgresTrainingDAO implements TrainingDAO {
    private final Connection connection;

    public PostgresTrainingDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(Entrainement entrainement) {
        String sql = "INSERT INTO entrainements (date, heure, lieu, activite, club_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(entrainement.getDate()));
            stmt.setTime(2, Time.valueOf(entrainement.getHeure()));
            stmt.setString(3, entrainement.getLieu());
            stmt.setString(4, entrainement.getActivite());
            stmt.setInt(5, entrainement.getClubId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création d'un entrainement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Entrainement> getUpcomingByClub(int clubId, LocalDate fromDate) {
        List<Entrainement> results = new ArrayList<>();
        LocalDate start = (fromDate == null) ? LocalDate.now() : fromDate;
        String sql = "SELECT id, date, heure, lieu, activite, club_id " +
                "FROM entrainements WHERE club_id = ? AND date >= ? " +
                "ORDER BY date ASC, heure ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setDate(2, Date.valueOf(start));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing des entrainements: " + e.getMessage());
        }
        return results;
    }

    @Override
    public Optional<Entrainement> getById(int id) {
        String sql = "SELECT id, date, heure, lieu, activite, club_id FROM entrainements WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération d'un entrainement: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean setParticipation(int entrainementId, String userId, ParticipationStatus status) {
        String sql = "INSERT INTO entrainement_participation (entrainement_id, user_id, status) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (entrainement_id, user_id) DO UPDATE SET status = EXCLUDED.status";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entrainementId);
            stmt.setString(2, userId);
            stmt.setString(3, status.name());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour de la participation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<User, ParticipationStatus> getParticipation(int entrainementId) {
        Map<User, ParticipationStatus> results = new LinkedHashMap<>();
        String sql = "SELECT u.id, u.name, u.email, u.role, p.status " +
                "FROM entrainement_participation p " +
                "JOIN users u ON u.id = p.user_id " +
                "WHERE p.entrainement_id = ? " +
                "ORDER BY u.name";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entrainementId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role"));
                    ParticipationStatus status = parseStatus(rs.getString("status"));
                    results.put(user, status);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des participations: " + e.getMessage());
        }
        return results;
    }

    private Entrainement mapRow(ResultSet rs) throws SQLException {
        return new Entrainement(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("heure").toLocalTime(),
                rs.getString("lieu"),
                rs.getString("activite"),
                rs.getInt("club_id"));
    }

    private ParticipationStatus parseStatus(String value) {
        if (value == null) {
            return ParticipationStatus.PENDING;
        }
        try {
            return ParticipationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ParticipationStatus.PENDING;
        }
    }
}
