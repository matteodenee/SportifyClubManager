package com.sportify.manager.services;

import com.sportify.manager.dao.UserDAO;
import com.sportify.manager.persistence.AbstractFactory;

import java.util.regex.Pattern;

public class UserManager {
    private static UserManager um = null;
    private static final String DEFAULT_ROLE = "MEMBER";
    private User currentUser;

    private UserManager() {
        this.currentUser = null;
    }

    public static UserManager createUserManager() {
        if (um == null) {
            um = new UserManager();
        }
        return um;
    }

    /**
     * Authenticates a user.
     * @param id user identifier
     * @param pwd plain password
     * @return User when credentials are valid, null otherwise
     */
    public User login(String id, String pwd) {
        AbstractFactory f = AbstractFactory.getFactory();
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(id);
        if (user != null) {
            if (pwd != null && pwd.equals(user.getPassword())) {
                this.currentUser = user;
            } else {
                this.currentUser = null;
            }
        }
        return this.currentUser;

    }

    /**
     * Diagram signature: register with name + password, id will equal name.
     * @param name username or display name
     * @param password plain password
     * @return true if persisted
     */
    public boolean createUser(String name, String password) {
        return createUser(name, name, password, "", DEFAULT_ROLE);
    }

    /**
     * Registers a user while preserving a dedicated id.
     * @param id user identifier
     * @param name display name (falls back to id)
     * @param password plain password
     * @return true when the DAO stores the user
     */
    public boolean createUser(String id, String name, String password) {
        return createUser(id, name, password, "", DEFAULT_ROLE);
    }

    /**
     * Registers a user with full details.
     * @param id user identifier
     * @param name display name (falls back to id)
     * @param password plain password
     * @param email optional email
     * @param role role to assign
     * @return true when the DAO stores the user
     */
    public boolean createUser(String id, String name, String password, String email, String role) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        if (password == null || !validatePassword(password)) {
            return false;
        }

        AbstractFactory f = AbstractFactory.createFactory();
        UserDAO udao = f.createUserDAO();

        if (udao.getUserById(id) != null) {
            return false;
        }

        String safeName = (name == null || name.isEmpty()) ? id : name;
        String safeRole = (role == null || role.isEmpty()) ? DEFAULT_ROLE : role;
        String safeEmail = (email == null) ? "" : email;

        User newUser = new User(id, safeName, password, safeEmail, safeRole);

        return udao.create(newUser);
    }

    /**
     * Simple password policy: 8 chars, 1 upper, 1 lower, 1 digit.
     * @param pwd password to validate
     * @return true when valid
     */
    public boolean validatePassword(String pwd) {
        if (pwd == null || pwd.length() < 8) {
            return false;
        }
        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        return upper.matcher(pwd).find() && lower.matcher(pwd).find() && digit.matcher(pwd).find();
    }
}
