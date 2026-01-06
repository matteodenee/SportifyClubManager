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

    @Override
    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        // MISE À JOUR : Sélection de toutes les colonnes nécessaires selon le diagramme de classes
        String sql = "SELECT id, password, name, email, role FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extraction des nouvelles données
                    String userId = rs.getString("id");
                    String pwd = rs.getString("password");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String role = rs.getString("role");

                    // Retourne l'objet User complet (assurez-vous d'avoir mis à jour User.java avant)
                    return new User(userId, pwd, name, email, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de base de données lors de la récupération de l'utilisateur: " + e.getMessage());
        }

        return null;
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