package com.sportify.manager.dao;

import com.sportify.manager.services.Equipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresEquipmentDAO implements EquipmentDAO {
    private final Connection connection;

    public PostgresEquipmentDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insert(Equipment equipment) {
        String sql = "INSERT INTO equipments (name, type, condition, quantity, type_id) " +
                "VALUES (?, ?, ?, ?, (SELECT id FROM equipment_types WHERE LOWER(name)=LOWER(?)))";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getType());
            stmt.setString(3, equipment.getCondition());
            stmt.setInt(4, equipment.getQuantity());
            stmt.setString(5, equipment.getType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion d'equipement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Equipment findByName(String name) {
        String sql = "SELECT id, name, type, condition, quantity FROM equipments WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("condition"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche d'equipement par nom: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Equipment findById(int id) {
        String sql = "SELECT id, name, type, condition, quantity FROM equipments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("condition"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche d'equipement: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateQuantity(int id, int newQty) {
        String sql = "UPDATE equipments SET quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQty);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise a jour quantite: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Equipment> listAll() {
        List<Equipment> results = new ArrayList<>();
        String sql = "SELECT id, name, type, condition, quantity FROM equipments ORDER BY name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("condition"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing d'equipement: " + e.getMessage());
        }
        return results;
    }
}
