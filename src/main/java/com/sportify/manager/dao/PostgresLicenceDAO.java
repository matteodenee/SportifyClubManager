package com.sportify.manager.dao;

import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.persistence.AbstractFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresLicenceDAO extends LicenceDAO {

    private final Connection connection;

    public PostgresLicenceDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Licence licence) {

        String sql = "INSERT INTO licences (id, sport_id, type_licence, statut, date_demande, membre_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, licence.getId());
            stmt.setInt(2, licence.getSport().getId());
            stmt.setString(3, licence.getTypeLicence().name());
            stmt.setString(4, licence.getStatut().name()); // Devrait être 'EN_ATTENTE'
            stmt.setDate(5, licence.getDateDemande());
            stmt.setString(6, licence.getMembre().getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion de la licence : " + e.getMessage());

            throw new RuntimeException("Impossible d'enregistrer la licence en base de données.", e);
        }
    }

    @Override
    public void update(Licence licence) {

        String sql = "UPDATE licences SET statut = ?, date_debut = ?, date_fin = ?, " +
                "date_decision = ?, commentaire_admin = ? WHERE id = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, licence.getStatut().name());
            stmt.setDate(2, licence.getDateDebut());
            stmt.setDate(3, licence.getDateFin());
            stmt.setDate(4, licence.getDateDecision());
            stmt.setString(5, licence.getCommentaireAdmin());
            stmt.setString(6, licence.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la licence.", e);
        }
    }

    @Override
    public Licence findById(String id) {
        String sql = "SELECT l.*, ts.nom as sport_nom, ts.description as sport_desc, ts.nb_joueurs " +
                "FROM licences l " +
                "JOIN type_sports ts ON l.sport_id = ts.id " +
                "WHERE l.id = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToLicence(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Licence> findByMembre(String membreId) {
        List<Licence> licences = new ArrayList<>();
        String sql = "SELECT l.*, ts.nom as sport_nom, ts.description as sport_desc, ts.nb_joueurs " +
                "FROM licences l " +
                "JOIN type_sports ts ON l.sport_id = ts.id " +
                "WHERE l.membre_id = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licences.add(mapResultSetToLicence(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return licences;
    }

    @Override
    public List<Licence> findByStatut(StatutLicence statut) {
        List<Licence> licences = new ArrayList<>();
        String sql = "SELECT l.*, ts.nom as sport_nom, ts.description as sport_desc, ts.nb_joueurs " +
                "FROM licences l " +
                "JOIN type_sports ts ON l.sport_id = ts.id " +
                "WHERE l.statut = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licences.add(mapResultSetToLicence(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return licences;
    }


    private Licence mapResultSetToLicence(ResultSet rs) throws SQLException {

        User membre = PostgresUserDAO.getInstance().getUserById(rs.getString("membre_id"));

        TypeSport sport = new TypeSport(
                rs.getInt("sport_id"),
                rs.getString("sport_nom"),
                rs.getString("sport_desc"),
                rs.getInt("nb_joueurs")
        );

        return new Licence(
                rs.getString("id"),
                sport,
                TypeLicence.valueOf(rs.getString("type_licence")),
                StatutLicence.valueOf(rs.getString("statut")),
                rs.getDate("date_demande"),
                rs.getDate("date_debut"),
                rs.getDate("date_fin"),
                membre,
                null,
                rs.getDate("date_decision"),
                rs.getString("commentaire_admin")
        );
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