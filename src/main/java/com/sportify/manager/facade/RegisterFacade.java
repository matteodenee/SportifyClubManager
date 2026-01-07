package com.sportify.manager.facade;

import com.sportify.manager.services.UserManager;

public class RegisterFacade {

    private static RegisterFacade instance;
    private final UserManager userManager;

    private RegisterFacade() {
        // UserManager gérera l'appel au UserDAO et à PostgreSQL
        this.userManager = UserManager.createUserManager();
    }

    public static synchronized RegisterFacade createRegisterFacade() {
        if (instance == null) {
            instance = new RegisterFacade();
        }
        return instance;
    }

    /**
     * Méthode simplifiée (souvent utilisée pour des tests ou des accès rapides)
     */
    public boolean register(String name, String password) {
        // On génère un ID basé sur le nom pour cette version simplifiée
        return registerWithId(name, name, password, "", "MEMBER");
    }

    /**
     * Méthode complète utilisée par ton RegisterFrame
     * @return true si l'utilisateur est bien enregistré en base PostgreSQL
     */
    public boolean registerWithId(String id, String name, String password, String email, String role) {
        // 1. Validation de base côté Facade
        if (id == null || id.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // 2. On délègue au UserManager qui contient la logique métier
        // Le UserManager vérifiera via le DAO si l'ID ou l'Email existe déjà
        return userManager.createUser(id, name, password, email, role);
    }
}