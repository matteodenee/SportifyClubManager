package com.sportify.manager.facade;

import com.sportify.manager.services.UserManager;

public class RegisterFacade {

    private static RegisterFacade instance;
    private final UserManager userManager;

    private RegisterFacade() {

        this.userManager = UserManager.createUserManager();
    }

    public static synchronized RegisterFacade createRegisterFacade() {
        if (instance == null) {
            instance = new RegisterFacade();
        }
        return instance;
    }


    public boolean register(String name, String password) {

        return registerWithId(name, name, password, "", "MEMBER");
    }


    public boolean registerWithId(String id, String name, String password, String email, String role) {

        if (id == null || id.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }


        return userManager.createUser(id, name, password, email, role);
    }
}