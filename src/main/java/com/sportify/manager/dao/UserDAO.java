package com.sportify.manager.dao;

import com.sportify.manager.services.User;



public abstract class UserDAO {

    public abstract User getUserById(String id);

}
