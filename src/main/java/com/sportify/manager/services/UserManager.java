package com.sportify.manager.services;

import com.sportify.manager.persistence.AbstractFactory;
import com.sportify.manager.dao.UserDAO;


public class UserManager {
    private static  UserManager um = null;
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

    public User login( String id,String pwd){
        AbstractFactory f = AbstractFactory.getFactory();
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(id);
        if (user != null){
            if (pwd != null && pwd.equals(user.getPwd())) {
                this.currentUser = user;
            }
            else {
                this.currentUser = null;
            }
        }
        return this.currentUser ;
        
    }

    
}
