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


    public void createClub(String name, String description, int sportId, String type, int maxCapacity, String managerId) {
        try {
            int clubID = generateClubID();


            Club club = new Club(
                    clubID,
                    name,
                    description,
                    sportId,
                    type,
                    maxCapacity,
                    managerId
            );


            clubDAO.addClub(club);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du club : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
