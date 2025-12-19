package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.PostgresUserDAO;


public class PostgresFactory extends AbstractFactory {

    // Constructeur
    public PostgresFactory() {}

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
}

