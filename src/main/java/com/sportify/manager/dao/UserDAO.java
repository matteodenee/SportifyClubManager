package com.sportify.manager.dao;

import com.sportify.manager.services.User;
import java.sql.SQLException;

public abstract class UserDAO {

    // Méthode existante pour la connexion
    public abstract User getUserById(String id);

    // Nouvelle méthode pour l'inscription
    public abstract void registerUser(User user) throws SQLException;

    public abstract java.util.List<User> getUsersByRole(String role) throws SQLException;
    public abstract java.util.List<User> getCoachesByClub(int clubId) throws SQLException;

}
