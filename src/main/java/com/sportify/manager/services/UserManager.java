package com.sportify.manager.services;

import com.sportify.manager.persistence.AbstractFactory;
import com.sportify.manager.dao.UserDAO;
import java.sql.SQLException;

public class UserManager {
    private static UserManager um = null;
    private User currentUser;

    private UserManager(){
        this.currentUser = null;
    }

    public static UserManager createUserManager(){
        if (um == null){
            um = new UserManager();
        }
        return um;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // --- LOGIQUE DE CONNEXION ---
    public User login(String id, String pwd){
        AbstractFactory f = AbstractFactory.getFactory();
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(id);
        if (user != null){
            // Note: Vérifie si ton modèle User utilise getPwd() ou getPassword()
            if (pwd != null && pwd.equals(user.getPwd())) {
                this.currentUser = user;
            } else {
                this.currentUser = null;
            }
        }
        return this.currentUser;
    }

    // --- NOUVELLE MÉTHODE : LOGIQUE D'INSCRIPTION ---
    // --- DANS UserManager.java ---
    public boolean createUser(String id, String name, String password, String email, String role) {
        try {
            AbstractFactory f = AbstractFactory.getFactory();
            UserDAO udao = f.createUserDAO();

            if (udao.getUserById(id) != null) {
                return false;
            }

            // CORRECTION DE L'ORDRE : (id, password, name, email, role)
            // On place 'password' en deuxième argument pour correspondre au constructeur
            User newUser = new User(id, password, name, email, role);

            udao.registerUser(newUser);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Overload pour la signature simple (utilisée parfois par la Facade)
    public boolean createUser(String name, String password) {
        return createUser(name, name, password, "", "MEMBER");
    }
}
