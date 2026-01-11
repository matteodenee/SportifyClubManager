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
}
