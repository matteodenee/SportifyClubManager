package com.sportify.manager.controllers;

import com.sportify.manager.frame.LoginFrame;
import com.sportify.manager.services.User;
import com.sportify.manager.dao.PostgresUserDAO;

public class LoginController {

    private LoginFrame loginFrame;
    private final PostgresUserDAO userDAO = PostgresUserDAO.getInstance();

    public void setLoginFrame(LoginFrame frame) {
        this.loginFrame = frame;
    }

    public void onClick(String id, String pwd) {
        if (loginFrame == null) {
            return;
        }

        // Récupère l'utilisateur complet depuis le DAO
        User user = userDAO.getUserById(id);

        // Vérification des identifiants
        if (user != null && user.getPwd().equals(pwd)) {
            // ÉTAPE 1 : On informe la vue du succès.
            // La LoginFrame devra maintenant gérer les différents rôles dans sa méthode showLoginSuccess.
            loginFrame.showLoginSuccess(user);
        } else {
            loginFrame.showLoginError();
        }
    }

    /**
     * Détermine quel type de tableau de bord l'utilisateur doit voir.
     */
    public String getDashboardType(User user) {
        if (user == null || user.getRole() == null) return "MEMBER";

        return user.getRole().toUpperCase(); // Retourne "ADMIN", "DIRECTOR", "COACH" ou "MEMBER"
    }

    /**
     * Vérifie si l'utilisateur a les droits d'accès à la gestion des clubs (Director/Admin).
     */
    public boolean canManageClubs(User user) {
        if (user == null || user.getRole() == null) return false;
        String role = user.getRole().toUpperCase();
        return role.equals("ADMIN") || role.equals("DIRECTOR");
    }
}