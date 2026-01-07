package com.sportify.manager.dao;

import com.sportify.manager.services.Club;
import com.sportify.manager.services.MembershipRequest;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PostgresClubDAO extends ClubDAO {

    private Connection connection;

    public PostgresClubDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addClub(Club club) throws SQLException {
        String query = "INSERT INTO clubs (name, description, type, meetingschedule, maxcapacity, status, requirements) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setString(3, club.getType());
            stmt.setString(4, club.getMeetingSchedule());
            stmt.setInt(5, club.getMaxCapacity());
            stmt.setString(6, club.getStatus());
            stmt.setString(7, club.getRequirements());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateClub(Club club) throws SQLException {
        String query = "UPDATE clubs SET name = ?, description = ?, type = ?, meetingschedule = ?, maxcapacity = ?, status = ?, requirements = ? WHERE clubid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setString(3, club.getType());
            stmt.setString(4, club.getMeetingSchedule());
            stmt.setInt(5, club.getMaxCapacity());
            stmt.setString(6, club.getStatus());
            stmt.setString(7, club.getRequirements());
            stmt.setInt(8, club.getClubID());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteClub(int clubID) throws SQLException {
        String query = "DELETE FROM clubs WHERE clubid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            stmt.executeUpdate();
        }
    }

    @Override
    public Club getClubById(int clubID) throws SQLException {
        String query = "SELECT * FROM clubs WHERE clubid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Club club = new Club(
                            rs.getInt("clubid"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("type"),
                            rs.getString("meetingschedule"),
                            rs.getInt("maxcapacity")
                    );
                    club.setStatus(rs.getString("status"));
                    club.setRequirements(rs.getString("requirements"));
                    club.setCurrentMemberCount(this.getCurrentMembers(clubID));
                    return club;
                }
            }
        }
        return null;
    }

    @Override
    public List<Club> getAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String query = "SELECT * FROM clubs";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Club club = new Club(
                        rs.getInt("clubid"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getString("meetingschedule"),
                        rs.getInt("maxcapacity")
                );
                club.setStatus(rs.getString("status"));
                club.setRequirements(rs.getString("requirements"));
                club.setCurrentMemberCount(this.getCurrentMembers(club.getClubID()));
                clubs.add(club);
            }
        }
        return clubs;
    }

    @Override
    public void addMemberToClub(int clubId, String userId) throws SQLException {
        String sql = "INSERT INTO members (clubid, userid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public int getCurrentMembers(int clubId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM members WHERE clubid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getMaxCapacity(int clubId) throws SQLException {
        String sql = "SELECT maxcapacity FROM clubs WHERE clubid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("maxcapacity");
            }
        }
        return 0;
    }

    @Override
    public boolean isMember(int clubId, String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM members WHERE clubid = ? AND userid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public void createMembershipRequest(int clubId, String userId) throws SQLException {
        String query = "INSERT INTO membership_requests (clubid, userid, status) VALUES (?, ?, 'PENDING')";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean hasPendingRequest(int clubId, String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM membership_requests WHERE clubid = ? AND userid = ? AND status = 'PENDING'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clubId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public List<MembershipRequest> getPendingRequests() throws SQLException {
        // Appelle la méthode filtrée avec null pour tout voir (Admin)
        return getPendingRequestsByDirector(null);
    }

    /**
     * Implémentation de la méthode filtrée pour le Directeur
     */
    @Override
    public List<MembershipRequest> getPendingRequestsByDirector(String directorId) throws SQLException {
        List<MembershipRequest> requests = new ArrayList<>();
        String query = "SELECT r.requestid, r.clubid, r.userid, c.name as clubname, u.name as username, r.status " +
                "FROM membership_requests r " +
                "JOIN clubs c ON r.clubid = c.clubid " +
                "JOIN users u ON r.userid = u.id " +
                "WHERE r.status = 'PENDING'";

        // Ajout du filtre si un ID de directeur est spécifié
        if (directorId != null) {
            query += " AND c.manager_id = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (directorId != null) {
                stmt.setString(1, directorId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(new MembershipRequest(
                            rs.getInt("requestid"),
                            rs.getInt("clubid"),
                            rs.getString("userid"),
                            rs.getString("clubname"),
                            rs.getString("username"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return requests;
    }

    @Override
    public void updateRequestStatus(int requestId, String status) throws SQLException {
        String query = "UPDATE membership_requests SET status = ? WHERE requestid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

    @Override
    public MembershipRequest getRequestById(int requestId) throws SQLException {
        String query = "SELECT r.requestid, r.clubid, r.userid, c.name as clubname, u.name as username, r.status " +
                "FROM membership_requests r " +
                "JOIN clubs c ON r.clubid = c.clubid " +
                "JOIN users u ON r.userid = u.id " +
                "WHERE r.requestid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MembershipRequest(
                            rs.getInt("requestid"),
                            rs.getInt("clubid"),
                            rs.getString("userid"),
                            rs.getString("clubname"),
                            rs.getString("username"),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }
}