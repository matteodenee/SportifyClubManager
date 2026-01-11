package com.sportify.manager.dao;

import com.sportify.manager.services.EquipmentType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresEquipmentTypeDAO implements EquipmentTypeDAO {
    private final Connection connection;

    public PostgresEquipmentTypeDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(EquipmentType type) {
        String sql = "INSERT INTO equipment_types (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la creation d'un type d'equipement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(EquipmentType type) {
        String sql = "UPDATE equipment_types SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise a jour du type d'equipement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM equipment_types WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du type d'equipement: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<EquipmentType> listAll() {
        List<EquipmentType> results = new ArrayList<>();
        String sql = "SELECT id, name, description FROM equipment_types ORDER BY name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new EquipmentType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du listing des types d'equipement: " + e.getMessage());
        }
        return results;
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM equipment_types WHERE LOWER(name) = LOWER(?) LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du controle de doublon: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isTypeUsed(int typeId) {
        String sql = "SELECT 1 FROM equipments " +
                "WHERE type_id = ? " +
                "OR LOWER(type) = LOWER((SELECT name FROM equipment_types WHERE id = ?)) " +
                "LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, typeId);
            stmt.setInt(2, typeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du controle d'utilisation: " + e.getMessage());
            return false;
        }
    }
}
