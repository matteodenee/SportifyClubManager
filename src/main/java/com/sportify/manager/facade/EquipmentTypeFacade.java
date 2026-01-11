package com.sportify.manager.facade;

import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import com.sportify.manager.services.EquipmentTypeManager;
import java.util.List;

public class EquipmentTypeFacade {
    private static EquipmentTypeFacade instance;
    private final EquipmentTypeManager manager;

    private EquipmentTypeFacade() {
        this.manager = EquipmentTypeManager.getInstance();
    }

    public static synchronized EquipmentTypeFacade getInstance() {
        if (instance == null) {
            instance = new EquipmentTypeFacade();
        }
        return instance;
    }

    public EquipmentTypeActionResult createEquipmentType(String name, String description) {
        return manager.createEquipmentType(name, description);
    }

    public EquipmentTypeActionResult updateEquipmentType(EquipmentType existing, String name, String description) {
        return manager.updateEquipmentType(existing, name, description);
    }

    public EquipmentTypeActionResult deleteEquipmentType(int id) {
        return manager.deleteEquipmentType(id);
    }

    public List<EquipmentType> listAllEquipmentTypes() {
        return manager.listAll();
    }
}
