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

        // Récupère l'utilisateur complet (avec son rôle, nom, etc.) depuis le DAO
        User user = userDAO.getUserById(id);

        // Vérification des identifiants
        if (user != null && user.getPwd().equals(pwd)) {
            // On informe la vue du succès
            loginFrame.showLoginSuccess(user);

            // Note : La redirection vers openClubManagementFrame()
            // est déjà gérée à l'intérieur de loginFrame.showLoginSuccess(user)
            // dans votre version actuelle de LoginFrame.
        } else {
            loginFrame.showLoginError();
        }
    }

    /**
     * Vérifie si l'utilisateur a les droits d'accès à la gestion des clubs.
     * Selon vos diagrammes, l'Admin et le Director ont ces droits.
     */
    public boolean canManageClubs(User user) {
        if (user == null || user.getRole() == null) return false;

        String role = user.getRole().toUpperCase();
        return role.equals("ADMIN") || role.equals("DIRECTOR");
    }
}