package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.dao.LicenceDAO; // Ajout de l'import

public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    /**
     * Singleton accessor returning the concrete factory.
     * @return factory instance
     */
    public static AbstractFactory getFactory() {
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    /**
     * Backward-compatible alias.
     */
    public static AbstractFactory createFactory() {
        return getFactory();
    }

    /**
     * Creates a UserDAO linked to the current persistence backend.
     * @return DAO instance
     */
    public abstract UserDAO createUserDAO();
    public abstract ClubDAO createClubDAO();
    public abstract StatDAO createStatDAO();
    public abstract LicenceDAO createLicenceDAO(); // Nouvelle méthode
}
