package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.PostgresClubDAO;  // Import de PostgresClubDAO

import java.sql.Connection;  // Import de Connection

public class PostgresFactory extends AbstractFactory {

    // Constructeur
    public PostgresFactory() {
    }

    // Méthode pour obtenir l'instance unique de PostgresFactory (Singleton)
    public static PostgresFactory getInstance() {
        return new PostgresFactory();
    }

    // Méthode pour obtenir un UserDAO en utilisant le Singleton de PostgresUserDAO
    @Override
    public UserDAO createUserDAO() {
        // Utiliser la méthode getInstance() pour obtenir l'instance unique de PostgresUserDAO
        return PostgresUserDAO.getInstance();
    }

    // Méthode pour créer un ClubDAO en utilisant PostgresClubDAO
    @Override
    public ClubDAO createClubDAO() {
        // 1. On récupère la connexion unique
        Connection connection = PostgresUserDAO.getConnection();

        // 2. On la transmet au nouveau DAO
        // Cette ligne ne sera plus en erreur car le constructeur accepte désormais 'connection'
        return new PostgresClubDAO(connection);
    }
}