package com.sportify.manager.facade;

import com.sportify.manager.services.UserManager;

public class RegisterFacade {

    private static RegisterFacade instance;
    private final UserManager userManager;

    private RegisterFacade() {
        this.userManager = UserManager.createUserManager();
    }

    public static RegisterFacade createRegisterFacade() {
        if (instance == null) {
            instance = new RegisterFacade();
        }
        return instance;
    }

    /**
     * Registers a new user using the name/pwd signature from the diagram.
     * @param name user name or identifier
     * @param password plain password
     * @return true if persisted
     */
    public boolean register(String name, String password) {
        return userManager.createUser(name, password);
    }

    /**
     * Internal overload to keep the id when provided by the UI.
     * @param id technical id / username
     * @param name display name (falls back to id if empty)
     * @param password plain password
     * @param email optional email
     * @param role role to assign
     * @return true if persisted
     */
    public boolean registerWithId(String id, String name, String password, String email, String role) {
        return userManager.createUser(id, name, password, email, role);
    }
}
