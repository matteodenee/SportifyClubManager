package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.dao.ClubDAO;
import com.sportify.manager.dao.PostgresClubDAO;
import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.dao.PostgresStatDAO;
import com.sportify.manager.dao.LicenceDAO;
import com.sportify.manager.dao.PostgresLicenceDAO;

import java.sql.Connection;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {
        // default
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
}
