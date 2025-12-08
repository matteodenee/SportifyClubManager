package SportifyClubManager.src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUserDAO extends UserDAO {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/sportifyclub";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private final String url;
    private final String user;
    private final String password;

    public PostgresUserDAO() {
        this(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    public PostgresUserDAO(String url, String user, String password) {
        this.url = (url == null || url.isEmpty()) ? DEFAULT_URL : url;
        this.user = (user == null || user.isEmpty()) ? DEFAULT_USER : user;
        this.password = (password == null) ? DEFAULT_PASSWORD : password;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Driver not present; connection attempt will fail but we avoid a hard crash here.
            System.err.println("PostgreSQL JDBC driver not found on classpath.");
        }
    }

    @Override
    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        String sql = "SELECT id, password FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    String pwd = rs.getString("password");
                    return new User(userId, pwd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de base de donnees lors de la recuperation de l'utilisateur: " + e.getMessage());
        }

        return null;
    }
}
