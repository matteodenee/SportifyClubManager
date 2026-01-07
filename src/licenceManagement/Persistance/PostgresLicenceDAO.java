package licenceManagement.Persistance;

import licenceManagement.Bl.Licence;
import licenceManagement.Bl.dao.LicenceDAO;
import licenceManagement.Enum.StatutLicence;
import licenceManagement.Enum.TypeLicence;
import persistance.AbstractFactory;
import TypeSportManagement.TypeSport;
import UserManagent.User;
import UserManagent.UserDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PostgresLicenceDAO extends LicenceDAO {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/sportifyclub";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private final String url;
    private final String user;
    private final String password;

    public PostgresLicenceDAO() {
        this(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    public PostgresLicenceDAO(String url, String user, String password) {
        this.url = (url == null || url.isEmpty()) ? DEFAULT_URL : url;
        this.user = (user == null || user.isEmpty()) ? DEFAULT_USER : user;
        this.password = (password == null) ? DEFAULT_PASSWORD : password;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found.");
        }
    }

    // =======================
    // CREATE
    // =======================
    @Override
    public void insert(Licence licence) {

        String sql = """
            INSERT INTO licences
            (id, sport, type_licence, statut, date_demande, date_debut, date_fin,
             membre_id, date_decision, commentaire_admin)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licence.getId());
            stmt.setString(2, licence.getSport().getNom());
            stmt.setString(3, licence.getTypeLicence().name());
            stmt.setString(4, licence.getStatut().name());
            stmt.setDate(5, licence.getDateDemande());
            stmt.setDate(6, licence.getDateDebut());
            stmt.setDate(7, licence.getDateFin());
            stmt.setString(8, licence.getMembre().getId());
            stmt.setDate(9, licence.getDateDecision());
            stmt.setString(10, licence.getCommentaireAdmin());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la licence : " + e.getMessage());
        }
    }

    // =======================
    // READ
    // =======================
    @Override
    public Licence findById(String id) {

        String sql = "SELECT * FROM licences WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLicence(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation de la licence : " + e.getMessage());
        }

        return null;
    }

    // =======================
    // UPDATE
    // =======================
    @Override
    public void update(Licence licence) {

        String sql = """
            UPDATE licences SET
                statut = ?,
                date_debut = ?,
                date_fin = ?,
                date_decision = ?,
                commentaire_admin = ?
            WHERE id = ?
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licence.getStatut().name());
            stmt.setDate(2, licence.getDateDebut());
            stmt.setDate(3, licence.getDateFin());
            stmt.setDate(4, licence.getDateDecision());
            stmt.setString(5, licence.getCommentaireAdmin());
            stmt.setString(6, licence.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la licence : " + e.getMessage());
        }
    }

    // =======================
    // MAPPING
    // =======================
    private Licence mapResultSetToLicence(ResultSet rs) throws SQLException {
        AbstractFactory f = AbstractFactory.getFactory();
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(rs.getString("membre_id"));
    
        return new Licence(
        rs.getString("id"),
        TypeSport.valueOf(rs.getString("sport")),
        TypeLicence.valueOf(rs.getString("type_licence")),
        StatutLicence.valueOf(rs.getString("statut")),
        rs.getDate("date_demande"),
        rs.getDate("date_debut"),
        rs.getDate("date_fin"),
        new User(user.getId(),user.getPwd() ),
        null, // documents via DAO séparée
        rs.getDate("date_decision"),
        rs.getString("commentaire_admin")
    );
    }



    @Override
    public List<Licence> findByMembre(String membreId) {

        List<Licence> licences = new java.util.ArrayList<>();

        String sql = "SELECT * FROM licences WHERE membre_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membreId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licences.add(mapResultSetToLicence(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des licences du membre : " + e.getMessage());
        }

        return licences;
    }

    @Override
    public List<Licence> findByStatut(StatutLicence statut) {

        List<Licence> licences = new java.util.ArrayList<>();

        String sql = "SELECT * FROM licences WHERE statut = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licences.add(mapResultSetToLicence(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des licences par statut : " + e.getMessage());
        }

        return licences;
    }


    @Override
    public void delete(String id) {

        String sql = "DELETE FROM licences WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la licence : " + e.getMessage());
        }
    }

}
