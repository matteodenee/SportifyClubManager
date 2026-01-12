package com.sportify.manager.dao;

import com.sportify.manager.services.TypeSport;
import java.sql.SQLException;
import java.util.List;

public abstract class TypeSportDAO {


    public abstract TypeSport create(TypeSport typeSport) throws SQLException;


    public abstract List<TypeSport> getAll() throws SQLException;


    public abstract TypeSport getById(int id) throws SQLException;


    public abstract TypeSport getByNom(String nom) throws SQLException;


    public abstract void update(TypeSport typeSport) throws SQLException;


    public abstract void delete(int id) throws SQLException;


    public abstract boolean isUsedByClubs(int id) throws SQLException;
}