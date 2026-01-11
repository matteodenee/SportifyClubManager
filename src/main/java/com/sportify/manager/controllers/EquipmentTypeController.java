package com.sportify.manager.controllers;

import com.sportify.manager.facade.EquipmentTypeFacade;
import com.sportify.manager.services.EquipmentType;
import com.sportify.manager.services.EquipmentTypeActionResult;
import java.util.List;

public class EquipmentTypeController {
    private final EquipmentTypeFacade facade = EquipmentTypeFacade.getInstance();

    public EquipmentTypeActionResult handleCreate(String name, String description) {
        return facade.createEquipmentType(name, description);
    }

    public EquipmentTypeActionResult handleUpdate(EquipmentType existing, String name, String description) {
        return facade.updateEquipmentType(existing, name, description);
    }

    public EquipmentTypeActionResult handleDelete(int id) {
        return facade.deleteEquipmentType(id);
    }

    public List<EquipmentType> handleListAll() {
        return facade.listAllEquipmentTypes();
    }
}
