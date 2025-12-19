package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;


public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    public static AbstractFactory createFactory() {
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    public abstract UserDAO createUserDAO();
}
