package com.sportify.manager.dao;

import com.sportify.manager.services.User;

public interface UserDAO {

    User getUserById(String id);

    boolean create(User user);
}
