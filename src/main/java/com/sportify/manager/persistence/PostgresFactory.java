package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;
import java.sql.Connection;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {
        // Constructeur vide
    }

    @Override
    public UserDAO createUserDAO() {
        // Utilise ta méthode Singleton existante
        return PostgresUserDAO.getInstance();
    }

    @Override
    public ClubDAO createClubDAO() {
        // Récupère la connexion partagée via PostgresUserDAO
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresClubDAO(connection);
    }

    @Override
    public StatDAO createStatDAO() {
        // Récupère la connexion partagée
        Connection connection = PostgresUserDAO.getConnection();
        return new PostgresStatDAO(connection);
    }

    @Override
    public LicenceDAO createLicenceDAO() {
        // Intégration du module de ton ami
        // Récupère la connexion partagée
        Connection connection = PostgresUserDAO.getConnection();
        // Retourne l'implémentation concrète pour PostgreSQL
        return new PostgresLicenceDAO(connection);
    }
}