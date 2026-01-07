package com.sportify.manager.dao;

import com.sportify.manager.services.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUserDAO implements UserDAO {

    private static final String DEFAULT_URL = "jdbc:postgresql://162.38.112.60:5432/erben-db";
    private static final String DEFAULT_USER = "rasim.erben";
    private static final String DEFAULT_PASSWORD = "erben2024!";

    private final String url;
    private final String user;
    private final String password;

    private static PostgresUserDAO instance;
    private static Connection connection;

    private PostgresUserDAO() {
        this.url = envOrDefault("DB_URL", DEFAULT_URL);
        this.user = envOrDefault("DB_USER", DEFAULT_USER);
        this.password = envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        try {
            Class.forName("org.postgresql.Driver");
            ensureConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur lors de la connexion à PostgreSQL: " + e.getMessage());
        }
    }

    public static synchronized PostgresUserDAO getInstance() {
        if (instance == null) {
            instance = new PostgresUserDAO();
        } else {
            try {
                instance.ensureConnection();
            } catch (SQLException e) {
                System.err.println("Erreur lors du contrôle de la connexion PostgreSQL: " + e.getMessage());
            }
        }
        return instance;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    private String envOrDefault(String key, String defaultValue) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : defaultValue;
    }

    /**
     * Récupère le club_id associé à un coach (table members).
     */
    public int getClubIdByCoach(String coachId) {
        String sql = "SELECT clubid FROM members WHERE userid = ? AND role_in_club = 'COACH'";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, coachId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("clubid");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du club du coach: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public User getUserById(String id) {
        String sql = "SELECT id, password, name, email, role FROM users WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(User user) {
        String sql = "INSERT INTO users (id, password, name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Connection getConnection() {
        try {
            if (instance == null) {
                getInstance();
            } else if (connection == null || connection.isClosed()) {
                instance.ensureConnection();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la connexion PostgreSQL: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
