package com.sportify.manager.services;

import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.PostgresClubDAO;  // DAO spécifique pour la base de données
import com.sportify.manager.dao.PostgresUserDAO;  // Import de PostgresUserDAO pour la connexion
import java.sql.Connection;
import java.sql.SQLException;

public class ClubManager {
    private ClubDAO clubDAO;
    private static int clubIDCounter = 1;  // Compteur pour générer des IDs uniques

    public ClubManager() {
        // Récupérer la connexion depuis PostgresUserDAO
        Connection connection = PostgresUserDAO.getConnection();

        // Créer une instance de PostgresClubDAO en passant la connexion
        clubDAO = new PostgresClubDAO(connection);
    }

    // Générateur d'ID pour les clubs
    private int generateClubID() {
        return clubIDCounter++;  // Incrémente l'ID à chaque fois qu'un nouveau club est créé
    }

    // Méthode pour créer un club
    public void createClub(String name, String description, String type, String meetingSchedule, int maxCapacity) {
        try {
            // Utiliser le générateur d'ID pour créer un ID unique pour chaque club
            int clubID = generateClubID();

            // Créer une nouvelle instance de Club avec 6 arguments
            Club newClub = new Club(clubID, name, description, type, meetingSchedule, maxCapacity);

            // Ajouter le club à la base de données via le DAO
            clubDAO.addClub(newClub);  // Cette méthode peut lancer une SQLException

        } catch (SQLException e) {
            // Gérer l'exception SQLException si elle est lancée
            System.err.println("Erreur lors de l'ajout du club : " + e.getMessage());
            e.printStackTrace();  // Afficher la trace de l'exception pour déboguer
        }
    }

    // Vous pouvez ajouter d'autres méthodes pour modifier et supprimer des clubs
}
