package com.sportify.manager.dao;

import com.sportify.manager.services.Match;
import com.sportify.manager.services.MatchStatus;
import com.sportify.manager.services.SmallEvent;
import com.sportify.manager.dao.PostgresStatDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PostgresMatchDAO implements MatchDAO {

    private final Connection con;

    public PostgresMatchDAO(Connection connection) {
        this.con = connection;
    }

    @Override
    public Match create(Match m) throws SQLException {

        String sql = "INSERT INTO matchs(type_sport_id, home_team_id, away_team_id, datetime, location, referee, composition_deadline, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
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
        String sql = "UPDATE matchs SET type_sport_id=?, home_team_id=?, away_team_id=?, datetime=?, " +
                "location=?, referee=?, composition_deadline=?, status=?, home_score=?, away_score=? WHERE id=?";
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
            ps.setObject(9, m.getHomeScore());
            ps.setObject(10, m.getAwayScore());
            ps.setInt(11, m.getId());

            ps.executeUpdate();

            // --- ADAPTATION LOGIQUE STATS ---
            // Si le match est marqué comme FINISHED, on crée les SmallEvents
            if (m.getStatus() == MatchStatus.FINISHED && m.getHomeScore() != null && m.getAwayScore() != null) {
                generateStatsAfterMatch(m);
            }
        }
    }

    private void generateStatsAfterMatch(Match m) throws SQLException {
        PostgresStatDAO statDAO = new PostgresStatDAO(con);
        Timestamp now = Timestamp.valueOf(m.getDateTime());
        String period = "Saison " + m.getDateTime().getYear();

        statDAO.deleteEventsByMatch(m.getId());

        String resultHome = (m.getHomeScore() > m.getAwayScore()) ? "VICTOIRE" :
                (m.getHomeScore() < m.getAwayScore()) ? "DEFAITE" : "NUL";

        statDAO.addSmallEvent(new SmallEvent(0, "MATCH", "Match joué", m.getHomeTeamId(), null, now, period, m.getId()));
        statDAO.addSmallEvent(new SmallEvent(0, resultHome, "Résultat final", m.getHomeTeamId(), null, now, period, m.getId()));

        for (int i = 0; i < m.getHomeScore(); i++) {
            String playerId = getRandomPlayerId(m.getHomeTeamId());
            statDAO.addSmallEvent(new SmallEvent(0, "GOAL", "But marqué", m.getHomeTeamId(), playerId, now, period, m.getId()));
        }

        String resultAway = (m.getAwayScore() > m.getHomeScore()) ? "VICTOIRE" :
                (m.getAwayScore() < m.getHomeScore()) ? "DEFAITE" : "NUL";

        statDAO.addSmallEvent(new SmallEvent(0, "MATCH", "Match joué", m.getAwayTeamId(), null, now, period, m.getId()));
        statDAO.addSmallEvent(new SmallEvent(0, resultAway, "Résultat final", m.getAwayTeamId(), null, now, period, m.getId()));

        for (int i = 0; i < m.getAwayScore(); i++) {
            String playerId = getRandomPlayerId(m.getAwayTeamId());
            statDAO.addSmallEvent(new SmallEvent(0, "GOAL", "But marqué", m.getAwayTeamId(), playerId, now, period, m.getId()));
        }

        List<String> stats = getSportStatNames(m.getTypeSportId());
        if (!stats.isEmpty()) {
            Random random = new Random();
            for (String stat : stats) {
                String upper = stat == null ? "" : stat.toUpperCase();
                if (upper.equals("MATCH") || upper.equals("VICTOIRE") || upper.equals("DEFAITE")
                        || upper.equals("NUL") || upper.equals("GOAL") || upper.equals("BUT")) {
                    continue;
                }
                int homeCount = random.nextInt(3);
                int awayCount = random.nextInt(3);
                for (int i = 0; i < homeCount; i++) {
                    String playerId = getRandomPlayerId(m.getHomeTeamId());
                    statDAO.addSmallEvent(new SmallEvent(0, stat, "Évènement", m.getHomeTeamId(), playerId, now, period, m.getId()));
                }
                for (int i = 0; i < awayCount; i++) {
                    String playerId = getRandomPlayerId(m.getAwayTeamId());
                    statDAO.addSmallEvent(new SmallEvent(0, stat, "Évènement", m.getAwayTeamId(), playerId, now, period, m.getId()));
                }
            }
        }
    }

    private List<String> getSportStatNames(int sportId) throws SQLException {
        List<String> stats = new ArrayList<>();
        String sql = "SELECT stat_name FROM sport_stats WHERE sport_id = ? ORDER BY stat_name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.add(rs.getString("stat_name"));
                }
            }
        }
        return stats;
    }

    private String getRandomPlayerId(int teamId) throws SQLException {
        String sql = "SELECT id_user FROM team_member WHERE id_team = ? ORDER BY random() LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_user");
                }
            }
        }
        return null;
    }

    @Override
    public Match getById(int id) throws SQLException {
        String sql = "SELECT * FROM matchs WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    @Override
    public List<Match> getAll() throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT * FROM matchs ORDER BY datetime DESC";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Match> getByClub(int clubId) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.* FROM matchs m " +
                "JOIN team th ON m.home_team_id = th.id_team " +
                "JOIN team ta ON m.away_team_id = ta.id_team " +
                "WHERE th.clubid = ? OR ta.clubid = ? " +
                "ORDER BY m.datetime DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public int getTypeSportId(int matchId) throws SQLException {
        String sql = "SELECT type_sport_id FROM matchs WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("type_sport_id");
            throw new SQLException("Match introuvable");
        }
    }

    @Override
    public LocalDateTime getCompositionDeadline(int matchId) throws SQLException {
        String sql = "SELECT composition_deadline FROM matchs WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("composition_deadline");
                return ts == null ? null : ts.toLocalDateTime();
            }
            throw new SQLException("Match introuvable");
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
