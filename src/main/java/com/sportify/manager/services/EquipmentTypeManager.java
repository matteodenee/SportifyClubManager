package com.sportify.manager.services;

import com.sportify.manager.dao.EquipmentTypeDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.util.Collections;
import java.util.List;

public class EquipmentTypeManager {
    private static EquipmentTypeManager instance;
    private final EquipmentTypeDAO equipmentTypeDAO;

    private EquipmentTypeManager() {
        this.equipmentTypeDAO = AbstractFactory.getFactory().createEquipmentTypeDAO();
    }

    public static synchronized EquipmentTypeManager getInstance() {
        if (instance == null) {
            instance = new EquipmentTypeManager();
        }
        return instance;
    }

    public EquipmentTypeActionResult createEquipmentType(String name, String description) {
        String cleanedName = normalize(name);
        if (cleanedName.isEmpty()) {
            return EquipmentTypeActionResult.invalid("Nom obligatoire.");
        }
        if (equipmentTypeDAO.existsByName(cleanedName)) {
            return EquipmentTypeActionResult.duplicate("Nom deja existant.");
        }
        EquipmentType type = new EquipmentType(cleanedName, normalizeDescription(description));
        boolean created = equipmentTypeDAO.create(type);
        return created
                ? EquipmentTypeActionResult.success("Type d'equipement cree.")
                : EquipmentTypeActionResult.error("Creation impossible.");
    }

    public EquipmentTypeActionResult updateEquipmentType(EquipmentType existing, String name, String description) {
        if (existing == null || existing.getId() == null) {
            return EquipmentTypeActionResult.invalid("Selection invalide.");
        }
        String cleanedName = normalize(name);
        if (cleanedName.isEmpty()) {
            return EquipmentTypeActionResult.invalid("Nom obligatoire.");
        }
        boolean nameChanged = existing.getName() == null || !existing.getName().equalsIgnoreCase(cleanedName);
        if (nameChanged && equipmentTypeDAO.existsByName(cleanedName)) {
            return EquipmentTypeActionResult.duplicate("Nom deja existant.");
        }
        EquipmentType updated = new EquipmentType(existing.getId(), cleanedName, normalizeDescription(description));
        boolean ok = equipmentTypeDAO.update(updated);
        return ok
                ? EquipmentTypeActionResult.success("Type d'equipement modifie.")
                : EquipmentTypeActionResult.error("Modification impossible.");
    }

    public EquipmentTypeActionResult deleteEquipmentType(int id) {
        if (id <= 0) {
            return EquipmentTypeActionResult.invalid("Selection requise.");
        }
        if (equipmentTypeDAO.isTypeUsed(id)) {
            return EquipmentTypeActionResult.inUse("Suppression impossible : type utilise.");
        }
        boolean deleted = equipmentTypeDAO.delete(id);
        return deleted
                ? EquipmentTypeActionResult.success("Type d'equipement supprime.")
                : EquipmentTypeActionResult.error("Suppression impossible.");
    }

    public List<EquipmentType> listAll() {
        List<EquipmentType> types = equipmentTypeDAO.listAll();
        return types != null ? types : Collections.emptyList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private String normalizeDescription(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
