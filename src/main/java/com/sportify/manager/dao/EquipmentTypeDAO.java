package com.sportify.manager.dao;

import com.sportify.manager.services.EquipmentType;
import java.util.List;

public interface EquipmentTypeDAO {

    boolean create(EquipmentType type);

    boolean update(EquipmentType type);

    boolean delete(int id);

    List<EquipmentType> listAll();

    boolean existsByName(String name);

    boolean isTypeUsed(int typeId);
}
