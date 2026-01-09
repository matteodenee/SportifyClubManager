package com.sportify.manager.dao;

import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.MatchRequestStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresMatchRequestDAO implements MatchRequestDAO {

    private final Connection con;

    public PostgresMatchRequestDAO(Connection connection) {
        this.con = connection;
    }

    @Override
    public MatchRequest create(MatchRequest request) throws SQLException {
        String sql = """
            INSERT INTO match_requests(
                requester_club_id, opponent_club_id, home_team_id, away_team_id,
                type_sport_id, requested_datetime, location, referee, requested_by, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, request_date
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, request.getRequesterClubId());
            ps.setInt(2, request.getOpponentClubId());
            ps.setInt(3, request.getHomeTeamId());
            ps.setInt(4, request.getAwayTeamId());
            ps.setInt(5, request.getTypeSportId());
            ps.setTimestamp(6, Timestamp.valueOf(request.getRequestedDateTime()));
            ps.setString(7, request.getLocation());
            ps.setString(8, request.getReferee());
            ps.setString(9, request.getRequestedBy());
            ps.setString(10, request.getStatus().name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                request.setId(rs.getInt("id"));
                return request;
            }
        }
        return null;
    }

    @Override
    public MatchRequest getById(int id) throws SQLException {
        String sql = "SELECT * FROM match_requests WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    @Override
    public List<MatchRequest> getPending() throws SQLException {
        return getByStatus(MatchRequestStatus.PENDING);
    }

    @Override
    public List<MatchRequest> getByClub(int clubId) throws SQLException {
        List<MatchRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE requester_club_id = ? OR opponent_club_id = ? ORDER BY request_date DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public void updateStatus(int id, MatchRequestStatus status, Integer matchId) throws SQLException {
        String sql = "UPDATE match_requests SET status = ?, match_id = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            if (matchId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, matchId);
            }
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    private List<MatchRequest> getByStatus(MatchRequestStatus status) throws SQLException {
        List<MatchRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE status = ? ORDER BY request_date DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private MatchRequest map(ResultSet rs) throws SQLException {
        Timestamp requestedTs = rs.getTimestamp("requested_datetime");
        Timestamp requestDate = rs.getTimestamp("request_date");
        return new MatchRequest(
                rs.getInt("id"),
                rs.getInt("requester_club_id"),
                rs.getInt("opponent_club_id"),
                rs.getInt("home_team_id"),
                rs.getInt("away_team_id"),
                rs.getInt("type_sport_id"),
                requestedTs == null ? null : requestedTs.toLocalDateTime(),
                rs.getString("location"),
                rs.getString("referee"),
                rs.getString("requested_by"),
                MatchRequestStatus.valueOf(rs.getString("status")),
                requestDate == null ? null : requestDate.toLocalDateTime(),
                (Integer) rs.getObject("match_id")
        );
    }
}
