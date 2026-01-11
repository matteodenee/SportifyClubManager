package com.sportify.manager.dao;

import com.sportify.manager.services.Equipment;
import java.util.List;

public interface EquipmentDAO {
    boolean insert(Equipment equipment);
    Equipment findByName(String name);
    Equipment findById(int id);
    boolean updateQuantity(int id, int newQty);
    List<Equipment> listAll();
}
