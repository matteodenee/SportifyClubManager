package com.sportify.manager.controllers;

import com.sportify.manager.frame.LoginFrame;
import com.sportify.manager.services.User;
import com.sportify.manager.services.UserManager;
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


        User user = userDAO.getUserById(id);


        if (user != null && user.getPwd().equals(pwd)) {
            UserManager.createUserManager().login(id, pwd);

            loginFrame.showLoginSuccess(user);
        } else {
            loginFrame.showLoginError();
        }
    }


    public String getDashboardType(User user) {
        if (user == null || user.getRole() == null) return "MEMBER";

        return user.getRole().toUpperCase(); // Retourne "ADMIN", "DIRECTOR", "COACH" ou "MEMBER"
    }


    public boolean canManageClubs(User user) {
        if (user == null || user.getRole() == null) return false;
        String role = user.getRole().toUpperCase();
        return role.equals("ADMIN") || role.equals("DIRECTOR");
    }
}
