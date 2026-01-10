package com.sportify.manager.dao;

import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresTeamDAO implements TeamDAO {
    private final Connection connection;

    public PostgresTeamDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Team create(Team team) throws Exception {
        String sql = "INSERT INTO team (nom, categorie, id_club, id_coach, id_type_sport) VALUES (?, ?, ?, ?, ?) RETURNING id_team";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, team.getNom());
            pstmt.setString(2, team.getCategorie());
            pstmt.setInt(3, team.getClubId());
            
            if (team.getCoachId() != null) pstmt.setInt(4, team.getCoachId());
            else pstmt.setNull(4, Types.INTEGER);
            
            if (team.getTypeSportId() != null) pstmt.setInt(5, team.getTypeSportId());
            else pstmt.setNull(5, Types.INTEGER);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                team.setId(rs.getInt(1));
            }
        }
        return team;
    }

    @Override
    public Team findById(int id) throws Exception {
        String sql = "SELECT * FROM team WHERE id_team = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Team t = mapResultSetToTeam(rs);
                t.setPlayers(findPlayersByTeamId(id)); // Charger les joueurs
                return t;
            }
        }
        return null;
    }

    @Override
    public List<Team> findAllByClubId(int clubId) throws Exception {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM team WHERE id_club = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clubId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
        }
        return teams;
    }

    @Override
    public void update(Team team) throws Exception {
        String sql = "UPDATE team SET nom=?, categorie=?, id_coach=?, id_type_sport=? WHERE id_team=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, team.getNom());
            pstmt.setString(2, team.getCategorie());
            
            if (team.getCoachId() != null) pstmt.setInt(3, team.getCoachId());
            else pstmt.setNull(3, Types.INTEGER);
            
            if (team.getTypeSportId() != null) pstmt.setInt(4, team.getTypeSportId());
            else pstmt.setNull(4, Types.INTEGER);
            
            pstmt.setInt(5, team.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM team WHERE id_team = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- Gestion des membres (Joueurs) ---

    @Override
    public void addPlayerToTeam(int teamId, String userId) throws Exception {
        String sql = "INSERT INTO team_member (id_team, id_user) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void removePlayerFromTeam(int teamId, String userId) throws Exception {
        String sql = "DELETE FROM team_member WHERE id_team = ? AND id_user = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<User> findPlayersByTeamId(int teamId) throws Exception {
        List<User> players = new ArrayList<>();
        // Jointure avec la table users pour récupérer les infos complètes
        String sql = "SELECT u.* FROM users u JOIN team_member tm ON u.id = tm.id_user WHERE tm.id_team = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Instanciation basique de User (adapter selon votre constructeur User existant)
                User u = new User(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
                players.add(u);
            }
        }
        return players;
    }

    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        int coachId = rs.getInt("id_coach");
        if (rs.wasNull()) coachId = 0;
        
        int typeSportId = rs.getInt("id_type_sport");
        if (rs.wasNull()) typeSportId = 0;

        return new Team(
            rs.getInt("id_team"),
            rs.getString("nom"),
            rs.getString("categorie"),
            rs.getInt("id_club"),
            coachId == 0 ? null : coachId,
            typeSportId == 0 ? null : typeSportId
        );
    }
}
