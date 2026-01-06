package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.ClubDAO;

public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    public static AbstractFactory createFactory() {
        if (instance == null) {
            instance = new PostgresFactory();  // Création d'une instance de PostgresFactory
        }
        return instance;
    }

    // Méthode pour créer UserDAO
    public abstract UserDAO createUserDAO();

    // Méthode pour créer ClubDAO
    public abstract ClubDAO createClubDAO();
}
