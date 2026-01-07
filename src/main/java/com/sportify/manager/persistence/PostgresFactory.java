package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.dao.PostgresUserDAO;

public class PostgresFactory extends AbstractFactory {

    public PostgresFactory() {
    }

    @Override
    public UserDAO createUserDAO() {
        return new PostgresUserDAO();
    }
}
