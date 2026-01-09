package com.sportify.manager.services;

import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.PostgresClubDAO;
import com.sportify.manager.dao.PostgresUserDAO;
import java.sql.Connection;
import java.sql.SQLException;

public class ClubManager {
    private ClubDAO clubDAO;
    private static int clubIDCounter = 1;

    public ClubManager() {
        // Récupérer la connexion depuis PostgresUserDAO
        Connection connection = PostgresUserDAO.getConnection();
        // Créer une instance de PostgresClubDAO en passant la connexion
        clubDAO = new PostgresClubDAO(connection);
    }

    private int generateClubID() {
        return clubIDCounter++;
    }

    // --- CORRECTION : Ajout de int sportId dans les paramètres ---
    public void createClub(String name, String description, int sportId, String type, String meetingSchedule, int maxCapacity) {
        try {
            int clubID = generateClubID();

            // --- CORRECTION : Utilisation des 7 arguments avec les bons noms de variables ---
            Club club = new Club(
                    clubID,          // Utilise clubID (généré plus haut)
                    name,
                    description,
                    sportId,         // Utilise le sportId passé en paramètre
                    type,
                    meetingSchedule,
                    maxCapacity
            );

            // --- CORRECTION : Utilise l'objet 'club' créé juste au-dessus ---
            clubDAO.addClub(club);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du club : " + e.getMessage());
            e.printStackTrace();
        }
    }
}