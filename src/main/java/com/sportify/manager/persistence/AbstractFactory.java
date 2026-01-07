package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.dao.LicenceDAO; // Ajout de l'import

public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    public static AbstractFactory getFactory() { // Note: j'ai utilisé getFactory pour la cohérence
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    public abstract UserDAO createUserDAO();
    public abstract ClubDAO createClubDAO();
    public abstract StatDAO createStatDAO();
    public abstract LicenceDAO createLicenceDAO(); // Nouvelle méthode
}