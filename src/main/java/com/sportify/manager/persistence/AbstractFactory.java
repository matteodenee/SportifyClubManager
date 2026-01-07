package com.sportify.manager.persistence;

import com.sportify.manager.dao.UserDAO;


public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    /**
     * Singleton accessor returning the concrete factory.
     * @return factory instance
     */
    public static AbstractFactory createFactory() {
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    /**
     * Creates a UserDAO linked to the current persistence backend.
     * @return DAO instance
     */
    public abstract UserDAO createUserDAO();
}
