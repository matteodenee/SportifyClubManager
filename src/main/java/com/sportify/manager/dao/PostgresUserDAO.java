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
            // Index 1 : ID
            stmt.setString(1, user.getId());

            // Index 2 : Password (Vérifie bien que user.getPwd() renvoie le mot de passe !)
            stmt.setString(2, user.getPwd());

            // Index 3 : Name
            stmt.setString(3, user.getName());

            // Index 4 : Email
            stmt.setString(4, user.getEmail());

            // Index 5 : Role
            stmt.setString(5, user.getRole());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'enregistrement de l'utilisateur: " + e.getMessage());
            throw e;
        }
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
        System.out.println("🔍 getClubIdByCoach(" + coachId + ")");
        String sql = "SELECT clubid FROM members WHERE userid = ? AND role_in_club = 'COACH'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, coachId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int clubId = rs.getInt("clubid");
                System.out.println("✅ Club trouvé: " + clubId);
                return clubId;
            }
            System.out.println("❌ Aucun club trouvé pour ce coach");
            return -1;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            return -1;
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