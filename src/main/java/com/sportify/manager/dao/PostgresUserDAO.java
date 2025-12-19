package com.sportify.manager.dao;

import com.sportify.manager.services.User;
import java.sql.*;



public class PostgresUserDAO extends UserDAO {

    // Paramètres de connexion par défaut
    private static final String DEFAULT_URL = "jdbc:postgresql://162.38.112.60:5432/erben-db";
    private static final String DEFAULT_USER = "rasim.erben";
    private static final String DEFAULT_PASSWORD = "erben2024!";

    private final String url;
    private final String user;
    private final String password;

    // Instance unique de PostgresUserDAO
    private static PostgresUserDAO instance;

    // Connexion à la base de données (partagée entre toutes les demandes)
    private static Connection connection;

    // Constructeur privé pour empêcher la création d'instances en dehors de la classe
    private PostgresUserDAO() {
        this.url = (System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty()) ? System.getenv("DB_URL") : DEFAULT_URL;
        this.user = (System.getenv("DB_USER") != null && !System.getenv("DB_USER").isEmpty()) ? System.getenv("DB_USER") : DEFAULT_USER;
        this.password = (System.getenv("DB_PASSWORD") != null && !System.getenv("DB_PASSWORD").isEmpty()) ? System.getenv("DB_PASSWORD") : DEFAULT_PASSWORD;

        try {
            // Charger le driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            // Si la connexion n'existe pas encore, la créer
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur lors de la connexion à PostgreSQL: " + e.getMessage());
        }
    }

    // Méthode publique pour obtenir l'instance unique de PostgresUserDAO (Singleton)
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

        String sql = "SELECT id, password FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    String pwd = rs.getString("password");
                    return new User(userId, pwd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de base de données lors de la récupération de l'utilisateur: " + e.getMessage());
        }

        return null;
    }

    // Méthode pour obtenir la connexion partagée
    public static Connection getConnection() {
        return connection;
    }

    // Méthode pour fermer la connexion à la base de données
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
