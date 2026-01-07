package com.sportify.manager.persistence;

import com.sportify.manager.dao.*;
import java.sql.Connection;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {
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
        // On récupère la connexion partagée via ton PostgresUserDAO
        Connection connection = PostgresUserDAO.getConnection();
        // On instancie le DAO des statistiques créé à l'étape 2
        return new PostgresStatDAO(connection);
    }
}