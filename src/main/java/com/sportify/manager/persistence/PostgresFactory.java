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

    /**
     * Nouvelle implémentation pour fabriquer le DAO de ton ami
     */
    @Override
    public TypeSportDAO createTypeSportDAO() {
        // On récupère la connexion partagée comme pour les autres DAOs
        Connection connection = PostgresUserDAO.getConnection();
        // On retourne l'implémentation concrète de ton ami
        return new PostgresTypeSportDAO(connection);
    }
}