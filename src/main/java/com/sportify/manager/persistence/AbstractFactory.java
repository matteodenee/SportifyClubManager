package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.dao.LicenceDAO;
import com.sportify.manager.dao.TypeSportDAO; // Ajout de l'import pour le module sport

public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    public static AbstractFactory getFactory() {
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    public abstract UserDAO createUserDAO();
    public abstract ClubDAO createClubDAO();
    public abstract StatDAO createStatDAO();
    public abstract LicenceDAO createLicenceDAO();
    public abstract TypeSportDAO createTypeSportDAO();
}