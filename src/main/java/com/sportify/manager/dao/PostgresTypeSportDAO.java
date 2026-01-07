package com.sportify.manager.dao;

import com.sportify.manager.services.TypeSport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL pour la gestion des types de sports.
 * Adapté pour l'intégration avec le module Licence.
 */
public class PostgresTypeSportDAO extends TypeSportDAO {

    private final Connection connection;

    public PostgresTypeSportDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public TypeSport create(TypeSport typeSport) throws SQLException {
        // Note : On utilise RETURNING id_type_sport pour récupérer l'ID généré par le SERIAL
        String sql = "INSERT INTO type_sport (nom, description, nb_joueurs) VALUES (?, ?, ?) RETURNING id_type_sport";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeSport.getNom());
            stmt.setString(2, typeSport.getDescription());
            stmt.setInt(3, typeSport.getNbJoueurs());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idGenere = rs.getInt("id_type_sport");
                    typeSport.setId(idGenere);

                    // Insertion liée des rôles et statistiques
                    if (typeSport.getRoles() != null && !typeSport.getRoles().isEmpty()) {
                        insertRoles(idGenere, typeSport.getRoles());
                    }
                    if (typeSport.getStatistiques() != null && !typeSport.getStatistiques().isEmpty()) {
                        insertStatistiques(idGenere, typeSport.getStatistiques());
                    }
                    return typeSport;
                }
            }
        }
        return null;
    }

    @Override
    public List<TypeSport> getAll() throws SQLException {
        List<TypeSport> typeSports = new ArrayList<>();
        String sql = "SELECT * FROM type_sport ORDER BY nom";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TypeSport typeSport = mapResultSetToTypeSport(rs);
                // Chargement des listes dépendantes
                typeSport.setRoles(getRoles(typeSport.getId()));
                typeSport.setStatistiques(getStatistiques(typeSport.getId()));
                typeSports.add(typeSport);
            }
        }
        return typeSports;
    }

    @Override
    public TypeSport getById(int id) throws SQLException {
        String sql = "SELECT * FROM type_sport WHERE id_type_sport = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TypeSport ts = mapResultSetToTypeSport(rs);
                    ts.setRoles(getRoles(id));
                    ts.setStatistiques(getStatistiques(id));
                    return ts;
                }
            }
        }
        return null;
    }

    @Override
    public TypeSport getByNom(String nom) throws SQLException {
        String sql = "SELECT * FROM type_sport WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TypeSport ts = mapResultSetToTypeSport(rs);
                    ts.setRoles(getRoles(ts.getId()));
                    ts.setStatistiques(getStatistiques(ts.getId()));
                    return ts;
                }
            }
        }
        return null;
    }

    @Override
    public void update(TypeSport typeSport) throws SQLException {
        String sql = "UPDATE type_sport SET nom = ?, description = ?, nb_joueurs = ? WHERE id_type_sport = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeSport.getNom());
            stmt.setString(2, typeSport.getDescription());
            stmt.setInt(3, typeSport.getNbJoueurs());
            stmt.setInt(4, typeSport.getId());
            stmt.executeUpdate();

            // Refresh des rôles et stats (Delete puis Re-insert)
            deleteRoles(typeSport.getId());
            if (typeSport.getRoles() != null) insertRoles(typeSport.getId(), typeSport.getRoles());

            deleteStatistiques(typeSport.getId());
            if (typeSport.getStatistiques() != null) insertStatistiques(typeSport.getId(), typeSport.getStatistiques());
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM type_sport WHERE id_type_sport = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean isUsedByClubs(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clubs c JOIN type_sport ts ON c.type = ts.nom WHERE ts.id_type_sport = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // --- MÉTHODES PRIVÉES DE MAPPING ET UTILITAIRES ---

    private TypeSport mapResultSetToTypeSport(ResultSet rs) throws SQLException {
        return new TypeSport(
                rs.getInt("id_type_sport"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getInt("nb_joueurs")
        );
    }

    private void insertRoles(int idTypeSport, List<String> roles) throws SQLException {
        String sql = "INSERT INTO role_type_sport (id_type_sport, nom_role) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String role : roles) {
                stmt.setInt(1, idTypeSport);
                stmt.setString(2, role);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void insertStatistiques(int idTypeSport, List<String> statistiques) throws SQLException {
        String sql = "INSERT INTO statistique_type_sport (id_type_sport, nom_statistique) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String stat : statistiques) {
                stmt.setInt(1, idTypeSport);
                stmt.setString(2, stat);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<String> getRoles(int idTypeSport) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT nom_role FROM role_type_sport WHERE id_type_sport = ? ORDER BY nom_role";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTypeSport);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) roles.add(rs.getString("nom_role"));
            }
        }
        return roles;
    }

    private List<String> getStatistiques(int idTypeSport) throws SQLException {
        List<String> stats = new ArrayList<>();
        String sql = "SELECT nom_statistique FROM statistique_type_sport WHERE id_type_sport = ? ORDER BY nom_statistique";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idTypeSport);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) stats.add(rs.getString("nom_statistique"));
            }
        }
        return stats;
    }

    private void deleteRoles(int id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM role_type_sport WHERE id_type_sport = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void deleteStatistiques(int id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM statistique_type_sport WHERE id_type_sport = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}