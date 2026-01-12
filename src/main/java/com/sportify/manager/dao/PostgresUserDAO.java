package com.sportify.manager.dao;

import com.sportify.manager.services.User;
import java.sql.*;

public class PostgresUserDAO extends UserDAO {

    private static final String DEFAULT_URL = "jdbc:postgresql://162.38.112.60:5432/erben-db";
    private static final String DEFAULT_USER = "rasim.erben";
    private static final String DEFAULT_PASSWORD = "erben2024!";

    private final String url;
    private final String user;
    private final String password;

    private static PostgresUserDAO instance;
    private static Connection connection;

    private PostgresUserDAO() {
        this.url = (System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty()) ? System.getenv("DB_URL") : DEFAULT_URL;
        this.user = (System.getenv("DB_USER") != null && !System.getenv("DB_USER").isEmpty()) ? System.getenv("DB_USER") : DEFAULT_USER;
        this.password = (System.getenv("DB_PASSWORD") != null && !System.getenv("DB_PASSWORD").isEmpty()) ? System.getenv("DB_PASSWORD") : DEFAULT_PASSWORD;

        try {
            Class.forName("org.postgresql.Driver");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur lors de la connexion à PostgreSQL: " + e.getMessage());
        }
    }

    public static synchronized PostgresUserDAO getInstance() {
        if (instance == null) {
            instance = new PostgresUserDAO();
        }
        return instance;
    }

    /**
     * NOUVELLE MÉTHODE : Enregistre un nouvel utilisateur en base de données.
     */
    @Override
    public void registerUser(User user) throws SQLException {
        // On aligne la requête SQL sur l'ordre logique : ID, Password, Name, Email, Role
        String sql = "INSERT INTO users (id, password, name, email, role) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getId());


            stmt.setString(2, user.getPwd());


            stmt.setString(3, user.getName());


            stmt.setString(4, user.getEmail());


            stmt.setString(5, user.getRole());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'enregistrement de l'utilisateur: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public java.util.List<User> getUsersByRole(String role) throws SQLException {
        java.util.List<User> users = new java.util.ArrayList<>();
        if (role == null || role.isEmpty()) {
            return users;
        }
        String sql = "SELECT id, password, name, email, role FROM users WHERE role = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ));
                }
            }
        }
        return users;
    }

    public java.util.List<User> getDirectorsWithoutClub() throws SQLException {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT u.id, u.password, u.name, u.email, u.role " +
                "FROM users u " +
                "LEFT JOIN clubs c ON c.manager_id = u.id " +
                "WHERE u.role = 'DIRECTOR' AND c.clubid IS NULL " +
                "ORDER BY u.name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }
        }
        return users;
    }

    @Override
    public java.util.List<User> getCoachesByClub(int clubId) throws SQLException {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT u.id, u.password, u.name, u.email, u.role " +
                "FROM users u JOIN members m ON u.id = m.userid " +
                "WHERE m.clubid = ? AND m.role_in_club = 'COACH'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ));
                }
            }
        }
        return users;
    }

    @Override
    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        String sql = "SELECT id, password, name, email, role FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    String pwd = rs.getString("password");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String role = rs.getString("role");

                    return new User(userId, pwd, name, email, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de base de données lors de la récupération de l'utilisateur: " + e.getMessage());
        }

        return null;
    }

    // --- AUTRES MÉTHODES EXISTANTES ---

    public int getClubIdByCoach(String coachId) {
        String sql = "SELECT clubid FROM members WHERE userid = ? AND role_in_club = 'COACH'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, coachId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("clubid");
            }
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public int getClubIdByMember(String userId) {
        if (userId == null || userId.isBlank()) {
            return -1;
        }
        String sql = "SELECT clubid FROM members WHERE userid = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("clubid");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getClubIdByMember: " + e.getMessage());
        }
        return -1;
    }

    public int getClubIdByDirector(String userId) {
        if (userId == null || userId.isBlank()) {
            return -1;
        }
        String sql = "SELECT clubid FROM clubs WHERE manager_id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("clubid");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getClubIdByDirector: " + e.getMessage());
        }
        return -1;
    }

    public String getDirectorIdByClub(int clubId) {
        if (clubId <= 0) {
            return null;
        }
        String sql = "SELECT manager_id FROM clubs WHERE clubid = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("manager_id");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getDirectorIdByClub: " + e.getMessage());
        }
        return null;
    }

    public java.util.List<User> searchMembersByName(int clubId, String nameQuery) {
        java.util.List<User> users = new java.util.ArrayList<>();
        if (clubId <= 0 || nameQuery == null || nameQuery.isBlank()) {
            return users;
        }
        String sql = "SELECT u.id, u.password, u.name, u.email, u.role " +
                "FROM users u " +
                "JOIN members m ON m.userid = u.id " +
                "WHERE m.clubid = ? AND u.role = 'MEMBER' AND LOWER(u.name) LIKE LOWER(?) " +
                "ORDER BY u.name";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setString(2, "%" + nameQuery.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL searchMembersByName: " + e.getMessage());
        }
        return users;
    }

    public java.util.List<User> getMembersByClub(int clubId) {
        java.util.List<User> users = new java.util.ArrayList<>();
        if (clubId <= 0) {
            return users;
        }
        String sql = "SELECT u.id, u.password, u.name, u.email, u.role " +
                "FROM users u JOIN members m ON u.id = m.userid " +
                "WHERE m.clubid = ? ORDER BY u.name";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("id"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getMembersByClub: " + e.getMessage());
        }
        return users;
    }

    public String getRoleInClub(String userId, int clubId) {
        if (userId == null || userId.isBlank() || clubId <= 0) {
            return "";
        }
        String sql = "SELECT role_in_club FROM members WHERE userid = ? AND clubid = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.trim());
            stmt.setInt(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role_in_club");
                    return role == null ? "" : role;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getRoleInClub: " + e.getMessage());
        }
        return "";
    }

    public boolean hasActiveLicenceForSport(String userId, int sportId) {
        if (userId == null || userId.isBlank() || sportId <= 0) {
            return false;
        }
        String sql = "SELECT 1 FROM licences WHERE membre_id = ? AND sport_id = ? AND statut = 'ACTIVE' LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.trim());
            stmt.setInt(2, sportId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL hasActiveLicenceForSport: " + e.getMessage());
            return false;
        }
    }

    public boolean isMemberInClub(String userId, int clubId) {
        if (userId == null || userId.isBlank() || clubId <= 0) {
            return false;
        }
        String sql = "SELECT 1 FROM members WHERE userid = ? AND clubid = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.trim());
            stmt.setInt(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL isMemberInClub: " + e.getMessage());
        }
        return false;
    }

    public void updateUserRole(String userId, String role) throws SQLException {
        if (userId == null || userId.isBlank() || role == null || role.isBlank()) {
            return;
        }
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
