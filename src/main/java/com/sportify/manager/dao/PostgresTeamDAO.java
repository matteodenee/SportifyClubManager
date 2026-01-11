package com.sportify.manager.dao;

import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL du DAO pour les équipes.
 */
public class PostgresTeamDAO implements TeamDAO {
    private final Connection connection;

    public PostgresTeamDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Team team) throws Exception {
        String sql = "INSERT INTO team (nom, categorie, club_id, id_coach, id_type_sport) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.getNom());
            stmt.setString(2, team.getCategorie());
            stmt.setInt(3, team.getClubId());
            stmt.setString(4, team.getCoachId());
            
            if (team.getTypeSportId() != null) {
                stmt.setInt(5, team.getTypeSportId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Team findById(int teamId) throws Exception {
        String sql = "SELECT * FROM team WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Team team = extractTeamFromResultSet(rs);
                    // Charger les joueurs
                    team.setPlayers(getTeamPlayers(teamId));
                    return team;
                }
            }
        }
        return null;
    }

    @Override
    public List<Team> findAllByClubId(int clubId) throws Exception {
        String sql = "SELECT * FROM team WHERE club_id = ?";
        List<Team> teams = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = extractTeamFromResultSet(rs);
                    team.setPlayers(getTeamPlayers(team.getId()));
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    @Override
    public List<Team> findAllByCoachId(String coachId) throws Exception {
        String sql = "SELECT * FROM team WHERE id_coach = ?";
        List<Team> teams = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, coachId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = extractTeamFromResultSet(rs);
                    team.setPlayers(getTeamPlayers(team.getId()));
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    @Override
    public void update(Team team) throws Exception {
        String sql = "UPDATE team SET nom = ?, categorie = ?, club_id = ?, id_coach = ?, id_type_sport = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, team.getNom());
            stmt.setString(2, team.getCategorie());
            stmt.setInt(3, team.getClubId());
            stmt.setString(4, team.getCoachId());
            
            if (team.getTypeSportId() != null) {
                stmt.setInt(5, team.getTypeSportId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setInt(6, team.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int teamId) throws Exception {
        // D'abord supprimer les membres de l'équipe
        String sqlMembers = "DELETE FROM team_member WHERE team_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlMembers)) {
            stmt.setInt(1, teamId);
            stmt.executeUpdate();
        }
        
        // Ensuite supprimer l'équipe
        String sql = "DELETE FROM team WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void addPlayer(int teamId, String playerId) throws Exception {
        String sql = "INSERT INTO team_member (team_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, playerId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void removePlayer(int teamId, String playerId) throws Exception {
        String sql = "DELETE FROM team_member WHERE team_id = ? AND user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, playerId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<String> getTeamPlayers(int teamId) throws Exception {
        String sql = "SELECT user_id FROM team_member WHERE team_id = ?";
        List<String> players = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("user_id"));
                }
            }
        }
        return players;
    }

    @Override
    public List<User> getTeamPlayersWithDetails(int teamId) throws Exception {
        String sql = "SELECT u.id, u.password, u.name, u.email, u.role " +
                     "FROM users u " +
                     "INNER JOIN team_member tm ON u.id = tm.user_id " +
                     "WHERE tm.team_id = ?";
        List<User> players = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Constructeur User : id, pwd, name, email, role
                    User user = new User(
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                    players.add(user);
                }
            }
        }
        return players;
    }

    /**
     * Extrait un objet Team à partir d'un ResultSet.
     */
    private Team extractTeamFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String categorie = rs.getString("categorie");
        int clubId = rs.getInt("club_id");
        String coachId = rs.getString("id_coach");
        
        Integer typeSportId = null;
        int sportId = rs.getInt("id_type_sport");
        if (!rs.wasNull()) {
            typeSportId = sportId;
        }
        
        return new Team(id, nom, categorie, clubId, coachId, typeSportId);
    }
}
