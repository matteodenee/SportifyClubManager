package com.sportify.manager.dao;

import com.sportify.manager.services.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostgresReservationDAO implements ReservationDAO {
    private final Connection connection;

    public PostgresReservationDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insert(Reservation reservation) {
        String sql = "INSERT INTO equipment_reservations (equipment_id, user_id, start_date, end_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getEquipmentId());
            stmt.setString(2, reservation.getUserId());
            stmt.setDate(3, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(4, Date.valueOf(reservation.getEndDate()));
            stmt.setString(5, reservation.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la reservation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Reservation> findOverlapping(int equipmentId, LocalDate start, LocalDate end) {
        List<Reservation> results = new ArrayList<>();
        String sql = "SELECT id, equipment_id, user_id, start_date, end_date, status " +
                "FROM equipment_reservations " +
                "WHERE equipment_id = ? AND NOT (end_date < ? OR start_date > ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Reservation(
                            rs.getInt("id"),
                            rs.getInt("equipment_id"),
                            rs.getString("user_id"),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getDate("end_date").toLocalDate(),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche de conflits: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Reservation> listByUser(String userId) {
        List<Reservation> results = new ArrayList<>();
        String sql = "SELECT id, equipment_id, user_id, start_date, end_date, status " +
                "FROM equipment_reservations WHERE user_id = ? ORDER BY start_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Reservation(
                            rs.getInt("id"),
                            rs.getInt("equipment_id"),
                            rs.getString("user_id"),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getDate("end_date").toLocalDate(),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listByUser: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<Reservation> listByClub(int clubId) {
        List<Reservation> results = new ArrayList<>();
        String sql = "SELECT r.id, r.equipment_id, r.user_id, r.start_date, r.end_date, r.status " +
                "FROM equipment_reservations r " +
                "JOIN equipments e ON e.id = r.equipment_id " +
                "WHERE e.club_id = ? " +
                "ORDER BY r.start_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Reservation(
                            rs.getInt("id"),
                            rs.getInt("equipment_id"),
                            rs.getString("user_id"),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getDate("end_date").toLocalDate(),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listByClub: " + e.getMessage());
        }
        return results;
    }

    @Override
    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE equipment_reservations SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL updateStatus: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countOverlapping(int equipmentId, LocalDate start, LocalDate end) {
        String sql = "SELECT COUNT(*) FROM equipment_reservations " +
                "WHERE equipment_id = ? AND status IN ('PENDING','APPROVED') " +
                "AND NOT (end_date < ? OR start_date > ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL countOverlapping: " + e.getMessage());
        }
        return 0;
    }
}
