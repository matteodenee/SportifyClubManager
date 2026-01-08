package com.sportify.manager.MatchManagement;

import com.sportify.manager.match.model.Match;
import com.sportify.manager.match.model.MatchStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresMatchDAO extends MatchDAO {

    private final Connection con;

    public PostgresMatchDAO(Connection connection) {
        this.con = connection;
    }

    @Override
    public Match create(Match m) throws SQLException {
        String sql = """
            INSERT INTO matchs(type_sport_id, home_team_id, away_team_id, datetime, location, referee, composition_deadline, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getTypeSportId());
            ps.setInt(2, m.getHomeTeamId());
            ps.setInt(3, m.getAwayTeamId());
            ps.setTimestamp(4, Timestamp.valueOf(m.getDateTime()));
            ps.setString(5, m.getLocation());
            ps.setString(6, m.getReferee());
            if (m.getCompositionDeadline() == null) ps.setNull(7, Types.TIMESTAMP);
            else ps.setTimestamp(7, Timestamp.valueOf(m.getCompositionDeadline()));
            ps.setString(8, m.getStatus().name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m.setId(rs.getInt("id"));
                return m;
            }
            return null;
        }
    }

    @Override
    public void update(Match m) throws SQLException {
        String sql = """
            UPDATE matchs
            SET type_sport_id=?, home_team_id=?, away_team_id=?, datetime=?, location=?, referee=?, composition_deadline=?, status=?
            WHERE id=?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getTypeSportId());
            ps.setInt(2, m.getHomeTeamId());
            ps.setInt(3, m.getAwayTeamId());
            ps.setTimestamp(4, Timestamp.valueOf(m.getDateTime()));
            ps.setString(5, m.getLocation());
            ps.setString(6, m.getReferee());
            if (m.getCompositionDeadline() == null) ps.setNull(7, Types.TIMESTAMP);
            else ps.setTimestamp(7, Timestamp.valueOf(m.getCompositionDeadline()));
            ps.setString(8, m.getStatus().name());
            ps.setInt(9, m.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public Match getById(int id) throws SQLException {
        String sql = """
            SELECT id, type_sport_id, home_team_id, away_team_id, datetime, location, referee, composition_deadline, status, home_score, away_score
            FROM matchs
            WHERE id=?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return map(rs);
        }
    }

    @Override
    public List<Match> getAll() throws SQLException {
        String sql = """
            SELECT id, type_sport_id, home_team_id, away_team_id, datetime, location, referee, composition_deadline, status, home_score, away_score
            FROM matchs
            ORDER BY datetime DESC
        """;
        List<Match> list = new ArrayList<>();
        try (Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public int getTypeSportId(int matchId) throws SQLException {
        String sql = "SELECT type_sport_id FROM matchs WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("Match introuvable");
            return rs.getInt("type_sport_id");
        }
    }

    @Override
    public LocalDateTime getCompositionDeadline(int matchId) throws SQLException {
        String sql = "SELECT composition_deadline FROM matchs WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("Match introuvable");
            Timestamp ts = rs.getTimestamp("composition_deadline");
            return ts == null ? null : ts.toLocalDateTime();
        }
    }

    private Match map(ResultSet rs) throws SQLException {
        Timestamp dl = rs.getTimestamp("composition_deadline");
        Timestamp dt = rs.getTimestamp("datetime");

        return new Match(
                rs.getInt("id"),
                rs.getInt("type_sport_id"),
                rs.getInt("home_team_id"),
                rs.getInt("away_team_id"),
                dt.toLocalDateTime(),
                rs.getString("location"),
                rs.getString("referee"),
                dl == null ? null : dl.toLocalDateTime(),
                MatchStatus.valueOf(rs.getString("status")),
                (Integer) rs.getObject("home_score"),
                (Integer) rs.getObject("away_score")
        );
    }
}