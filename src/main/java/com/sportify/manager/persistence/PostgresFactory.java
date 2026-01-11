package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;
import java.sql.Connection;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {
        // Constructeur vide
    }

    @Override
    public UserDAO createUserDAO() {
        return PostgresUserDAO.getInstance();
    }

    @Override
    public ClubDAO createClubDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresClubDAO(connection);
    }

    @Override
    public StatDAO createStatDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresStatDAO(connection);
    }

    @Override
    public LicenceDAO createLicenceDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresLicenceDAO(connection);
    }

    @Override
    public TypeSportDAO createTypeSportDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresTypeSportDAO(connection);
    }

    // --- LES DEUX MÉTHODES MANQUANTES QUI CAUSENT L'ERREUR ---

    @Override
    public MatchDAO createMatchDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresMatchDAO(connection);
    }

    @Override
    public CompositionDAO createCompositionDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresCompositionDAO(connection);
    }

    @Override
    public MatchRequestDAO createMatchRequestDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresMatchRequestDAO(connection);
    }

    @Override
    public EquipmentTypeDAO createEquipmentTypeDAO() {
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresEquipmentTypeDAO(connection);
    }
}
