package com.sportify.manager.dao;

import com.sportify.manager.services.User;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.persistence.AbstractFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresLicenceDAO extends LicenceDAO {

    private final Connection connection;

    // AJOUT DU CONSTRUCTEUR : C'est ce qui manquait pour la Factory
    public PostgresLicenceDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Licence licence) {
        String sql = "INSERT INTO licences (id, sport, type_licence, statut, date_demande, date_debut, date_fin, membre_id, date_decision, commentaire_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, licence.getId());
            stmt.setString(2, licence.getSport());
            stmt.setString(3, licence.getTypeLicence().name());
            stmt.setString(4, licence.getStatut().name());
            stmt.setDate(5, licence.getDateDemande());
            stmt.setDate(6, licence.getDateDebut());
            stmt.setDate(7, licence.getDateFin());
            stmt.setString(8, licence.getMembre().getId());
            stmt.setDate(9, licence.getDateDecision());
            stmt.setString(10, licence.getCommentaireAdmin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Licence findById(String id) {
        String sql = "SELECT * FROM licences WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToLicence(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void update(Licence licence) {
        String sql = "UPDATE licences SET statut = ?, date_debut = ?, date_fin = ?, date_decision = ?, commentaire_admin = ? WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, licence.getStatut().name());
            stmt.setDate(2, licence.getDateDebut());
            stmt.setDate(3, licence.getDateFin());
            stmt.setDate(4, licence.getDateDecision());
            stmt.setString(5, licence.getCommentaireAdmin());
            stmt.setString(6, licence.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Licence mapResultSetToLicence(ResultSet rs) throws SQLException {
        // Correction ici : on utilise le nom exact de ta méthode dans AbstractFactory
        AbstractFactory f = AbstractFactory.getFactory(); // CORRECT
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(rs.getString("membre_id"));

        return new Licence(
                rs.getString("id"),
                rs.getString("sport"),
                TypeLicence.valueOf(rs.getString("type_licence")),
                StatutLicence.valueOf(rs.getString("statut")),
                rs.getDate("date_demande"),
                rs.getDate("date_debut"),
                rs.getDate("date_fin"),
                user,
                null,
                rs.getDate("date_decision"),
                rs.getString("commentaire_admin")
        );
    }

    @Override
    public List<Licence> findByMembre(String membreId) {
        List<Licence> licences = new ArrayList<>();
        String sql = "SELECT * FROM licences WHERE membre_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) licences.add(mapResultSetToLicence(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return licences;
    }

    @Override
    public List<Licence> findByStatut(StatutLicence statut) {
        List<Licence> licences = new ArrayList<>();
        String sql = "SELECT * FROM licences WHERE statut = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) licences.add(mapResultSetToLicence(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return licences;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM licences WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}